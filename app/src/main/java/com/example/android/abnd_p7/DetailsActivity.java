package com.example.android.abnd_p7;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.abnd_p7.data.StoreContract.ProductEntry;
import com.example.android.abnd_p7.util.Message;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;

    private boolean mIsCreationMode;    // This specifies whether this activity is on Creation Mode or Edit Mode
    private Uri mEntryUri;              // This is the Uri coming from the MainActivity when a product is selected
    private int mQuantity;              // The quantity temp variable to increase and decrease

    @BindView(R.id.product_name_edit_text) TextInputEditText mProductNameEditText;
    @BindView(R.id.price_edit_text) TextInputEditText mProductPriceEditText;
    @BindView(R.id.supplier_name_edit_text) TextInputEditText mSupplierNameEditText;
    @BindView(R.id.supplier_phone_edit_text) TextInputEditText mSupplierPhoneEditText;
    @BindView(R.id.decrease_quantity_button) ImageView mDecreaseQuantityImageView;
    @BindView(R.id.increase_quantity_button) ImageView mIncreaseQuantityImageView;
    @BindView(R.id.quantity_text) TextView mProductQuantityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        ButterKnife.bind(this);

        // Gets the intent coming from the MainActivity
        Intent intent = getIntent();
        if(intent.getData() != null){
            mIsCreationMode = false;
            mEntryUri = intent.getData();
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
        }else{
            mIsCreationMode = true;
            setInfoUI("", 0, 0, "","");
        }

        mDecreaseQuantityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mQuantity > 0){
                    mQuantity--;
                    mProductQuantityTextView.setText(String.valueOf(mQuantity));
                }
            }
        });

        mIncreaseQuantityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mQuantity++;
                mProductQuantityTextView.setText(String.valueOf(mQuantity));
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.details_activity_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.save_product_action:
                // insert the product into the database, show SUCCESS message, go to MainActivity
                if(saveProduct())
                    finish();
                break;
            case R.id.delete_product_action:
                // TODO: show confirmation dialog, delete if accepted, go to MainActivity
//                deleteProduct();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        // hide the delete option if the activity has been initialized at the creation mode
        if(mIsCreationMode)
            menu.findItem(R.id.delete_product_action).setVisible(false);

        return super.onPrepareOptionsMenu(menu);
    }

    private void setInfoUI(String name, int price, int quantity, String supplierName, String supplierPhone){
        mQuantity = quantity;

        mProductNameEditText.setText(name);
        mProductPriceEditText.setText(String.valueOf(price));
        mProductQuantityTextView.setText(String.valueOf(mQuantity));
        mSupplierNameEditText.setText(supplierName);
        mSupplierPhoneEditText.setText(supplierPhone);
    }

    // Create contentValue object and insert the product into the database
    private boolean saveProduct(){
        boolean success = false;

        String productName = mProductNameEditText.getText().toString();
        int productPrice = parsePriceText(mProductPriceEditText.getText().toString());
        int productQuantity = Integer.parseInt(mProductQuantityTextView.getText().toString());
        String supplierName = mSupplierNameEditText.getText().toString();
        String supplierPhone = mSupplierPhoneEditText.getText().toString();

        ContentValues contentValues = new ContentValues();
        contentValues.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, supplierName);
        contentValues.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE, supplierPhone);

        try {
            if(mIsCreationMode) {
                getContentResolver().insert(ProductEntry.CONTENT_URI, contentValues);
                Message.showPlainTextToast(this, getString(R.string.product_added, productName));
            } else {
                getContentResolver().update(mEntryUri, contentValues, null, null);
                Message.showPlainTextToast(this, getString(R.string.product_updated, productName));
            }
            success = true;
        }catch(SQLException e){
            Message.showErrorToast(this, e.getMessage());
        }catch (IllegalArgumentException e){
            Message.showPlainTextToast(this, e.getMessage());
        }

        return success;
    }

    private int parsePriceText(String priceString){
        return Integer.parseInt(priceString);
    }


    /*************************** LOADER CALLBACKS *********************************/

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int i, @Nullable Bundle bundle) {
        String[] projection = new String[]{
            ProductEntry._ID,
            ProductEntry.COLUMN_PRODUCT_NAME,
            ProductEntry.COLUMN_PRODUCT_PRICE,
            ProductEntry.COLUMN_PRODUCT_QUANTITY,
            ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
            ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE
        };
        return new CursorLoader(this, mEntryUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        int columnNameIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int columnPriceIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int columnQuantityIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int columnSupplierNameIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        int columnSupplierPhoneIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE);

        if(cursor.moveToNext()) {
            String name = cursor.getString(columnNameIndex);
            int price = cursor.getInt(columnPriceIndex);
            int quantity = cursor.getInt(columnQuantityIndex);
            String supplierName = cursor.getString(columnSupplierNameIndex);
            String supplierPhone = cursor.getString(columnSupplierPhoneIndex);

            setInfoUI(name, price, quantity, supplierName, supplierPhone);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
