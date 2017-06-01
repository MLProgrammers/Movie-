package com.hstmvtdj.hardeepsingh.recommender;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;

/**
 * Created by hardeepsingh on 4/29/17.
 */

public class IntialSetup {

    private SharedPreferences preferences;
    private URLHandler urlHandler;

    IntialSetup(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        urlHandler = new URLHandler();

        //Check for first time use
        if(preferences.getBoolean("first_time", true)) {
            getGenreInformation();
            preferences.edit().putBoolean("first_time", false).apply();
        }
    }

    private void getGenreInformation() {
        StringRequest nowPopularRequest = new StringRequest(urlHandler.getGenreUrl(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                preferences.edit().putString("genre", response).commit();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        Singleton.getInstance().addToReqeustQueue(nowPopularRequest);
    }
}
