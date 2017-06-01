package com.hstmvtdj.hardeepsingh.recommender;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;

import java.util.ArrayList;

/**
 * Created by hardeepsingh on 4/21/17.
 */

class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> {

    private ArrayList<Movie> moviesList;
    private ImageLoader imageLoader = Singleton.getInstance().getImageLoader();
    private URLHandler urlHandler = new URLHandler();
    private Context context;

    public GridViewAdapter(Context context, ArrayList<Movie> moviesList) {
        this.context = context;
        this.moviesList = moviesList;
    }

    @Override
    public GridViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(final GridViewAdapter.ViewHolder holder, final int position) {
        Movie m = moviesList.get(position);
        holder.recommendMovieName.setText(m.getTitle());
        imageLoader.get(urlHandler.getImageUrl(m.getBackdropPath(), "w185"), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                holder.recommendImage.setImageBitmap(response.getBitmap());
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Grid View Image Error: ", error.getMessage());
            }
        });

        //Handle Click
        holder.recommendImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context.getApplicationContext(), Details.class);
                intent.putExtra("movie", moviesList.get(position));
                Activity activity = (Activity) context;
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_down, R.anim.fade_out);
            }
        });
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView recommendImage;
        TextView recommendMovieName;
        public ViewHolder(View itemView) {
            super(itemView);
            recommendImage = (ImageView) itemView.findViewById(R.id.recommend_image);
            recommendMovieName = (TextView) itemView.findViewById(R.id.recommend_image_movie_name);
        }
    }
}

