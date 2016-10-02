package com.zacmckenney.comicnexus;

import android.app.ActivityOptions;
import android.content.ContentProviderOperation;
import android.content.Intent;
import android.content.OperationApplicationException;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.transition.Slide;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zacmckenney.comicnexus.adapters.ComicsGridAdapter;
import com.zacmckenney.comicnexus.data.ComicColumns;
import com.zacmckenney.comicnexus.data.ComicProvider;
import com.zacmckenney.comicnexus.data.Utils;
import com.zacmckenney.comicnexus.models.NewComic;
import com.zacmckenney.comicnexus.parsers.NewComicParser;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zac on 9/9/16.
 */
public class NewComicsFragment extends Fragment{

    //Our Views
    @BindView(R.id.new_comics_grid) GridView gridView;
    @BindView(R.id.comic_release_week) TextView comicWeekReleaseTextview;
    @BindView(R.id.content_main_error) TextView contentMainError;

    //Our Endpoints and necessary keys below
    private static final String ENDPOINT_NEXT_WEEK = "http://gateway.marvel.com/v1/public/comics?formatType=comic&noVariants=true&dateDescriptor=nextWeek&limit=100";
    public static final String ENDPOINT_THIS_WEEK = "http://gateway.marvel.com/v1/public/comics?formatType=comic&noVariants=true&dateDescriptor=thisWeek&limit=100";

    private final String API_KEY = BuildConfig.API_KEY;
    private final String PUBLIC_KEY = "644ca5a41a32e5ba84cd6ce566261ddb";
    private String timestamp;
    private String authorizationKey;

    //Volley and GSON items
    private RequestQueue requestQueue;
    private Gson gson;
    private StringRequest request;

    //For checking the week in the Database and to set the week
    public final int THIS_WEEK = 0;
    public final int NEXT_WEEK = 1;
    private int whatWeek = -1;

    //For our broadcast so our widget updates
    public static final String ACTION_DATA_UPDATED = "com.zacmckenney.marvelnexus.ACTION_DATA_UPDATED";

    //Our adapter
    private ComicsGridAdapter comicGridAdapter;

    //the columns we will query
    private String[] QUERY_COLUMNS = {
            ComicColumns.DAY_OF_YEAR,
            ComicColumns.COMIC_ID,
            ComicColumns.THUMBNAIL_PATH,
            ComicColumns.PRICE,
            ComicColumns.TITLE,
            ComicColumns.WEEK
    };

    //Matching index for ease of use
    private final int INDEX_DAY_OF_YEAR = 0;
    private final int INDEX_ID = 1;
    private final int INDEX_THUMBNAIL = 2;
    private final int INDEX_PRICE = 3;
    private final int INDEX_TITLE = 4;
    private final int INDEX_WEEK = 5;

    //The lists we will use for the adapter
    private NewComic[] newComicList;
    private ArrayList<NewComic> newComicArrayList = new ArrayList<>();

    //To check the database to see if we already have data from today
    Calendar rightNow;
    private int dayOfYear = -1;

    //For animations
    public Bundle animationBundle;

    public NewComicsFragment(){}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //This does all of the heavy lifting for me
        setRetainInstance(true);

        //GSON and Volley setup
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(NewComic[].class, new NewComicParser());
        gson = gsonBuilder.create();

        requestQueue = Volley.newRequestQueue(getContext());

        //For the DB check and API call
        rightNow = Calendar.getInstance();
        dayOfYear = rightNow.get(Calendar.DAY_OF_YEAR);

        //Set a swipe transition into the detail view
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
           getActivity().getWindow().setExitTransition(new Slide(Gravity.LEFT));
        }

    }

    public void fetchThisWeeksComics() {
        whatWeek = THIS_WEEK;

        comicWeekReleaseTextview.setText(R.string.this_week_comics);

        if (!dataUpToDate()) {
            //get a timestamp, then the full auth key with hash, then make the call
            timestamp = "" + rightNow.getTimeInMillis();
            authorizationKey = Utils.getAuthorizationKey(timestamp, API_KEY, PUBLIC_KEY);
            request = new StringRequest(Request.Method.GET, ENDPOINT_THIS_WEEK + authorizationKey, onComicsLoaded, onComicsError);
            requestQueue.add(request);
        }

    }


    public void fetchNextWeeksComics(){
        whatWeek = NEXT_WEEK;

        comicWeekReleaseTextview.setText(R.string.next_week_comics);
        if (!dataUpToDate()) {
            //get a timestamp, then the full auth key with hash, then make the call
            timestamp = "" + rightNow.getTimeInMillis();
            authorizationKey = Utils.getAuthorizationKey(timestamp, API_KEY, PUBLIC_KEY);
            request = new StringRequest(Request.Method.GET, ENDPOINT_NEXT_WEEK + authorizationKey, onComicsLoaded, onComicsError);
            requestQueue.add(request);
        }

    }

    private boolean dataUpToDate() {
        //Query with cursor on whatWeek and which week
        Cursor cursorThisWeek = getContext().getContentResolver().query(ComicProvider.Comics.COMICS_URI, QUERY_COLUMNS, ComicColumns.DAY_OF_YEAR + "= ? AND " + ComicColumns.WEEK + "= ?", new String[]{String.valueOf(dayOfYear), String.valueOf(whatWeek)}, null);

        //If data comes back meaning our week and day matched some in the DB then fill our views
        if (cursorThisWeek.moveToFirst()) {
            newComicArrayList.clear();
            comicGridAdapter.clear();
            while (!cursorThisWeek.isAfterLast()) {
                NewComic cursorNewComic = new NewComic();
                cursorNewComic.setId(cursorThisWeek.getInt(INDEX_ID));
                cursorNewComic.setTitle(cursorThisWeek.getString(INDEX_TITLE));
                cursorNewComic.setPrice(cursorThisWeek.getDouble(INDEX_PRICE));
                cursorNewComic.setThumbnailFromDB(cursorThisWeek.getString(INDEX_THUMBNAIL));

                newComicArrayList.add(cursorNewComic);
                cursorThisWeek.moveToNext();
            }
            cursorThisWeek.close();
            //return true to say that dataUpToDate found current data
            return true;
        } else {
            //return false if there isn't current data
            return false;
        }
    }

    private final Response.Listener<String> onComicsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {

            contentMainError.setVisibility(View.GONE);
            newComicList = gson.fromJson(response, NewComic[].class);

            ArrayList<ContentProviderOperation> batchOperations = new ArrayList<>();
            if (whatWeek == THIS_WEEK) {
                for (NewComic newComic : newComicList) {
                    batchOperations.add(Utils.buildComicBatchOperation(newComic, THIS_WEEK, dayOfYear));
                }
            } else if (whatWeek == NEXT_WEEK){
                for (NewComic newComic : newComicList) {
                    batchOperations.add(Utils.buildComicBatchOperation(newComic, NEXT_WEEK, dayOfYear));
                }
            }
            try {
                //No need to keep the comics on update - moving next weeks comics to this week would work but in case of changes I think its best to repull the data
                getContext().getContentResolver().delete(ComicProvider.Comics.COMICS_URI, ComicColumns.WEEK + " LIKE ?", new String[] { String.valueOf(whatWeek) });
                getContext().getContentResolver().applyBatch(ComicProvider.AUTHORITY, batchOperations);

                updateWidgets();

            } catch (RemoteException e) {
                e.printStackTrace();
            } catch (OperationApplicationException e) {
                e.printStackTrace();
            }

            //Only use this when we've made a new call and have a newComicList already filled to save memory rather than using a cursor
            updateViewData(newComicList);
        }
    };

    public void updateViewData(NewComic[] newComics){
        comicGridAdapter.clear();
        for (NewComic newComicData : newComics){
            comicGridAdapter.add(newComicData);
        }
    }

    private void updateWidgets() {
        // Setting the package ensures that only components in our app will receive the broadcast
        Intent dataUpdatedIntent = new Intent(ACTION_DATA_UPDATED)
                .setPackage(getContext().getPackageName());
        getContext().sendBroadcast(dataUpdatedIntent);
    }


    private final Response.ErrorListener onComicsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            //Toast message to notify of error
            Toast.makeText(getContext(), R.string.error_get_response, Toast.LENGTH_LONG).show();

            Cursor cursorThisWeek = getContext().getContentResolver().query(ComicProvider.Comics.COMICS_URI, new String[]{ ComicColumns.COMIC_ID, ComicColumns.THUMBNAIL_PATH, ComicColumns.PRICE, ComicColumns.WEEK}, ComicColumns.WEEK + "= ?", new String[]{String.valueOf(whatWeek)}, null);
            if (cursorThisWeek.moveToFirst()) {
                newComicArrayList.clear();
                comicGridAdapter.clear();
                while (!cursorThisWeek.isAfterLast()) {
                    NewComic cursorNewComic = new NewComic();
                    cursorNewComic.setId(cursorThisWeek.getInt(INDEX_ID));
                    cursorNewComic.setPrice(cursorThisWeek.getDouble(INDEX_PRICE));
                    cursorNewComic.setThumbnailFromDB(cursorThisWeek.getString(INDEX_THUMBNAIL));

                    newComicArrayList.add(cursorNewComic);
                    cursorThisWeek.moveToNext();
                }
            }
            cursorThisWeek.close();
//            Log.v("#@@#NewCOmicsFrag", error.toString());
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        comicGridAdapter = new ComicsGridAdapter(getActivity(), newComicArrayList);

        View rootView = inflater.inflate(R.layout.content_main, container, false);

        ButterKnife.bind(this, rootView);

        contentMainError.setVisibility(View.GONE);

        if (whatWeek == -1) {
            fetchThisWeeksComics();
            whatWeek = THIS_WEEK;
        } else if (whatWeek == NEXT_WEEK){
            if (comicWeekReleaseTextview != null) {
                comicWeekReleaseTextview.setText(R.string.next_week_comics);
            }
        }

        gridView.setAdapter(comicGridAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                NewComic singleComicItem = comicGridAdapter.getItem(i);

                Bundle bundle = new Bundle();
                bundle.putInt("id", singleComicItem.getId());
                bundle.putDouble("price", singleComicItem.getPrice());
                bundle.putString("coverpath", singleComicItem.getThumbnailPath());

                Intent comicDetailIntent = new Intent(getActivity(), ComicDetailActivity.class).putExtra("comicdetails", bundle);

                //Our Animation for transitioning activities
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ) {
                    animationBundle = ActivityOptions.makeSceneTransitionAnimation(getActivity())
                    .toBundle();
                    startActivity(comicDetailIntent, animationBundle);
                } else {
                    startActivity(comicDetailIntent);
                }

            }
        });

        return rootView;
    }
}
