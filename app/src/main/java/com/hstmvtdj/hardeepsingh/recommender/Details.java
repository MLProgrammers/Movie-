package com.hstmvtdj.hardeepsingh.recommender;

import android.app.SearchManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class Details extends AppCompatActivity {

    Movie movie;
    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    GridViewAdapter adapter;
    ImageLoader imageLoader = Singleton.getInstance().getImageLoader();
    URLHandler urlHandler = new URLHandler();
    SharedPreferences prefs;

    TextView detail_name, detail_date, detail_imdb_rating, detail_rt_rating,
            detail_mc_rating, detail_db_rating, detail_genre, detail_description;
    ImageView detail_movie_image;
    CoordinatorLayout coordinatorLayout;
    DatabaseAPI databaseAPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Get Intent, Preferences and API
        Intent i = getIntent();
        movie = (Movie) i.getSerializableExtra("movie");
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        databaseAPI = new DatabaseAPI();

        //Initialize Layouts
        detail_name = (TextView) findViewById(R.id.detail_name);
        detail_date = (TextView) findViewById(R.id.detail_date);
        detail_imdb_rating = (TextView) findViewById(R.id.detail_imdb_rating);
        detail_rt_rating = (TextView) findViewById(R.id.detail_rt_rating);
        detail_mc_rating = (TextView) findViewById(R.id.detail_mc_rating);
        detail_db_rating = (TextView) findViewById(R.id.detail_db_rating);
        detail_genre = (TextView) findViewById(R.id.detail_genre);
        detail_description = (TextView) findViewById(R.id.detail_description);
        detail_movie_image = (ImageView) findViewById(R.id.detail_movie_image);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.cordinator_layout);


        recyclerView = (RecyclerView) findViewById(R.id.grid_view);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(layoutManager);

        //Set Data
        detail_name.setText(movie.getTitle());
        detail_date.setText(movie.getReleaseDate());
        detail_description.setText(movie.getOverview());

        //Get Ratings
        if (movie.getRatings() == null) {
            databaseAPI.makeRequest(urlHandler.getOmdbRatingUrl(movie.getTitle()), new ResponseInterface() {
                @Override
                public void onDataRecieved(String json) {
                    movie.setRatings(databaseAPI.parseRating(json));
                    handleRatingFormat();
                }
            });
        } else {
            handleRatingFormat();
        }

        //Getting Genre Single Line Separated
        if (movie.getGenreHash() == null) {
            databaseAPI.makeRequest(urlHandler.getOmdbRatingUrl(movie.getTitle()), new ResponseInterface() {
                @Override
                public void onDataRecieved(String json) {
                    movie.setGenreHash(databaseAPI.parseGenre(prefs.getString("genre", null)));
                    handleGenreFormat();
                }
            });
        } else {
            handleGenreFormat();
        }

        //Set Image
        imageLoader.get(urlHandler.getImageUrl(movie.getBackdropPath(), "w300"), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                //Skip the Failure
                if (isImmediate && response.getBitmap() == null) return;

                Bitmap myBitmap = response.getBitmap();
                detail_movie_image.setImageBitmap(myBitmap);
                Palette.from(myBitmap).generate(new Palette.PaletteAsyncListener() {
                    @Override
                    public void onGenerated(Palette palette) {
                        int defaultColor = 0xffffff;

                        int color = palette.getDarkVibrantColor(defaultColor);

                        //Change Status Bar Color
                        Window window = getWindow();
                        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                        window.setStatusBarColor(color);
                    }
                });
            }

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Detail Image Error: ", error.getMessage());
            }
        });


        //Get Recommendation
        final DatabaseAPI databaseAPI = new DatabaseAPI();
        URLHandler urlHandler = new URLHandler();
        databaseAPI.makeRequest(urlHandler.getRecommendationUrl(movie.getId().toString()), new ResponseInterface() {
            @Override
            public void onDataRecieved(String json) {
                ArrayList<Movie> recommendation = databaseAPI.parseMovies(json);
                adapter = new GridViewAdapter(Details.this, recommendation);
                recyclerView.setAdapter(adapter);
            }
        });

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent sendIntent = new Intent();
                sendIntent.setAction(Intent.ACTION_SEND);
                sendIntent.putExtra(Intent.EXTRA_TEXT, "Check out " + movie.getTitle() + " with ratings " + movie.getRatings());
                sendIntent.setType("text/plain");
                startActivity(sendIntent);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_up, R.anim.fade_out);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_details, menu);

        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(menu.findItem(R.id.detail_search));
        final SearchManager searchManager = (SearchManager) getSystemService(SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                Intent i = new Intent(Details.this, MainActivity.class);
                i.putExtra("search_movie", query);
                i.putExtra("detailToMain", true);
                startActivity(i);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return true;
            }
        });
        return true;
    }

    public void handleGenreFormat() {
        StringBuilder builder = new StringBuilder();
        Iterator<Map.Entry<Integer, String>> entryIterator = movie.getGenreHash().entrySet().iterator();
        while (entryIterator.hasNext()) {
            Map.Entry<Integer, String> entry = entryIterator.next();
            builder.append(entry.getValue());
            if (entryIterator.hasNext()) {
                builder.append("\n");
            }
        }
        detail_genre.setText(builder.toString());
    }

    public void handleRatingFormat() {
        String notAvaliable = "N/A";
        for (Map.Entry<String, Double> r : movie.getRatings().entrySet()) {
            switch (r.getKey()) {
                case "TMDB":
                    detail_db_rating.setText(Double.toString(r.getValue()));
                    break;
                case "IMDB":
                    detail_imdb_rating.setText(Double.toString(r.getValue()));
                    break;
                case "Rotten Tomatoes":
                    detail_rt_rating.setText(Double.toString(r.getValue()));
                    break;
                case "Metacritic":
                    detail_mc_rating.setText(Double.toString(r.getValue()));
                    break;
            }
        }
    }
}
