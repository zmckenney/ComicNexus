package com.zacmckenney.comicnexus;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.facebook.stetho.Stetho;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final String COMIC_COVER_FRAG_TAG = "COMICFRAGTAG";
    public static final String SERIES_SEARCH_FRAG_TAG = "SERIESSEARCHTAG";
    public static final String MARVEL_WIKIA_FRAG_TAG = "CHARSEARCHTAG";

    public static boolean twoPane = false;

    public static Context context;

    public int whatWeek = -1;
    public final int THIS_WEEK = 0;
    public final int NEXT_WEEK = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Stetho.initializeWithDefaults(this);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = getApplicationContext();

        twoPane = context.getResources().getBoolean(R.bool.tablet_view);

        //See if we have a view in layout for fragment_covers - only mobile has fragment_covers view
        if (!twoPane){
            //Only mobile should have the fancy animation for opening and closing the drawer - tablet is permanent
            setUpNavDrawer(toolbar);
        } else {
            //If we dont have a fragment running from a saved instance -> create one
            if (savedInstanceState == null){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.tablet_container, new NewComicsFragment(), COMIC_COVER_FRAG_TAG)
                        .commit();

            }
        }

        //Both mobile and tablet use a Navigation view and this sets it up
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    private void setUpNavDrawer(Toolbar toolbar) {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_fan_wikia) {
            Bundle bundle = new Bundle();
            bundle.putString("webURL", "http://marvel.wikia.com/wiki/Marvel_Database");
            //If tablet, open in container else new activity view
            if (twoPane){
                WebViewFragment fragment = new WebViewFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.tablet_container, fragment, MARVEL_WIKIA_FRAG_TAG)
                        .commit();
            } else if (!twoPane){
                Intent marvelWikiaIntent = new Intent(this, WebViewActivity.class);
                marvelWikiaIntent.putExtra("webviewURL", bundle);
                startActivity(marvelWikiaIntent);
            }

        } else if (id == R.id.nav_official_wiki) {
            Bundle bundle = new Bundle();
            bundle.putString("webURL", "http://marvel.com/universe/Main_Page");
            //If tablet, open in container else new activity view
            if (twoPane) {
                WebViewFragment fragment = new WebViewFragment();
                fragment.setArguments(bundle);
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.tablet_container, fragment, MARVEL_WIKIA_FRAG_TAG)
                        .commit();
            } else if (!twoPane) {
                Intent marvelWikiaIntent = new Intent(this, WebViewActivity.class);
                marvelWikiaIntent.putExtra("webviewURL", bundle);
                startActivity(marvelWikiaIntent);
            }
        } else if (id == R.id.nav_search_series) {

            //If tablet, open in container else new activity view
            if (twoPane){
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.tablet_container, new SeriesSearchFragment(), SERIES_SEARCH_FRAG_TAG)
                        .commit();
            } else if (!twoPane){
                Intent seriesSearchIntent = new Intent(this, SeriesSearchActivity.class);
                startActivity(seriesSearchIntent);
            }

        } else if (id == R.id.nav_find_store) {
            // Search for comic stores nearby
            Uri gmmIntentUri = Uri.parse("geo:0,0?q=comic+book");
            Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
            mapIntent.setPackage("com.google.android.apps.maps");
            startActivity(mapIntent);

        } else if (id == R.id.nav_this_week_comics) {
            whatWeek = THIS_WEEK;
            //If tablet, open in container else new activity view
            if (twoPane) {
                NewComicsFragment newComicsFragment = (NewComicsFragment) getSupportFragmentManager().findFragmentByTag(COMIC_COVER_FRAG_TAG);

                //Check to see if we have a frag running, if not start one
                if (newComicsFragment == null){
                    getSupportFragmentManager().beginTransaction()
                            .replace(R.id.tablet_container, new NewComicsFragment(), COMIC_COVER_FRAG_TAG)
                            .commit();
                } else {
                    newComicsFragment.fetchThisWeeksComics();
                }

            } else if (!twoPane) {
                NewComicsFragment newComicsFragment = (NewComicsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_covers);
                newComicsFragment.fetchThisWeeksComics();
            }
        }
            else if (id == R.id.nav_next_week_comics){
            whatWeek = NEXT_WEEK;
            //Keep the same fragment alive if possible
                if (twoPane){
                    NewComicsFragment newComicsFragment = (NewComicsFragment) getSupportFragmentManager().findFragmentByTag(COMIC_COVER_FRAG_TAG);

                    //Check to see if we have a frag running, if not start one
                    if (newComicsFragment == null){
                        getSupportFragmentManager().beginTransaction()
                                .replace(R.id.tablet_container, new NewComicsFragment(), COMIC_COVER_FRAG_TAG)
                                .commit();
                    } else {
                        newComicsFragment.fetchNextWeeksComics();
                    }
                } else if (!twoPane){
                    NewComicsFragment newComicsFragment = (NewComicsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_covers);
                    newComicsFragment.fetchNextWeeksComics();
                }
        }

        //Only mobile uses drawer animation
        if (!twoPane) {
            DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
            drawer.closeDrawer(GravityCompat.START);
        }
        return true;
    }
}

