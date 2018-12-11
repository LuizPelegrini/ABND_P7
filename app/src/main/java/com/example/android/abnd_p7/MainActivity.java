package com.example.android.abnd_p7;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
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

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;                         // The loader id when it is initialized

    @BindView(R.id.result_text_view) TextView mDataTextView;        // Reference to the view that shows the results
    @BindView(R.id.fab) FloatingActionButton mFloatActionButton;    // The FAB reference
    @BindView(R.id.list_view) ListView mListView;                   // The ListView reference

    private CursorAdapter mAdapter;                                 // The adapter to be used by the ListView

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

        getSupportLoaderManager().initLoader(LOADER_ID,null, this);

        // Display info upon creating the activity
//        displayInfo();
    }

    /** Inserts the data in the database */
    private void insertDummyProduct(String name, int price, int quantity, String supplierName, String supplierPhone) {
        // Create a content values object and fill it with data
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, name);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, price);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);

        // Perform insert operation through a content provider
        getContentResolver().insert(ProductEntry.CONTENT_URI, contentValues);
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

    /******************* Loader callbacks *******************/


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(this, ProductEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
