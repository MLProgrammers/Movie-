package com.example.hardeepsingh.recommender;

/**
 * Created by hardeepsingh on 4/14/17.
 */
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.graphics.Palette;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class FoldingCellListAdapter extends ArrayAdapter<Movie> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private ArrayList<Movie> moviesList;
    private ImageLoader imageLoader = Singleton.getInstance().getImageLoader();
    private URLHandler urlHandler = new URLHandler();


    public FoldingCellListAdapter(Context context, ArrayList<Movie> movies) {
        super(context, 0, movies);

        //Clone to filter through list
        moviesList = new ArrayList<>(movies.size());
        moviesList.addAll(movies);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // get item for selected view
        final Movie item = getItem(position);

        // if cell is exists - reuse it, if not - create the new one from resource
        FoldingCell cell = (FoldingCell) convertView;
        final ViewHolder viewHolder;
        if (cell == null) {
            viewHolder = new ViewHolder();
            LayoutInflater vi = LayoutInflater.from(getContext());
            cell = (FoldingCell) vi.inflate(R.layout.cell, parent, false);

            // binding folded view parts to view holder
            viewHolder.fold_name_letter = (TextView) cell.findViewById(R.id.folder_letter_name);
            viewHolder.fold_date_month = (TextView) cell.findViewById(R.id.fold_date_month);
            viewHolder.fold_year = (TextView) cell.findViewById(R.id.fold_year);
            viewHolder.fold_name = (TextView) cell.findViewById(R.id.fold_name);
            viewHolder.fold_genre = (TextView) cell.findViewById(R.id.fold_genre);
            viewHolder.fold_popularity = (TextView) cell.findViewById(R.id.fold_popularity);


            // binding unfolded view parts to view holder
            viewHolder.unfold_top_name = (TextView) cell.findViewById(R.id.unfold_top_name);
            viewHolder.head_image = (ImageView) cell.findViewById(R.id.head_image);
            viewHolder.unfold_name = (TextView) cell.findViewById(R.id.unfold_name);
//            viewHolder.unfold_rating = (TextView) cell.findViewById(R.id.unfold_rating);
            viewHolder.unfold_genre = (TextView) cell.findViewById(R.id.unfold_genre);
            viewHolder.unfold_date = (TextView) cell.findViewById(R.id.unfold_date);
            viewHolder.unfold_popularity = (TextView) cell.findViewById(R.id.unfold_popularity);

            // binding fold/unfold relative view to view holder
            viewHolder.fold_background = (RelativeLayout) cell.findViewById(R.id.fold_background);
            viewHolder.unfold_background = (RelativeLayout) cell.findViewById(R.id.unfold_background);

            // binding unfolded view parts to view holder button
            viewHolder.moreDetailButton = (TextView) cell.findViewById(R.id.more_detail_button);
            cell.setTag(viewHolder);
        } else {
            // for existing cell set valid valid state(without animation)
            if (unfoldedIndexes.contains(position)) {
                cell.unfold(true);
            } else {
                cell.fold(true);
            }
            viewHolder = (ViewHolder) cell.getTag();
        }

        //Split Date
        String [] dateSplit = item.getReleaseDate().split(", ");


        // bind folded data from selected element to view through view holder
        viewHolder.fold_name_letter.setText(item.getTitle().toUpperCase().substring(0,1));
        viewHolder.fold_date_month.setText(dateSplit[0]);
        viewHolder.fold_year.setText(dateSplit[1]);
        viewHolder.fold_name.setText(item.getTitle());
        viewHolder.fold_genre.setText(item.getGenreHash().values().toString());
        viewHolder.fold_popularity.setText(String.format("%.2f", item.getPopularity()));

        // bind unfolded data from selected element to view through view holder
        viewHolder.unfold_top_name.setText(item.getTitle().toUpperCase().substring(0,1));
        viewHolder.unfold_name.setText(item.getTitle());
        viewHolder.unfold_genre.setText(item.getGenreHash().values().toString());
        viewHolder.unfold_date.setText(item.getReleaseDate());
        viewHolder.unfold_popularity.setText(item.getPopularity() + "");

        //Set Pallete Color and Image
        imageLoader.get(urlHandler.getImageUrl(item.getBackdropPath(), "w300"), new ImageLoader.ImageListener() {
            @Override
            public void onResponse(ImageLoader.ImageContainer response, boolean isImmediate) {
                Bitmap myBitmap = response.getBitmap();
                if (myBitmap != null && !myBitmap.isRecycled()) {
                    viewHolder.head_image.setImageBitmap(myBitmap);
                    Palette.from(myBitmap).generate(new Palette.PaletteAsyncListener() {
                        @Override
                        public void onGenerated(Palette palette) {
                            int defaultColor = 0x000000;
                            viewHolder.fold_background.setBackgroundColor(palette.getVibrantColor(defaultColor));
                            viewHolder.unfold_background.setBackgroundColor(palette.getVibrantColor(defaultColor));
                        }
                    });
                }
            }
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Folding Cell Image Error: ", error.getMessage());
            }
        });

        //Assign an OnClickListener to Button
        viewHolder.moreDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext().getApplicationContext(), Details.class);
                intent.putExtra("movie", item);
                Activity activity = (Activity) getContext();
                activity.startActivity(intent);
                activity.overridePendingTransition(R.anim.slide_down, R.anim.fade_out);
            }
        });

        return cell;
    }

    // simple methods for register cell state changes
    public void registerToggle(int position) {
        if (unfoldedIndexes.contains(position))
            registerFold(position);
        else
            registerUnfold(position);
    }

    public void registerFold(int position) {
        unfoldedIndexes.remove(position);
    }

    public void registerUnfold(int position) {
        unfoldedIndexes.add(position);
    }

    // View lookup cache
    private static class ViewHolder {
        //Folded
        TextView fold_name_letter;
        TextView fold_date_month;
        TextView fold_year;
        TextView fold_name;
        TextView fold_genre;
        TextView fold_popularity;

        //Unfolded
        TextView unfold_top_name;
        ImageView head_image;
        TextView unfold_name;
        TextView unfold_rating;
        TextView unfold_genre;
        TextView unfold_date;
        TextView unfold_popularity;

        //Button
        TextView moreDetailButton;

        //RelativeLayout
        RelativeLayout fold_background;
        RelativeLayout unfold_background;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults filterResults = new FilterResults();
            if(constraint != null) {
                ArrayList<Movie> suggestions =  new ArrayList<Movie>();
                for(Movie movie: moviesList) {
                    if(movie.getTitle().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        suggestions.add(movie);
                    }
                }
                filterResults.values = suggestions;
                filterResults.count = suggestions.size();
            }
            return  filterResults;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            if(results != null && results.count > 0) {
                addAll((ArrayList<Movie>) results.values);
            } else {
                addAll(moviesList);
            }
            notifyDataSetChanged();
        }
    };
}