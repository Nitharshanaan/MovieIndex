package com.nitharshanaan.android.movieindex;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by nitha on 11/29/2017.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MovieViewHolder> {

    private Context mContext;
    private List<Movie> movieList;

    public MoviesAdapter(Context mContext, ArrayList<Movie> movieList) {
        this.mContext = mContext;
        this.movieList = movieList;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.movie_card, parent, false);

        return new MovieViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {

        Movie movie = movieList.get(position);
        holder.title.setText(movie.title);
        holder.rating.setText(movie.rating);

        Glide.with(mContext).load(movie.thumbnailUrl).into(holder.thumbnail);

    }

    @Override
    public int getItemCount() {
        return movieList.size();
    }

    public class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView title, rating;
        public ImageView thumbnail;

        public MovieViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.title);
            rating = view.findViewById(R.id.rating);
            thumbnail = view.findViewById(R.id.thumbnail);
            view.setOnClickListener(MovieViewHolder.this);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            Movie selectedMovie = movieList.get(position);
            Intent intent = new Intent(view.getContext(), MovieDetail.class);
            intent.putExtra("Movie", selectedMovie);
            view.getContext().startActivity(intent);
        }
    }
}