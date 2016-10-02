package com.zacmckenney.comicnexus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.zacmckenney.comicnexus.adapters.RecyclerViewAdapter;
import com.zacmckenney.comicnexus.data.Utils;
import com.zacmckenney.comicnexus.models.SearchResult;
import com.zacmckenney.comicnexus.parsers.SearchResultParser;

import java.util.Calendar;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Zac on 9/9/16.
 */
public class SeriesSearchFragment extends Fragment {

    @BindView(R.id.search_progressbar)
    ProgressBar searchProgress;

    @BindView(R.id.search_results_recyclerview) RecyclerView recyclerView;

    @BindView(R.id.search_error_textview) TextView searchErrorTextview;

//    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private Gson gson;
    private RequestQueue requestQueue;
    private StringRequest request;

    private static final String ENDPOINT_PREFIX = "http://gateway.marvel.com/v1/public/series?titleStartsWith=";
    private static final String ENDPOINT_SUFFIX = "&orderBy=-startYear&limit=100";

    private final String API_KEY = BuildConfig.API_KEY;
    private final String PUBLIC_KEY = "644ca5a41a32e5ba84cd6ce566261ddb";
    private String timestamp;
    private String authorizationString;
    public String editTextSearch;

    private RecyclerViewAdapter recyclerViewAdapter;

    private SearchResult[] searchResultList;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.registerTypeAdapter(SearchResult[].class, new SearchResultParser());
        gson = gsonBuilder.create();

        requestQueue = Volley.newRequestQueue(getContext());
    }

    public void fetchSearchResults() {
        timestamp = "" + Calendar.getInstance().getTimeInMillis();
        authorizationString = Utils.getAuthorizationKey(timestamp, API_KEY, PUBLIC_KEY);
        request = new StringRequest(Request.Method.GET, ENDPOINT_PREFIX + editTextSearch + ENDPOINT_SUFFIX + authorizationString, onComicsLoaded, onComicsError);
        requestQueue.add(request);
    }

    private final Response.Listener<String> onComicsLoaded = new Response.Listener<String>() {
        @Override
        public void onResponse(String response) {
//            Log.v("#@#@NewComicsFrag", response);
            searchResultList = gson.fromJson(response, SearchResult[].class);

            recyclerViewAdapter = new RecyclerViewAdapter(searchResultList);
            recyclerView.setAdapter(recyclerViewAdapter);

            searchProgress.setVisibility(View.GONE);

        }
    };

    private final Response.ErrorListener onComicsError = new Response.ErrorListener() {
        @Override
        public void onErrorResponse(VolleyError error) {
            searchProgress.setVisibility(View.GONE);
            searchErrorTextview.setVisibility(View.VISIBLE);
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_series_search, container, false);

        ButterKnife.bind(this, rootView);
        searchProgress.setVisibility(View.GONE);
        searchErrorTextview.setVisibility(View.GONE);

        linearLayoutManager = new LinearLayoutManager(getContext());
        recyclerView.addItemDecoration(new DividerItemDecoration(getContext(), LinearLayoutManager.VERTICAL));
        recyclerView.setLayoutManager(linearLayoutManager);

        final EditText editText = (EditText) rootView.findViewById(R.id.series_search_input);

        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                boolean handled = false;
                if (i == EditorInfo.IME_ACTION_GO){
                    editTextSearch = textView.getText().toString();
                    editTextSearch = editTextSearch.replace(" ", "%20");
                    searchProgress.setVisibility(View.VISIBLE);
                    fetchSearchResults();
                    handled = true;
                }
                return handled;
            }
        });


        return rootView;
    }
}
