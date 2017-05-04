package com.example.hardeepsingh.recommender;

import android.app.SearchManager;
import android.content.ClipData;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.MediaRouter;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
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
import android.widget.RatingBar;
import android.widget.Toast;

import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    ListView listView;
    FoldingCellListAdapter adapter;

    //Cache
    SharedPreferences prefs;
    URLHandler urlHandler = new URLHandler();

    ArrayList<Movie> moviesList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        RatingBar ratingBar = (RatingBar) findViewById(R.id.ratingBar);
//        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
//        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        //Initialize Preferences
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        IntialSetup intialSetup = new IntialSetup(this);


        //Initialize Variable
        listView = (ListView) findViewById(R.id.mainListView);

        //Initialize ListView
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                ((FoldingCell) view).toggle(false);
                adapter.registerToggle(pos);
            }
        });


        //Get Now Playing Data
        getData(urlHandler.getNowPopularUrl());

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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);

        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.main_search));
        SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                getData(urlHandler.getSearchUrl(query));
                searchView.clearFocus();
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                adapter.getFilter().filter(newText);
                adapter.notifyDataSetChanged();
                return true;
            }
        });

        //Get Intent
        Intent i = getIntent();
        boolean detailToMain = i.getBooleanExtra("detailToMain", false);
        if(detailToMain) {
            String searchQueryDetail = i.getStringExtra("search_movie");
            searchView.setQuery(searchQueryDetail, true);
            searchView.setIconified(false);
            searchView.clearFocus();
        }

        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_setting) {
            Toast.makeText(this, "Settings Page", Toast.LENGTH_SHORT).show();
        } 

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getData(String url) {
        //Get Data
        final DatabaseAPI databaseApi = new DatabaseAPI();
        databaseApi.makeRequest(url, new ResponseInterface() {
            @Override
            public void onDataRecieved(String json) {
                moviesList.clear();
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

                //Set the list and Update
                adapter = new FoldingCellListAdapter(MainActivity.this, moviesList);
                listView.setAdapter(adapter);
            }
        });
    }

}
