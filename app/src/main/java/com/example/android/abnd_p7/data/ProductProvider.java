package com.example.android.abnd_p7.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;

import com.example.android.abnd_p7.data.StoreContract.ProductEntry;

public class ProductProvider extends ContentProvider {

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);  // The UriMatcher object used to map URI's
    private static final int PRODUCT = 0;                                               // The code to help find URI of all entries
    private static final int PRODUCT_ID = 1;                                            // The code to help find URI of a single entry
    private static final int PRODUCT_QUANTITY = 2;                                      // The code to help find quantity URI of a single entry

    private StoreDbHelper mStoreDbHelper;

    static {
        // content://com.example.android.abnd_p7.product/products
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, ProductEntry.PATH_PRODUCT, PRODUCT);
        // content://com.example.android.abnd_p7.product/products/2
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, ProductEntry.PATH_PRODUCT + "/#", PRODUCT_ID);
        // content://com.example.android.abnd_p7.product/products/quantity/2
        sUriMatcher.addURI(StoreContract.CONTENT_AUTHORITY, ProductEntry.PATH_PRODUCT_QUANTITY + "/#", PRODUCT_QUANTITY);
    }

    @Override
    public boolean onCreate() {
        mStoreDbHelper = new StoreDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = mStoreDbHelper.getReadableDatabase();
        Cursor cursor;
        switch (sUriMatcher.match(uri)){
            case PRODUCT:
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            break;
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                cursor = db.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
            break;
            default:
                throw new IllegalArgumentException("Cannot query with unknown URI " + uri);
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)){
            case PRODUCT:
                return ProductEntry.CONTENT_TYPE_DIR;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_TYPE_ITEM;
        }
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) throws SQLException, IllegalArgumentException{
        // Matches the URI with content://com.example.android.abnd_p7.product/products
        switch (sUriMatcher.match(uri)){
            case PRODUCT:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Cannot insert with URI " + uri);
        }
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        switch(sUriMatcher.match(uri)){
            case PRODUCT:
                return deleteProduct(uri, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return deleteProduct(uri, selection, selectionArgs);
        }

        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        switch(sUriMatcher.match(uri)){
            case PRODUCT:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_QUANTITY:
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProductQuantity(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Cannot update with URI " + uri);
        }
    }




    // Insert the product into the database
    private Uri insertProduct(Uri uri, ContentValues contentValues) throws SQLException{
        // Validates the input
        validateInput(contentValues);

        // Get a database reference to write in the database
        SQLiteDatabase db = mStoreDbHelper.getWritableDatabase();

        // Perform the insertion
        long id = db.insert(ProductEntry.TABLE_NAME, null, contentValues);
        if(id != -1L) {
            // Notify a change has been made in this content URI
            getContext().getContentResolver().notifyChange(ContentUris.withAppendedId(uri, id), null);
            return ContentUris.withAppendedId(uri, id);
        }

        // Otherwise return a null URI
        return null;
    }

    private int updateProduct(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) throws SQLException{
        // validates the input
        validateInput(contentValues);

        // Get a database writable instance
        SQLiteDatabase db = mStoreDbHelper.getWritableDatabase();

        // update the registry
        int rowsAffected = db.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);

        // If 1 or more rows were updated,
        // then notify all listeners that the data at the given URI has changed
        if(rowsAffected > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsAffected;
    }

    // Used to validate and update the quantity only
    private int updateProductQuantity(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs){
        if(contentValues.size() == 0)
            throw new SQLException("empty");

        int quantity = contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if(quantity < 0)
            throw new SQLException(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        SQLiteDatabase db = mStoreDbHelper.getWritableDatabase();
        int rowsAffected = db.update(ProductEntry.TABLE_NAME, contentValues, selection, selectionArgs);
        if(rowsAffected > 0)
            getContext().getContentResolver().notifyChange(uri, null);

        return rowsAffected;
    }


    private int deleteProduct(Uri uri, String selection, String[] selectionArgs){
        SQLiteDatabase db = mStoreDbHelper.getWritableDatabase();
        int rowsDeleted = db.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
        if(rowsDeleted > 0){
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    // Validates the input coming from ContentValues
    private void validateInput(ContentValues contentValues) throws SQLException{
        if(contentValues.size() == 0)
            throw new SQLException("empty");

        // Validate name
        String name = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if(name == null || TextUtils.isEmpty(name.trim()))
            throw new SQLException(ProductEntry.COLUMN_PRODUCT_NAME);

        // Validate price
        int price = contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_PRICE);
        if(price < 0)
            throw new SQLException(ProductEntry.COLUMN_PRODUCT_PRICE);

        // Validate quantity
        int quantity = contentValues.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if(quantity < 0)
            throw new SQLException(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        // Validate supplier name
        String supplierName = contentValues.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if(supplierName == null || TextUtils.isEmpty(supplierName.trim()))
            throw new SQLException(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
    }
}
