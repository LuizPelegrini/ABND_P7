package com.example.android.abnd_p7;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.android.abnd_p7.adapter.ProductRecyclerAdapter;
import com.example.android.abnd_p7.data.StoreContract.ProductEntry;
import com.example.android.abnd_p7.util.Message;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 0;                             // The loader id when it is initialized

    @BindView(R.id.result_text_view) TextView mDataTextView;            // Reference to the view that shows the results
    @BindView(R.id.fab) FloatingActionButton mFloatActionButton;        // The FAB reference
    @BindView(R.id.recycler_view) RecyclerView mRecyclerView;           // The RecyclerView reference
    @BindView(R.id.no_products_info_layout) View mNoProductsInfoView;   // A view that appears when there is no product to be shown

    private ProductRecyclerAdapter mAdapter;                        // The adapter to be used by the RecyclerView

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

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new ProductRecyclerAdapter(this, null);
        mRecyclerView.setAdapter(mAdapter);
        mNoProductsInfoView.setVisibility(View.GONE);

        getSupportLoaderManager().initLoader(LOADER_ID,null, this);
    }

    /** Inserts the data in the database */
    private void insertDummyProduct() {
        // Create a content values object and fill it with data
        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, "Android Fundamentals");
        contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, 209);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 2);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Amazon");
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, "555-555-555");

        // Perform insert operation through a content provider and if any input is not validated,
        // show a toast message, telling about the error
        try{
            getContentResolver().insert(ProductEntry.CONTENT_URI, contentValues);
        } catch (SQLException e){
            Message.showErrorToast(this, e.getMessage());
        }
    }

    private void deleteAllData(){
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        if(rowsDeleted > 0)
            Message.showPlainTextToast(this, getString(R.string.all_products_deleted));
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
                insertDummyProduct();
                break;
            case R.id.delete_all_data:
                showAlertDialog();
                break;
        }
        return true;
    }

    private void showAlertDialog(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.dialog_delete_all_message);
        alertDialogBuilder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                deleteAllData();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null)
                    dialogInterface.dismiss();
            }
        });

        alertDialogBuilder.create().show();
    }

    /******************* Loader callbacks *******************/
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        return new CursorLoader(this, ProductEntry.CONTENT_URI, null, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mAdapter.changeCursor(cursor);
        if(cursor.getCount() == 0)
            mNoProductsInfoView.setVisibility(View.VISIBLE);
        else
            mNoProductsInfoView.setVisibility(View.GONE);
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.changeCursor(null);
        Log.d("TAG", "Hello from RESET");
    }
}
