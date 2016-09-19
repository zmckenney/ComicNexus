package com.zacmckenney.comicnexus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Zac on 9/9/16.
 */
public class WebViewActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview_activity_layout);

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.drawable.ic_logo);


        Bundle args = getIntent().getBundleExtra("webviewURL");
        //if no savedInstanceState then create a new fragment
        if (savedInstanceState == null){
            WebViewFragment fragment = new WebViewFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.webview_container, fragment)
                    .commit();
        }


    }
}
