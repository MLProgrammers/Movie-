package com.example.hardeepsingh.recommender;

/**
 * Created by hardeepsingh on 4/29/17.
 */

class URLHandler {
    private static final String API_KEY = "5812e4b63553d1273a420416fddeed72";

    String getNowPopularUrl() {
        return "https://api.themoviedb.org/3/movie/popular?api_key=" + API_KEY + "&language=en-US&page=1&region=US";
    }

    String getGenreUrl() {
        return "https://api.themoviedb.org/3/genre/movie/list?api_key=" + API_KEY + "&language=en-US";
    }

    String getOmdbRatingUrl(String name) {
        String OMDB_RATING_URL = "http://www.omdbapi.com/?t=";
        return OMDB_RATING_URL + name.replaceAll(" ", "%20");
    }

    String getRecommendationUrl(String movieID) {
        String RECOMMENDATION_URL = "https://api.themoviedb.org/3/movie/*movie_id*/recommendations?api_key=" + API_KEY + "&language=en-US&page=1";
        return RECOMMENDATION_URL.replace("*movie_id*", movieID);
    }

    String getImageUrl(String path, String size) {
        String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/";
        return IMAGE_BASE_URL + size + path;
    }
}
