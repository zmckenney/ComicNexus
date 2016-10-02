package com.zacmckenney.comicnexus.widget;

import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Binder;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;
import android.widget.AdapterView;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zacmckenney.comicnexus.BuildConfig;
import com.zacmckenney.comicnexus.NewComicsFragment;
import com.zacmckenney.comicnexus.R;
import com.zacmckenney.comicnexus.data.ComicColumns;
import com.zacmckenney.comicnexus.data.ComicProvider;
import com.zacmckenney.comicnexus.data.Utils;
import com.zacmckenney.comicnexus.models.NewComic;
import com.zacmckenney.comicnexus.parsers.NewComicParser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Zac on 9/16/16.
 */

public class ComicWidgetRemoteViewsService extends RemoteViewsService {

    //Used for querying the database
    private static final String[] COMIC_COLUMNS = {
            ComicColumns.COMIC_ID,
            ComicColumns.TITLE,
            ComicColumns.ISSUE_NUMBER,
            ComicColumns.PAGE_COUNT,
            ComicColumns.PRICE,
            ComicColumns.WEEK,
            ComicColumns.DAY_OF_YEAR
    };

    // these indices must match the projection
    static final int INDEX_ID = 0;
    static final int INDEX_TITLE = 1;
    static final int INDEX_ISSUE_NUMBER = 2;
    static final int INDEX_PAGE_COUNT = 3;
    static final int INDEX_PRICE = 4;



    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new RemoteViewsFactory() {
            private Cursor data = null;
            private Gson gson;
            private final int THIS_WEEK = 0;
            private int dayOfYear;
            long identityToken;

            private final String PUBLIC_KEY = "644ca5a41a32e5ba84cd6ce566261ddb";
            private final String API_KEY = BuildConfig.API_KEY;
            private String timestamp;
            private String authorizationString;


            @Override
            public void onCreate() {
                if (data != null) {
                    data.close();
                }
            }

            @Override
            public void onDataSetChanged() {
                if (data != null) {
                    data.close();
                }

                //Get day of the year to check against the database
                Calendar rightNow = Calendar.getInstance();
                dayOfYear = rightNow.get(Calendar.DAY_OF_YEAR);
                timestamp = "" + rightNow.getTimeInMillis();

                //initialize the gsonbuilder
                GsonBuilder gsonBuilder = new GsonBuilder();
                gsonBuilder.registerTypeAdapter(NewComic[].class, new NewComicParser());
                gson = gsonBuilder.create();

                //Content Provider export is set to false so clear, then restore calling identity to get data
                identityToken = Binder.clearCallingIdentity();

                //set the data cursor to the current week and day - if its already been updated via the app then
                //data.moveToFirst will be true and its finished.  if data.moveToFirst is false it will request new data for the day
                //the app also checks the day so we dont get concurrent calls
                getDataDayWeek(dayOfYear);
                if (!data.moveToFirst()) {

                    authorizationString = Utils.getAuthorizationKey(timestamp, API_KEY, PUBLIC_KEY);
                    //Well make a synchronous call for the JSON data

                    // These two need to be declared outside the try/catch
                    // so that they can be closed in the finally block.
                    HttpURLConnection urlConnection = null;
                    BufferedReader reader = null;
                    String comicJsonString = null;



                    try {
                        URL url = new URL(NewComicsFragment.ENDPOINT_THIS_WEEK + authorizationString);

                        // Create the request to Marvels API, and open the connection
                        urlConnection = (HttpURLConnection) url.openConnection();
                        urlConnection.setRequestMethod("GET");
                        urlConnection.connect();

                        // Read the input stream into a String
                        InputStream inputStream = urlConnection.getInputStream();
                        StringBuilder buffer = new StringBuilder();
                        if (inputStream == null) {
                            // Nothing to do.
                        }

                        reader = new BufferedReader(new InputStreamReader(inputStream));

                        String line;
                        while ((line = reader.readLine()) != null) {
                            // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                            // But it does make debugging a *lot* easier if you print out the completed
                            // buffer for debugging.
                            buffer.append(line + "\n");
                        }

                        comicJsonString = buffer.toString();
//                        Log.v("@@@JSONSTRING", "JSON string : " + comicJsonString);


                    } catch (IOException e) {
                        Log.e("PlaceholderFragment", "Error ", e);
                    } finally {
                        if (urlConnection != null) {
                            urlConnection.disconnect();
                        }
                        if (reader != null) {
                            try {
                                reader.close();
                            } catch (final IOException e) {
                                Log.e("PlaceholderFragment", "Error closing stream", e);
                            }
                        }
                    }

                    NewComic[] newComicList = gson.fromJson(comicJsonString, NewComic[].class);

                    ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();

                    for (NewComic newComic : newComicList) {
                        batchOperations.add(Utils.buildComicBatchOperation(newComic, THIS_WEEK, dayOfYear));
                    }

                    try {
                        //No need to keep the comics on update - moving next weeks comics to this week would work but in case of changes I think its best to repull the data
                        getBaseContext().getContentResolver().delete(ComicProvider.Comics.COMICS_URI, ComicColumns.WEEK + " LIKE ?", new String[]{String.valueOf(THIS_WEEK)});
                        getBaseContext().getContentResolver().applyBatch(ComicProvider.AUTHORITY, batchOperations);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    } catch (OperationApplicationException e) {
                        e.printStackTrace();
                    }
                    getDataDayWeek(dayOfYear);
                }
                //Restore after all data has been updated
                Binder.restoreCallingIdentity(identityToken);
            }

            @Override
            public void onDestroy() {
                if (data != null) {
                    data.close();
                    data = null;
                }
            }

            @Override
            public int getCount() {
                return data == null ? 0 : data.getCount();
            }

            @Override
            public RemoteViews getViewAt(int i) {
                if (i == AdapterView.INVALID_POSITION ||
                        data == null || !data.moveToPosition(i)) {
                    return null;
                }

                RemoteViews views = new RemoteViews(getPackageName(),
                        R.layout.widget_list_item);

                int comicId = data.getInt(INDEX_ID);
                String title = data.getString(INDEX_TITLE);
                int issueNumber = data.getInt(INDEX_ISSUE_NUMBER);
                int pageCount = data.getInt(INDEX_PAGE_COUNT);
                double price = data.getDouble(INDEX_PRICE);

                views.setTextViewText(R.id.widget_title, title);
                views.setTextViewText(R.id.widget_issue_number, getString(R.string.widget_issue_number, issueNumber));
                views.setTextViewText(R.id.widget_pages, getString(R.string.widget_page_count, pageCount));
                if (price != 0) {
                    views.setTextViewText(R.id.widget_price, getString(R.string.widget_price_detail, price));
                } else {
                    views.setTextViewText(R.id.widget_price, getString(R.string.free));

                }

                Bundle bundle = new Bundle();
                bundle.putInt("id", comicId);
                bundle.putDouble("price", price);

                final Intent fillInIntent = new Intent();
                fillInIntent.putExtra("comicdetails", bundle);
                views.setOnClickFillInIntent(R.id.widget_list_item, fillInIntent);

                return views;
            }

            @Override
            public RemoteViews getLoadingView() {
                return new RemoteViews(getPackageName(), R.layout.widget_list_item);
            }

            @Override
            public int getViewTypeCount() {
                return 1;
            }

            @Override
            public long getItemId(int i) {
                if (data.moveToPosition(i))
                    return data.getInt(0);
                return i;
            }

            @Override
            public boolean hasStableIds() {
                return true;
            }

            private void getDataDayWeek(int dayOfYear){
                data = getContentResolver().query(ComicProvider.Comics.COMICS_URI,
                        COMIC_COLUMNS,
                        ComicColumns.WEEK + " =? AND " + ComicColumns.DAY_OF_YEAR + " =?",
                        new String[] { String.valueOf(THIS_WEEK), String.valueOf(dayOfYear) },
                        null
                );
            }
        };
    }
}
