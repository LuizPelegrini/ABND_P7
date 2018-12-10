package com.example.android.abnd_p7.data;

import android.provider.BaseColumns;

public final class StoreContract {

    // Make the constructor private so no instances of this class can be created
    private StoreContract(){}

    // PRODUCT table
    public static class ProductEntry implements BaseColumns {
        public static final String TABLE_NAME = "products";
        public static final String COLUMN_PRODUCT_NAME = "name";
        public static final String COLUMN_PRODUCT_PRICE = "price";
        public static final String COLUMN_PRODUCT_QUANTITY = "quantity";
        public static final String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";
        public static final String COLUMN_PRODUCT_SUPPLIER_PHONE = "supplier_phone_number";
    }
}
