package com.example.udacity.populermoviesapp.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.example.udacity.populermoviesapp.Favorites;
import com.example.udacity.populermoviesapp.R;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FavoritesAdapter extends BaseAdapter {
    private static final String TAG = FavoritesAdapter.class.getSimpleName();
    private Context context;
    private ArrayList<Favorites> favoriteList;

    public FavoritesAdapter(Context context, ArrayList<Favorites> favoriteList){
        this.context = context;
        this.favoriteList = favoriteList;
    }

    @Override
    public int getCount() {
        return favoriteList.size();
    }

    @Override
    public Object getItem(int i) {
        return favoriteList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder viewHolder;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.image_item, viewGroup, false);
        }
        viewHolder = new ViewHolder(view);

        viewHolder.imageView.setImageBitmap(favoriteList.get(i).getImage());
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
