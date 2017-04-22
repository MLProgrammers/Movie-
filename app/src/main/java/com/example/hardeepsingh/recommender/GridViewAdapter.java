package com.example.hardeepsingh.recommender;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by hardeepsingh on 4/21/17.
 */

class GridViewAdapter extends RecyclerView.Adapter<GridViewAdapter.ViewHolder> {

    ArrayList<Movies> moviesList;
    Context context;

    public GridViewAdapter(Context context, ArrayList<Movies> moviesList) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public GridViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.recommend_row, parent, false);
        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(GridViewAdapter.ViewHolder holder, int position) {
        Movies m = moviesList.get(position);
        holder.recommendImage.setImageDrawable(context.getResources().getDrawable(m.getMovieDrawableID()));
        holder.recommendMovieName.setText(m.getName());

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

