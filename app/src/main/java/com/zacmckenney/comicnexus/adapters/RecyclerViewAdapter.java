package com.zacmckenney.comicnexus.adapters;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zacmckenney.comicnexus.models.Creators;
import com.zacmckenney.comicnexus.models.SearchResult;
import com.zacmckenney.comicnexus.R;
import com.zacmckenney.comicnexus.WebViewActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zac on 9/12/16.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.SearchResultHolder> {

    private SearchResult[] searchResultArrayList;

    public RecyclerViewAdapter (SearchResult[] searchResultList){
        searchResultArrayList = searchResultList;
    }

    public static class SearchResultHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        @BindView(R.id.search_result_title)
        TextView titleView;
        @BindView(R.id.search_result_startyear)
        TextView startYearView;
        @BindView(R.id.search_result_type)
        TextView typeView;

        private SearchResult singleResult;

        private Context context;


        public SearchResultHolder(View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            itemView.setOnClickListener(this);

            this.context = context;
        }

        public void bindResult (SearchResult searchResult) {
            singleResult = searchResult;
            titleView.setText(searchResult.getTitle());
            startYearView.setText(context.getString(R.string.start_year, searchResult.getStartYear()));

            //get the type or set it as unavailable
            if (!searchResult.getType().isEmpty()) {
                typeView.setText(context.getString(R.string.type_of_comic, searchResult.getType()));
            } else {
                typeView.setText(context.getString(R.string.type_of_comic, R.string.unavailable));
            }

            //set all of the creator views
            Creators[] creators = searchResult.getCreators();

            //Find the LinearLayout
            LinearLayout linLay = (LinearLayout) itemView.findViewById(R.id.search_linear_layout);

            //create linearlayout element
            LinearLayout ll = new LinearLayout(itemView.getContext());
            ll.setOrientation(LinearLayout.VERTICAL);

            //Add textviews for creators
            if (creators != null) {
                 for (int i = 0; i < creators.length; i++) {
                        TextView tv = new TextView(itemView.getContext());
                        tv.setText(creators[i].getRole() + ": " + creators[i].getName());
                        ll.addView(tv);
                }
                //add the ll to ll
                linLay.addView(ll);
            }
        }

        @Override
        public void onClick(View view) {
            Context context = itemView.getContext();
            Bundle bundle = new Bundle();
            bundle.putString("webURL", singleResult.getUrl());

            Intent seriesResultIntent = new Intent(context, WebViewActivity.class);
            seriesResultIntent.putExtra("webviewURL", bundle);
            context.startActivity(seriesResultIntent);
        }
    }


    @Override
    public RecyclerViewAdapter.SearchResultHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View inflatedView = LayoutInflater.from(parent.getContext()).inflate(R.layout.search_result_item, parent, false);

        return new SearchResultHolder(inflatedView, parent.getContext());
    }

    @Override
    public void onBindViewHolder(RecyclerViewAdapter.SearchResultHolder holder, int position) {
        SearchResult itemSearchResult = searchResultArrayList[position];
        holder.bindResult(itemSearchResult);

    }

    @Override
    public int getItemCount() {
        if (searchResultArrayList != null){
            return searchResultArrayList.length;
        } else {
            return 0;
        }
    }
}
