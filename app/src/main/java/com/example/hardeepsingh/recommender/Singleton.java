package com.example.hardeepsingh.recommender;

import android.app.Application;
import android.text.TextUtils;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by hardeepsingh on 4/29/17.
 */

public class Singleton extends Application {

    public static final String TAG = Singleton.class.getSimpleName();

    private RequestQueue requestQueue;
    private ImageLoader imageLoader;

    private static Singleton instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static synchronized Singleton getInstance() {
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if(requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());
        }
        return requestQueue;
    }

    public ImageLoader getImageLoader() {
        getRequestQueue();
        if(imageLoader == null) {
            imageLoader = new ImageLoader(this.requestQueue, new BitmapCache());
        }
        return this.imageLoader;
    }

    public <T> void addToReqeustQueue(Request<T> req, String tag) {
        req.setTag(TextUtils.isEmpty(tag) ? TAG : tag);
        getRequestQueue().add(req);
    }

    public <T> void addToReqeustQueue(Request<T> req) {
        req.setTag(TAG);
        getRequestQueue().add(req);
    }

    public void cancelPendingRequests (Object tag) {
        if(requestQueue != null) {
            requestQueue.cancelAll(tag);
        }
    }
}
