package com.example.android.abnd_p7.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.android.abnd_p7.data.StoreContract.ProductEntry;

public class StoreDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "bookstore.db";         // Database name
    private static final int DATABASE_VERSION = 1;                      // Database version. When changing this number, onUpgrade is automatically called

    public StoreDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE = "CREATE TABLE " + ProductEntry.TABLE_NAME + "(" +
                        ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                        ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " +
                        ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, " +
                        ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " TEXT NOT NULL, " +
                        ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + " TEXT);";
        sqLiteDatabase.execSQL(CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        String DROP = "DROP TABLE " + ProductEntry.TABLE_NAME + ";";
        sqLiteDatabase.execSQL(DROP);
        onCreate(sqLiteDatabase);
    }
}
