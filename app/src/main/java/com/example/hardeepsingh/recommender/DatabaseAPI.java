package com.example.hardeepsingh.recommender;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by hardeepsingh on 4/29/17.
 */

public class DatabaseAPI {

    public void makeRequest(String url, final ResponseInterface callback) {
        StringRequest request = new StringRequest(url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onDataRecieved(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });

        Singleton.getInstance().addToReqeustQueue(request);
    }

    public ArrayList<Movie> parseMovies(String json) {
        if (json != null && json.contains("results")) {
            JsonElement jelement = new JsonParser().parse(json);
            JsonObject jobject = jelement.getAsJsonObject();
            JsonArray jarray = jobject.getAsJsonArray("results");
            Type type = new TypeToken<List<Movie>>() {
            }.getType();
            ArrayList<Movie> movies = new Gson().fromJson(jarray.toString(), type);
            return movies;
        } else {
            System.err.println("Genre Not Found!");
            return null;
        }
    }

    public HashMap<String, String> parseRating(String json) {
        if (json != null && json.contains("Ratings")) {
            JsonElement jelement = new JsonParser().parse(json);
            JsonObject jobject = jelement.getAsJsonObject();
            JsonArray jarray = jobject.getAsJsonArray("Ratings");

            HashMap<String, String> ratings = new HashMap<>();
            for (int i = 0; i < jarray.size(); i++) {
                String id = jarray.get(i).getAsJsonObject().get("Source").toString();
                String name = jarray.get(i).getAsJsonObject().get("Value").toString();
                ratings.put(id, name);
            }
            return ratings;
        } else {
            System.err.println("Genre Not Found!");
            return null;
        }
    }

    public HashMap<String, String> parseGenre(String json) {
        if (json != null && json.contains("genres")) {
            JsonElement jelement = new JsonParser().parse(json);
            JsonObject jobject = jelement.getAsJsonObject();
            JsonArray jarray = jobject.getAsJsonArray("genres");

            HashMap<String, String> genres = new HashMap<>();
            for (int i = 0; i < jarray.size(); i++) {
                String id = jarray.get(i).getAsJsonObject().get("id").toString();
                String name = jarray.get(i).getAsJsonObject().get("name").toString();
                genres.put(id, name);
            }
            return genres;
        } else {
            System.err.println("Genre Not Found!");
            return null;
        }
    }
}
