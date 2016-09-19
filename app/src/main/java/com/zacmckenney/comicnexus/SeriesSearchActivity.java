package com.zacmckenney.comicnexus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Zac on 9/9/16.
 */
public class SeriesSearchActivity extends AppCompatActivity {


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout_container);

        //if no savedInstanceState then create a new fragment
        if (savedInstanceState == null) {
             SeriesSearchFragment fragment = new SeriesSearchFragment();

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commit();
        }

    }
}
