package com.example.hardeepsingh.recommender;

import android.view.View;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by hardeepsingh on 4/14/17.
 */

public class Movies implements Serializable{
    private String name;
    private String genre;
    private String month_date;
    private String year;
    private String rating;
    private String boxOfficeCollection;
    private String recommendation;
    private int movieDrawableID;

    private View.OnClickListener moreDetailBtnClickListener;

    public Movies(String name, String genre, String releaseDate,
                  String rating, String boxOfficeCollection, String recommendation, int movieDrawableId) {
        this.name = name;
        this.genre = genre;
        this.rating = rating;
        this.boxOfficeCollection = boxOfficeCollection;
        this.recommendation = recommendation;
        this.movieDrawableID = movieDrawableId;

        String [] breakDate = releaseDate.split("\\s+");
        this.month_date = breakDate[0] + " " + breakDate[1];
        this.year = breakDate[2];
    }

    public String getMonth_date() {
        return month_date;
    }

    public String getYear() {
        return year;
    }

    public void setMonth_date(String month_date) {
        this.month_date = month_date;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }

    public int getMovieDrawableID() {
        return movieDrawableID;
    }

    public void setMovieDrawableID(int movieDrawableID) {
        this.movieDrawableID = movieDrawableID;
    }

    public String getBoxOfficeCollection() {
        return boxOfficeCollection;
    }

    public void setBoxOfficeCollection(String boxOfficeCollection) {
        this.boxOfficeCollection = boxOfficeCollection;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public View.OnClickListener getMoreDetailBtnClickListener() {
        return moreDetailBtnClickListener;
    }

    public void setMoreDetailBtnClickListener(View.OnClickListener moreDetailBtnClickListener) {
        this.moreDetailBtnClickListener = moreDetailBtnClickListener;
    }
}
