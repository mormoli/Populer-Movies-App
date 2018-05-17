package com.example.udacity.populermoviesapp.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.udacity.populermoviesapp.R;
import com.example.udacity.populermoviesapp.model.TheVideo;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
//https://stackoverflow.com/questions/44151979/how-to-add-onclick-listener-to-recycler-view
public class VideosAdapter extends RecyclerView.Adapter<VideosAdapter.VideosViewHolder> {
    private ArrayList<TheVideo> videoList;
    private OnItemClicked onItemClicked;
    private static final String YOUTUBE_BASE_URL = "https://www.youtube.com/watch?v=";
    // Provide a suitable constructor (depends on the kind of dataset)
    public VideosAdapter(ArrayList<TheVideo> videoList){
        this.videoList = videoList;
    }
    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public class VideosViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.movie_name_tv)
        Button name;

        public VideosViewHolder(View view){
            super(view);
            ButterKnife.bind(this, view);
            //name = view.findViewById(R.id.movie_name_tv);
        }
    }
    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public VideosViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.movie_item, parent, false);
        return new VideosViewHolder(itemView);
    }
    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(@NonNull final VideosViewHolder holder, final int position) {
        final TheVideo theVideo = videoList.get(position);
        holder.name.setText(theVideo.getName());
        holder.name.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                //Build the intent for trailer link.
                Intent videoPlayIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(YOUTUBE_BASE_URL + theVideo.getKey()));
                //Log.d(TAG, key);
                //Verify it resolves
                PackageManager packageManager = view.getContext().getPackageManager();
                List<ResolveInfo> activities = packageManager.queryIntentActivities(videoPlayIntent, 0);
                boolean isIntentSafe = activities.size() > 0;
                //Start an activity if it's safe
                if(isIntentSafe)
                    view.getContext().startActivity(videoPlayIntent);
            }
        });
    }
    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public interface OnItemClicked{
        void onItemClick(String key);
    }

    public void setOnClick(OnItemClicked onClick) {
        this.onItemClicked = onClick;
    }
}
