package com.example.udacity.populermoviesapp.utils;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.udacity.populermoviesapp.R;
import com.example.udacity.populermoviesapp.model.TheReview;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsViewHolder> {
    private ArrayList<TheReview> reviewList;
    // Provide a suitable constructor (depends on the kind of dataset)
    public ReviewsAdapter(ArrayList<TheReview> reviewList){
        this.reviewList = reviewList;
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class ReviewsViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.review_content_tv)
        TextView reviewContent;
        @BindView(R.id.review_author_tv)
        TextView reviewAuthor;

        public ReviewsViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public ReviewsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.review_item, parent, false);
        return new ReviewsViewHolder(itemView);
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final ReviewsViewHolder holder, final int position) {
        final TheReview theReview = reviewList.get(position);
        holder.reviewContent.setText(theReview.getContent());
        holder.reviewAuthor.setText(theReview.getAuthor());
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return reviewList.size();
    }
}
