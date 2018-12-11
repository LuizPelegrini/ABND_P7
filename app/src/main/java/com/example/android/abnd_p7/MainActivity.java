package com.example.android.abnd_p7;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.abnd_p7.adapter.ProductAdapter;
import com.example.android.abnd_p7.data.StoreContract.ProductEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.result_text_view) TextView mDataTextView;    // Reference to the view that shows the results
    @BindView(R.id.fab) FloatingActionButton mFloatActionButton;
    @BindView(R.id.list_view) ListView mListView;

    private CursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        mFloatActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });

        mAdapter = new ProductAdapter(this, null);
        mListView.setAdapter(mAdapter);

        // Display info upon creating the activity
//        displayInfo();
    }

    /** Inserts the data in the database */
    private void insertDummyProduct(String name, int price, int quantity, String supplierName, String supplierPhone){
        // Create a content values object and fill it with data
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);

        // Perform insert operation
        getContentResolver().insert(ProductEntry.CONTENT_URI, contentValues);
        displayInfo();

//
//        if(id >= 0){
//            Toast.makeText(this, getString(R.string.product_added, id), Toast.LENGTH_LONG).show();
//            displayInfo();
//        }else{
//            Toast.makeText(this, getString(R.string.error_add), Toast.LENGTH_LONG).show();
//        }
    }

    private Cursor displayInfo(){
        mDataTextView.setText("");

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
        Cursor cursor = getContentResolver().query(ProductEntry.CONTENT_URI, projection, null, null, null);

        return cursor;
//        mDataTextView.append(ProductEntry.COLUMN_PRODUCT_NAME + " | "
//                + ProductEntry.COLUMN_PRODUCT_PRICE + " | "
//                + ProductEntry.COLUMN_PRODUCT_QUANTITY + " | "
//                + ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " | "
//                + ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE + " | \n");
//
//        // Gets the index of each column
//        int idIndex = cursor.getColumnIndex(ProductEntry._ID);
//        int nameIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
//        int priceIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
//        int quantityIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
//        int supplierNameIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
//        int supplierPhoneIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);
//
//
//        try{
//            // Show each entry of the table...
//            while(cursor.moveToNext()){
//                // Get the data for each entry
//                int id = cursor.getInt(idIndex);
//                String name = cursor.getString(nameIndex);
//                int price = cursor.getInt(priceIndex);
//                int quantity = cursor.getInt(quantityIndex);
//                String supplierName = cursor.getString(supplierNameIndex);
//                String supplierPhone = cursor.getString(supplierPhoneIndex);
//
//                // Append to the text view
//                mDataTextView.append(id + ", " + name + ", " + price + ", " +quantity + ", " +supplierName + ", " + supplierPhone + "\n");
//            }
//        }finally{
//            // Closes the cursor and make it invalid
//            cursor.close();
//        }
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
                insertDummyProduct("Android Fundamentals", 20, 2, "Amazon", "555-555-555");
                break;
        }
        return true;
    }
}
