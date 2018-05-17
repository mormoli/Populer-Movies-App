package com.example.udacity.populermoviesapp.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class FavoritesProvider extends ContentProvider {
    private static final String TAG = FavoritesProvider.class.getSimpleName();
    private static final UriMatcher sUriMatcher = buildUriMatcher();
    private FavoritesDBHelper mOpenHelper;

    //Codes for the UriMatcher
    private static final int FAVORITES = 100;
    private static final int FAVORITES_WITH_ID = 200;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        final String authority = FavoritesContract.CONTENT_AUTHORITY;

        matcher.addURI(authority, FavoritesContract.FavoritesEntry.TABLE_FAVORITES, FAVORITES);
        matcher.addURI(authority, FavoritesContract.FavoritesEntry.TABLE_FAVORITES + "/#", FAVORITES_WITH_ID);

        return matcher;
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new FavoritesDBHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        Cursor retCursor;
        switch (sUriMatcher.match(uri)){
            case FAVORITES: {
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoritesContract.FavoritesEntry.TABLE_FAVORITES,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            case FAVORITES_WITH_ID:{
                retCursor = mOpenHelper.getReadableDatabase().query(
                        FavoritesContract.FavoritesEntry.TABLE_FAVORITES,
                        projection,
                        FavoritesContract.FavoritesEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))},
                        null,
                        null,
                        sortOrder);
                return retCursor;
            }
            default:{
                throw new UnsupportedOperationException(TAG + " : " + uri);
            }
        }
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = sUriMatcher.match(uri);

        switch (match){
            case FAVORITES:{
                return FavoritesContract.FavoritesEntry.CONTENT_DIR_TYPE;
            }
            case FAVORITES_WITH_ID:{
                return FavoritesContract.FavoritesEntry.CONTENT_ITEM_TYPE;
            }
            default:{
                throw new UnsupportedOperationException(TAG + " : " + uri);
            }
        }
    }
    @SuppressWarnings("ConstantConditions")
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri returnUri;
        switch (sUriMatcher.match(uri)){
            case FAVORITES:{
                long _id = db.insert(FavoritesContract.FavoritesEntry.TABLE_FAVORITES, null, contentValues);
                //insert unless it is already contained in the database
                if(_id > 0){
                    returnUri = FavoritesContract.FavoritesEntry.buildFavoritesUri(_id);
                } else {
                    throw new SQLException("Failed to insert row into:  "+ uri);
                }
                break;
            }
            default:{
                throw new UnsupportedOperationException(TAG + " : " + uri);
            }
        }
        //get content resolver may produce null exception
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        final int match = sUriMatcher.match(uri);
        int numDeleted;
        switch (match){
            case FAVORITES:
                numDeleted = db.delete(FavoritesContract.FavoritesEntry.TABLE_FAVORITES, selection, selectionArgs);
                //reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"+ FavoritesContract.FavoritesEntry.TABLE_FAVORITES+"'");
                break;
            case FAVORITES_WITH_ID:
                numDeleted = db.delete(FavoritesContract.FavoritesEntry.TABLE_FAVORITES,
                        FavoritesContract.FavoritesEntry._ID + " = ?",
                        new String[]{String.valueOf(ContentUris.parseId(uri))});
                //reset _ID
                db.execSQL("DELETE FROM SQLITE_SEQUENCE WHERE NAME = '"+ FavoritesContract.FavoritesEntry.TABLE_FAVORITES +"'");
                break;
            default:
                throw new UnsupportedOperationException(TAG + " : " + uri);
        }
        return numDeleted;
    }
    @SuppressWarnings("ConstantConditions")
    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String selection, @Nullable String[] selectionArgs) {
        final SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int numUpdated = 0;
        if (contentValues == null){
            throw new IllegalArgumentException("Cannot have null content values");
        }

        switch(sUriMatcher.match(uri)){
            case FAVORITES:{
                numUpdated = db.update(FavoritesContract.FavoritesEntry.TABLE_FAVORITES,
                        contentValues,
                        selection,
                        selectionArgs);
                break;
            }
            case FAVORITES_WITH_ID: {
                numUpdated = db.update(FavoritesContract.FavoritesEntry.TABLE_FAVORITES,
                        contentValues,
                        FavoritesContract.FavoritesEntry._ID + " = ?",
                        new String[] {String.valueOf(ContentUris.parseId(uri))});
                break;
            }
            default:{
                throw new UnsupportedOperationException("Unknown uri: " + uri);
            }
        }

        if (numUpdated > 0){
            //get content resolver may produce null pointer exception
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return numUpdated;
    }
}
