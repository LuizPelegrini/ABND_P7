package com.example.android.abnd_p7;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.abnd_p7.data.StoreContract.ProductEntry;
import com.example.android.abnd_p7.data.StoreDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    private SQLiteOpenHelper mStoreDbHelper;                    // Reference to the store SqlHelper
    @BindView(R.id.result_text_view) TextView mDataTextView;    // Reference to the view that shows the results

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mStoreDbHelper = new StoreDbHelper(this);

        // Display info upon creating the activity
        displayInfo();
    }

    /** Inserts the data in the database */
    private void insertData(String name, int price, int quantity, String supplierName, String supplierPhone){
        // Create a content values object and fill it with data
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);

        // Get an instance of the database for writing purposes
        SQLiteDatabase db = mStoreDbHelper.getWritableDatabase();

        // Perform insert operation
        long id = db.insert(ProductEntry.TABLE_NAME, null, contentValues);

        if(id >= 0){
            Toast.makeText(this, getString(R.string.product_added, id), Toast.LENGTH_LONG).show();
            displayInfo();
        }else{
            Toast.makeText(this, getString(R.string.error_add), Toast.LENGTH_LONG).show();
        }
    }

    private void displayInfo(){
        mDataTextView.setText("");

        // Reads from the database
        SQLiteDatabase db = mStoreDbHelper.getReadableDatabase();

        // Choose the columns I want to show
        String[] projection = new String[]{
            ProductEntry._ID,
            ProductEntry.COLUMN_PRODUCT_NAME,
            ProductEntry.COLUMN_PRODUCT_PRICE,
            ProductEntry.COLUMN_PRODUCT_QUANTITY,
            ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
            ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE
        };

        // Query the database for the results
        Cursor cursor = db.query(ProductEntry.TABLE_NAME, projection, null, null, null, null, null);

        mDataTextView.append(ProductEntry.COLUMN_PRODUCT_NAME + " | "
                + ProductEntry.COLUMN_PRODUCT_PRICE + " | "
                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " | "
                + ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " | "
                + ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + " | \n");

        // Gets the index of each column
        int idIndex = cursor.getColumnIndex(ProductEntry._ID);
        int nameIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNameIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        int supplierPhoneIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);


        try{
            // Show each entry of the table...
            while(cursor.moveToNext()){
                // Get the data for each entry
                int id = cursor.getInt(idIndex);
                String name = cursor.getString(nameIndex);
                int price = cursor.getInt(priceIndex);
                int quantity = cursor.getInt(quantityIndex);
                String supplierName = cursor.getString(supplierNameIndex);
                String supplierPhone = cursor.getString(supplierPhoneIndex);

                // Append to the text view
                mDataTextView.append(id + ", " + name + ", " + price + ", " +quantity + ", " +supplierName + ", " + supplierPhone + "\n");
            }
        }finally{
            // Closes the cursor and make it invalid
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()){
            // Insert some dummy data for testing
            case R.id.insert_dummy_data:
                insertData("Android Fundamentals", 20, 2, "Amazon", "555-555-555");
                break;
        }
        return true;
    }
}
