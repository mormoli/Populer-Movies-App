package com.example.udacity.populermoviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FavoritesDBHelper extends SQLiteOpenHelper {
    public static final String TAG = FavoritesDBHelper.class.getSimpleName();

    //name & version
    private static final String DATABASE_NAME = "favorites.db";
    private static final int DATABASE_VERSION = 1;

    public FavoritesDBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " +
                FavoritesContract.FavoritesEntry.TABLE_FAVORITES + "(" + FavoritesContract.FavoritesEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FavoritesContract.FavoritesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_IMAGE + " BLOB, " +
                FavoritesContract.FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT_NOT_NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_RATING + " TEXT NOT NULL, " +
                FavoritesContract.FavoritesEntry.COLUMN_SYNOPSIS + " TEXT NOT NULL);";

        //Execute database creation
        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        Log.d(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion + ". OLD DATA WILL BE DESTROYED");

        //Drop the table
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavoritesContract.FavoritesEntry.TABLE_FAVORITES);
        sqLiteDatabase.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '" +
                FavoritesContract.FavoritesEntry.TABLE_FAVORITES + "'");

        //re-create database
        onCreate(sqLiteDatabase);
    }
}
