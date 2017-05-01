package com.example.hardeepsingh.recommender;

import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.MediaRouter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.Toast;

import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listView;
    FoldingCellListAdapter adapter;
    AutoCompleteTextView autoCompleteTextView;


    //Cache
    SharedPreferences prefs;
    URLHandler urlHandler = new URLHandler();

    ArrayList<Movie> moviesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        //Initialize Preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        IntialSetup intialSetup = new IntialSetup(this);

        //Initialize Variable
        autoCompleteTextView = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView);
        autoCompleteTextView.setDropDownHeight(0);
        listView = (ListView) findViewById(R.id.mainListView);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                ((FoldingCell) view).toggle(false);
                adapter.registerToggle(pos);
            }
        });


        //Get Data
        final DatabaseAPI databaseApi = new DatabaseAPI();
        databaseApi.makeRequest(urlHandler.getNowPopularUrl(), new ResponseInterface() {
            @Override
            public void onDataRecieved(String json) {
                moviesList = databaseApi.parseMovies(json);

                //Collect All Movie Ratings
                for(final Movie m: moviesList) {
                   databaseApi.makeRequest(urlHandler.getOmdbRatingUrl(m.getTitle()), new ResponseInterface() {
                       @Override
                       public void onDataRecieved(String json) {
                           //Set Rating and Genre
                           m.setRatings(databaseApi.parseRating(json));

                       }
                   });
                    m.setGenreHash(databaseApi.parseGenre(prefs.getString("genre", null)));
                }

                adapter = new FoldingCellListAdapter(MainActivity.this, moviesList);
                listView.setAdapter(adapter);
                autoCompleteTextView.setAdapter(adapter);
            }
        });

        //Floating Button Handling
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        //Navigation Bar Handling
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}
