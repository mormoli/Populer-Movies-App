package com.example.udacity.populermoviesapp.utils;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.udacity.populermoviesapp.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CustomArrayAdapter extends BaseAdapter{
    private static final String TAG = CustomArrayAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<String> posterImageLinks;

    //constructor
    public CustomArrayAdapter(Context context, ArrayList<String> imgLinks){
        this.context = context;
        this.posterImageLinks = imgLinks;
        //this.imgDimension = imgDimension;
    }

    @Override
    public int getCount() {
        return posterImageLinks.size();
    }

    @Override
    public Object getItem(int i) {
        return posterImageLinks.get(i);
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }
    @SuppressWarnings("ConstantConditions")
    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        // If this is a new View object we're getting, then inflate the layout.
        // If not, this view already has the layout inflated from a previous call to getView,
        // and we modify the View widgets as usual.
        if(view == null){
            //Log.d(TAG, "view initialization");
            view = LayoutInflater.from(context).inflate(R.layout.image_item, viewGroup, false);
            //ButterKnife.bind(this, view);
        }
        viewHolder = new ViewHolder(view);
        //viewHolder.imageView = view.findViewById(R.id.image_view_tv);
        //dynamically setting pixel sizes of image
        //final float scale = context.getResources().getDisplayMetrics().density;
        //int pixels = (int) ( imgDimension * scale + 0.5f );

        //viewHolder.imageView.setLayoutParams(new LinearLayout.LayoutParams(pixels, pixels*2));

        //loading images in to image view with picasso
        Picasso.get()
                .load(posterImageLinks.get(i))
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.error)
                .into(viewHolder.imageView);
        //Log.d(TAG,posterImageLinks.get(i));
        return view;
    }

    class ViewHolder{
        @BindView(R.id.image_view_tv)
        ImageView imageView;
        ViewHolder(View view) {
            ButterKnife.bind(this, view);
        }
    }
}
