package com.zacmckenney.comicnexus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Zac on 9/13/16.
 */
public class ComicDetailActivity extends AppCompatActivity {



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout_container);


        Bundle args = getIntent().getBundleExtra("comicdetails");

        //if no savedInstanceState then create a new fragment
        if (savedInstanceState == null){

            ComicDetailFragment fragment = new ComicDetailFragment();
            fragment.setArguments(args);
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commit();
        }
    }


}
