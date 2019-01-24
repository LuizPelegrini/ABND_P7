package com.example.android.abnd_p7.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class StoreContract {

    // Make the constructor private so no instances of this class can be created
    private StoreContract(){}

    public static final String CONTENT_AUTHORITY = "com.example.android.abnd_p7.product";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);


    // PRODUCT table
    public static class ProductEntry implements BaseColumns {
        public static final String PATH_PRODUCT = "products";
        public static final String PATH_PRODUCT_QUANTITY = PATH_PRODUCT + "/quantity";
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT);
        public static final Uri CONTENT_URI_QUANTITY = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCT_QUANTITY);

        public static final String CONTENT_TYPE_DIR = ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;
        public static final String CONTENT_TYPE_ITEM = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCT;

        public static final String TABLE_NAME = "products";
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplier_phone_number";
    }
}
