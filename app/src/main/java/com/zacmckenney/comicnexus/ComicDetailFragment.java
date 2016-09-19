package com.zacmckenney.comicnexus;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zacmckenney.comicnexus.data.ComicColumns;
import com.zacmckenney.comicnexus.data.ComicProvider;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zac on 9/14/16.
 */
public class ComicDetailFragment extends Fragment implements View.OnClickListener {

    @BindView(R.id.detail_comic_cover)
    ImageView comicCoverImage;

    @BindView(R.id.detail_release_week)
    TextView releaseDateView;
    @BindView(R.id.detail_release_day) TextView releaseDayView;

    @BindView(R.id.detail_issue_number) TextView issueNumberView;

    @BindView(R.id.detail_description) TextView descriptionView;

    @BindView(R.id.detail_page_number) TextView pageNumberView;

    @BindView(R.id.detail_price) TextView priceView;

    @BindView(R.id.detail_title) TextView titleView;

    @BindView(R.id.detail_button_find_store) Button findStoreButton;
    @BindView(R.id.detail_button_view_site) Button viewSiteButton;
    @BindView(R.id.detail_button_share) Button shareButton;

    private int id;
    private String coverPath;
    private int pageCount;
    private double price;
    private String title;
    private int issueNumber;
    private String description;
    private String onSaleDate;
    private String[] creators;
    private String detailUrl;

    private final String[] QUERY_COLUMNS = {
            ComicColumns.COMIC_ID,
            ComicColumns.TITLE,
            ComicColumns.PAGE_COUNT,
            ComicColumns.ISSUE_NUMBER,
            ComicColumns.DESCRIPTION,
            ComicColumns.ON_SALE_DATE,
            ComicColumns.DETAIL_URL,
            ComicColumns.CREATORS,
            ComicColumns.THUMBNAIL_PATH
    };

    private final int INDEX_ID = 0;
    private final int INDEX_TITLE = 1;
    private final int INDEX_PAGE_COUNT = 2;
    private final int INDEX_ISSUE_NUMBER = 3;
    private final int INDEX_DESCRIPTION = 4;
    private final int INDEX_ON_SALE_DATE = 5;
    private final int INDEX_DETAIL_URL = 6;
    private final int INDEX_CREATORS = 7;
    private final int INDEX_THUMBNAIL = 8;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle bundle = this.getArguments();
        if (bundle != null) {
            id = bundle.getInt("id");
            price = bundle.getDouble("price");
            coverPath = bundle.getString("coverpath");
        }

        Cursor cursorDetails = getContext().getContentResolver().query(ComicProvider.Comics.COMICS_URI, QUERY_COLUMNS , ComicColumns.COMIC_ID + "= ?", new String[]{String.valueOf(id)}, null);
        if (cursorDetails.moveToFirst()) {
            pageCount = cursorDetails.getInt(INDEX_PAGE_COUNT);
            title = cursorDetails.getString(INDEX_TITLE);
            issueNumber = cursorDetails.getInt(INDEX_ISSUE_NUMBER);
            description = cursorDetails.getString(INDEX_DESCRIPTION);
            onSaleDate = cursorDetails.getString(INDEX_ON_SALE_DATE);
            detailUrl = cursorDetails.getString(INDEX_DETAIL_URL);
            creators = cursorDetails.getString(INDEX_CREATORS).replace("[", " ").replace("]", "").split(",");

            if (coverPath == null){
                coverPath = cursorDetails.getString(INDEX_THUMBNAIL);
            }
        }
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_comic_detail, container, false);
        ButterKnife.bind(this, rootView);

        Glide.with(getContext()).load(coverPath).into(comicCoverImage);

        issueNumberView.setText(String.valueOf(issueNumber));
        descriptionView.setText(description);
        pageNumberView.setText(String.valueOf(pageCount));
        if (price != 0) {
            priceView.setText(getString(R.string.price_on_gridview, price));
        } else {
            priceView.setText(R.string.free);
        }
        titleView.setText(title);


        //Set all of the creators
        LinearLayout creatorLinLay = (LinearLayout) rootView.findViewById(R.id.detail_creator_linearlayout);
        if (creators != null) {

                for (int i = 0; i < creators.length; i++){
                    TextView createText = new TextView(getContext());
                    createText.setText(creators[i]);
                    creatorLinLay.addView(createText);
                }

        } else {
            TextView createText = new TextView(getContext());
            createText.setText(R.string.unavailable);
            creatorLinLay.addView(createText);
        }

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ");
        Date myDate = null;
        try {
            myDate = dateFormat.parse(onSaleDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        SimpleDateFormat dayFormat = new SimpleDateFormat("EEEE");
        String finalDay = dayFormat.format(myDate);

        SimpleDateFormat dateFinalFormat = new SimpleDateFormat("MMM d, yyyy");
        String finalDate = dateFinalFormat.format(myDate);

        releaseDayView.setText(String.valueOf(finalDay));
        releaseDateView.setText(String.valueOf(finalDate));

        comicCoverImage.setContentDescription(getString(R.string.a11y_description_view, title, String.valueOf(finalDay), String.valueOf(finalDate), price));


        findStoreButton.setOnClickListener(this);
        viewSiteButton.setOnClickListener(this);
        shareButton.setOnClickListener(this);

        getActivity().setTitle(title);

        return rootView;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){

            case R.id.detail_button_find_store:
                // Search for comic stores nearby
                Uri gmmIntentUri = Uri.parse("geo:0,0?q=comic+book");
                Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
                mapIntent.setPackage("com.google.android.apps.maps");
                startActivity(mapIntent);
                break;
            case R.id.detail_button_view_site:
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setData(Uri.parse(detailUrl));
                startActivity(i);
                break;
            case R.id.detail_button_share:
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, title + " " + R.string.hashtag);
                startActivity(shareIntent);
                break;
        }

    }
}
