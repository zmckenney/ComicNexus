package com.zacmckenney.comicnexus;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Zac on 9/11/16.
 */
public class NewComicsActivity extends AppCompatActivity{
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout_container);

        //if no savedInstanceState then create a new fragment
        if (savedInstanceState == null){

            NewComicsFragment fragment = new NewComicsFragment();
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.main_container, fragment)
                    .commit();
        }


    }
}
