package com.example.hardeepsingh.recommender;

/**
 * Created by hardeepsingh on 4/14/17.
 */
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import android.support.v7.graphics.Palette;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ramotion.foldingcell.FoldingCell;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;


public class FoldingCellListAdapter extends ArrayAdapter<Movies> {

    private HashSet<Integer> unfoldedIndexes = new HashSet<>();
    private ArrayList<Movies> moviesList;


    public FoldingCellListAdapter(Context context, ArrayList<Movies> movies) {
        super(context, 0, movies);

        //Clone to filter through list
        moviesList = new ArrayList<>(movies.size());
        moviesList.addAll(movies);
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        // get item for selected view
        final Movies item = getItem(position);

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
            viewHolder.fold_rating = (TextView) cell.findViewById(R.id.fold_rating);
            viewHolder.fold_genre = (TextView) cell.findViewById(R.id.fold_genre);
            viewHolder.fold_collection = (TextView) cell.findViewById(R.id.fold_collection);


            // binding unfolded view parts to view holder
            viewHolder.unfold_top_name = (TextView) cell.findViewById(R.id.unfold_top_name);
            viewHolder.head_image = (ImageView) cell.findViewById(R.id.head_image);
            viewHolder.unfold_name = (TextView) cell.findViewById(R.id.unfold_name);
            viewHolder.unfold_rating = (TextView) cell.findViewById(R.id.unfold_rating);
            viewHolder.unfold_genre = (TextView) cell.findViewById(R.id.unfold_genre);
            viewHolder.unfold_date = (TextView) cell.findViewById(R.id.unfold_date);
            viewHolder.unfold_collection = (TextView) cell.findViewById(R.id.unfold_collection);

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

        // bind folded data from selected element to view through view holder
        viewHolder.fold_name_letter.setText(item.getName().toUpperCase().substring(0,1));
        viewHolder.fold_date_month.setText(item.getMonth_date());
        viewHolder.fold_year.setText(item.getYear());
        viewHolder.fold_name.setText(item.getName());
        viewHolder.fold_genre.setText(item.getGenre());
        viewHolder.fold_rating.setText(item.getRating());
        viewHolder.fold_collection.setText(item.getBoxOfficeCollection());

        // bind unfolded data from selected element to view through view holder
        viewHolder.unfold_top_name.setText(item.getName().toUpperCase().substring(0,1));
        viewHolder.head_image.setImageDrawable(getContext().getResources().getDrawable(item.getMovieDrawableID()));
        viewHolder.unfold_name.setText(item.getName());
        viewHolder.unfold_rating.setText(item.getRating());
        viewHolder.unfold_genre.setText(item.getGenre());
        viewHolder.unfold_date.setText(item.getMonth_date() + ", " + item.getYear());
        viewHolder.unfold_collection.setText(item.getBoxOfficeCollection());




        //Get Image Palette for background color
        Bitmap myBitmap = BitmapFactory.decodeResource(getContext().getResources(), item.getMovieDrawableID());
        if (myBitmap != null && !myBitmap.isRecycled()) {
            Palette.from(myBitmap).generate(new Palette.PaletteAsyncListener() {
                @Override
                public void onGenerated(Palette palette) {
                    int defaultColor = 0x000000;
                    viewHolder.fold_background.setBackgroundColor(palette.getVibrantColor(defaultColor));
                    viewHolder.unfold_background.setBackgroundColor(palette.getVibrantColor(defaultColor));
                }
            });
        }


        //Assign an OnClickListener to Button
        viewHolder.moreDetailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getContext().getApplicationContext(), Details.class);
                i.putExtra("movie", item);
                getContext().startActivity(i);
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
        TextView fold_rating;
        TextView fold_collection;

        //Unfolded
        TextView unfold_top_name;
        ImageView head_image;
        TextView unfold_name;
        TextView unfold_rating;
        TextView unfold_genre;
        TextView unfold_date;
        TextView unfold_collection;

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
                ArrayList<Movies> suggestions =  new ArrayList<Movies>();
                for(Movies movie: moviesList) {
                    if(movie.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
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
                addAll((ArrayList<Movies>) results.values);
            } else {
                addAll(moviesList);
            }
            notifyDataSetChanged();
        }
    };
}