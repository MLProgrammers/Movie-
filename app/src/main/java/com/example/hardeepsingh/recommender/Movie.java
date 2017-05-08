package com.example.hardeepsingh.recommender;

/**
 * Created by hardeepsingh on 4/29/17.
 */

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Movie implements Serializable {

    @SerializedName("poster_path")
    @Expose
    private String posterPath;
    @SerializedName("adult")
    @Expose
    private Boolean adult;
    @SerializedName("overview")
    @Expose
    private String overview;
    @SerializedName("release_date")
    @Expose
    private String releaseDate;
    @SerializedName("genre_ids")
    @Expose
    private ArrayList<Integer> genreIds = null;
    @SerializedName("genre_string")
    @Expose
    private HashMap<Integer, String> genreHash;
    @SerializedName("id")
    @Expose
    private Integer id;
    @SerializedName("original_title")
    @Expose
    private String originalTitle;
    @SerializedName("original_language")
    @Expose
    private String originalLanguage;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("backdrop_path")
    @Expose
    private String backdropPath;
    @SerializedName("popularity")
    @Expose
    private Double popularity;
    @SerializedName("vote_count")
    @Expose
    private Double voteCount;
    @SerializedName("video")
    @Expose
    private Boolean video;
    @SerializedName("vote_average")
    @Expose
    private Double voteAverage;
    @SerializedName("recommendation")
    @Expose
    private ArrayList<Movie> recommendation;
    @SerializedName("Ratings")
    @Expose
    private HashMap<String, Double> Ratings;

    private final static long serialVersionUID = 4584106220021721732L;

    /**
     * No args constructor for use in serialization
     */
    public Movie() {
    }

    public Movie(String posterPath, Boolean adult, String overview, String releaseDate, ArrayList<Integer> genreIds, Integer id,
                 String originalTitle, String originalLanguage, String title, String backdropPath, Double popularity,
                 Double voteCount, Boolean video, Double voteAverage) {
        super();
        this.posterPath = posterPath;
        this.adult = adult;
        this.overview = overview;
        this.releaseDate = releaseDate;
        this.genreIds = genreIds;
        this.id = id;
        this.originalTitle = originalTitle;
        this.originalLanguage = originalLanguage;
        this.title = title;
        this.backdropPath = backdropPath;
        this.popularity = popularity;
        this.voteCount = voteCount;
        this.video = video;
        this.voteAverage = voteAverage;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public Boolean getAdult() {
        return adult;
    }

    public void setAdult(Boolean adult) {
        this.adult = adult;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public String getReleaseDate() {
        try {
            SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.US);
            SimpleDateFormat format2 = new SimpleDateFormat("MMM dd, yyyy", Locale.US);
            Date date = format1.parse(releaseDate);
            return format2.format(date);
        } catch (Exception e){}
        return null;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public List<Integer> getGenreIds() {
        return genreIds;
    }

    public void setGenreIds(ArrayList<Integer> genreIds) {
        this.genreIds = genreIds;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOriginalLanguage() {
        return originalLanguage;
    }

    public void setOriginalLanguage(String originalLanguage) {
        this.originalLanguage = originalLanguage;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public void setBackdropPath(String backdropPath) {
        this.backdropPath = backdropPath;
    }

    public Double getPopularity() {
        return popularity;
    }

    public void setPopularity(Double popularity) {
        this.popularity = popularity;
    }

    public Double getVoteCount() {
        return voteCount;
    }

    public void setVoteCount(Double voteCount) {
        this.voteCount = voteCount;
    }

    public Boolean getVideo() {
        return video;
    }

    public void setVideo(Boolean video) {
        this.video = video;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public void setVoteAverage(Double voteAverage) {
        this.voteAverage = voteAverage;
    }

    public ArrayList<Movie> getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(ArrayList<Movie> recommendation) {
        this.recommendation = recommendation;
    }

    public HashMap<String, Double> getRatings() {
        return Ratings;
    }

    public void setRatings(HashMap<String, String> ratings) {
        HashMap<String, Double> dRating = new HashMap<>();
        //Convert all ratings to one format
        if (ratings != null) {
            for (String key : ratings.keySet()) {
                String value = ratings.get(key).replaceAll("\"", "").trim();
                key = key.replaceAll("\"", "").trim();

                // Rename to Internet Movie Database to IMDB
                if(key.equalsIgnoreCase("Internet Movie Database")) key = "IMDB";

                if (value.endsWith("%")) {
                    dRating.put(key, Double.parseDouble(value.replace("%", "")) / 10);
                } else if (value.endsWith("/100")) {
                    dRating.put(key, Double.parseDouble(value.replace("/100", "")) / 10);
                } else { // for /10
                    dRating.put(key, Double.parseDouble(value.replace("/10", "")));
                }
            }

            //Add the theMovieDB Rating to the list
            String rValue = String.valueOf(getVoteAverage());
            if (!rValue.isEmpty()) {
                dRating.put("TMDB", getVoteAverage());
            }
        }
        this.Ratings = dRating;
    }

    public HashMap<Integer, String> getGenreHash() {
        return genreHash;
    }

    public void setGenreHash(HashMap<String, String> genres) {
        HashMap<Integer, String> genreHash = new HashMap<>();
        if(genres != null) {
            for (Integer i : getGenreIds()) {
                if (genres.containsKey(String.valueOf(i))) {
                    genreHash.put(i, genres.get(String.valueOf(i)) == null ? "" : genres.get(String.valueOf(i)).replace("\"", "").trim());
                }
            }
            this.genreHash = genreHash;
        }
    }

    @Override
    public String toString() {
        return "Movie ID: " + getId() + ", Movie Name: " + getTitle() + ", Genre: " + getGenreHash() + ", Ratings: " + getRatings();
    }
}