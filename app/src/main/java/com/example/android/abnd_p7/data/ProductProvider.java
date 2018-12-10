package com.example.android.abnd_p7.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import com.example.android.abnd_p7.data.StoreContract.ProductEntry;

public class ProductProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);  // The UriMatcher object used to map URI's
    private static final int PRODUCT = 0;                                               // The code to help find URI of all entries
private static final int PRODUCT_ID = 1;                                                // The code to help find URI of a single entry

    private SQLiteOpenHelper mStoreDbHelper;

    static {
        // content://com.example.android.abnd_p7.product/products
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, ProductEntry.PATH_PRODUCT, PRODUCT);
        // content://com.example.android.abnd_p7.product/products/2
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, ProductEntry.PATH_PRODUCT + "/#", PRODUCT_ID);
    }

    @Override
    public boolean onCreate() {
        mStoreDbHelper = new StoreDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mStoreDbHelper.getReadableDatabase();
        switch (sUriMatcher.match(uri)){
            case PRODUCT:
                return db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            default:
                throw new IllegalArgumentException("Cannot query with unknown URI " + uri);
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        SQLiteDatabase db = mStoreDbHelper.getWritableDatabase();
        switch (sUriMatcher.match(uri)){
            case PRODUCT:
                long id = db.insert(ProductEntry.TABLE_NAME, null, contentValues);
                if(id != -1L)
                    return ContentUris.withAppendedId(uri, id);
                return null;
            default:
                throw new IllegalArgumentException("Cannot insert with URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        return 0;
    }
}
