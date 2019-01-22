package com.example.android.abnd_p7;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.abnd_p7.data.StoreContract.ProductEntry;
import com.example.android.abnd_p7.util.Message;
import com.example.android.abnd_p7.util.NumberTextWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_ID = 1;

    private boolean mIsCreationMode;    // This specifies whether this activity is on Creation Mode or Edit Mode
    private Uri mEntryUri;              // This is the Uri coming from the MainActivity when a product is selected
    private int mQuantity;              // The quantity temp variable to increase and decrease

    private String mNameToCompare;
    private int mPriceToCompare;
    private int mQuantityToCompare;
    private String mSupplierNameToCompare;
    private String mSupplierPhoneToCompare;

    @BindView(R.id.product_name_edit_text) TextInputEditText mProductNameEditText;
    @BindView(R.id.price_edit_text) TextInputEditText mProductPriceEditText;
    @BindView(R.id.supplier_name_edit_text) TextInputEditText mSupplierNameEditText;
    @BindView(R.id.supplier_phone_edit_text) TextInputEditText mSupplierPhoneEditText;
    @BindView(R.id.decrease_quantity_button) ImageView mDecreaseQuantityImageView;
    @BindView(R.id.increase_quantity_button) ImageView mIncreaseQuantityImageView;
    @BindView(R.id.quantity_text) TextView mProductQuantityTextView;
    @BindView(R.id.call_supplier_button) Button mCallSupplierButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        ButterKnife.bind(this);

        addListenersToViews();

        // Gets the intent coming from the MainActivity
        Intent intent = getIntent();
        if(intent.getData() != null){
            mIsCreationMode = false;
            mEntryUri = intent.getData();
            getSupportLoaderManager().initLoader(LOADER_ID, null, this);
            mCallSupplierButton.setVisibility(View.VISIBLE);
        }else{
            mIsCreationMode = true;
            mCallSupplierButton.setVisibility(View.GONE);
            setInfoUI("", 0, 0, "","");
        }
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
                return true;
            case R.id.delete_product_action:
                // show confirmation dialog, delete if accepted, go to MainActivity
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                // Check if the form has been changed
                if(isFormDirty()){
                    // If the user wants to discard the changes, navigate back to the MainActivity
                    showUnsavedChangesDialog(new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                        }
                    });
                } else {
                    // Navigate back to parent activity
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
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

    @Override
    public void onBackPressed() {
        // if the form has changed, ask the user if he wants to discard the changes
        if(isFormDirty()){
            showUnsavedChangesDialog(new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    finish();
                }
            });
        }else{
            super.onBackPressed();
        }
    }

    private void setInfoUI(String name, int price, int quantity, String supplierName, String supplierPhone){
        mQuantity = quantity;

        mProductNameEditText.setText(name);
        mNameToCompare = name;
        mProductPriceEditText.setText(String.valueOf(price));
        mPriceToCompare = price;
        mProductQuantityTextView.setText(String.valueOf(mQuantity));
        mQuantityToCompare = mQuantity;
        mSupplierNameEditText.setText(supplierName);
        mSupplierNameToCompare = supplierName;
        mSupplierPhoneEditText.setText(supplierPhone);
        mSupplierPhoneToCompare = supplierPhone;
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

    private void showDeleteConfirmationDialog(){
        // Create a dialog builder
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        // Set it up
        alertDialogBuilder.setMessage(R.string.dialog_delete_message);
        alertDialogBuilder.setPositiveButton(R.string.dialog_yes, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int buttonClicked) {
                deleteProduct();
                finish();
            }
        });
        alertDialogBuilder.setNegativeButton(R.string.dialog_no, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null)
                    dialogInterface.dismiss();
            }
        });

        // Show it
        alertDialogBuilder.create().show();
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener listener){
        // TODO: show a dialog when any change has been made in the form
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(R.string.dialog_unsaved_message);
        alertDialogBuilder.setPositiveButton(R.string.dialog_unsaved_discard, listener);
        alertDialogBuilder.setNegativeButton(R.string.dialog_unsaved_keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if(dialogInterface != null)
                    dialogInterface.dismiss();
            }
        });
        alertDialogBuilder.create().show();
    }

    private boolean isFormDirty(){
        String name = mProductNameEditText.getText().toString();
        int quantity = Integer.parseInt(mProductQuantityTextView.getText().toString());
        // TODO: Get the price edittext and format it to represent a whole number before comparing
        int price = parsePriceText(mProductPriceEditText.getText().toString());
        String supplierName = mSupplierNameEditText.getText().toString();
        String supplierPhone = mSupplierPhoneEditText.getText().toString();

        return !name.equals(mNameToCompare) ||
               quantity != mQuantityToCompare ||
               price != mPriceToCompare ||
               !supplierName.equals(mSupplierNameToCompare) ||
               !supplierPhone.equals(mSupplierPhoneToCompare);
    }

    private void deleteProduct(){
        int rowsAffected = getContentResolver().delete(mEntryUri, null, null);
        if(rowsAffected > 0){
            Message.showPlainTextToast(this, getString(R.string.delete_product_successfully));
        }
    }

    // Parse the formatted text into a whole integer
    private int parsePriceText(String priceString){
        String cleanString = priceString.replaceAll("[$.,]", "");
        return Integer.parseInt(cleanString);
    }


    private void addListenersToViews(){
        // Add a watcher to format the edit text field
        mProductPriceEditText.addTextChangedListener(new NumberTextWatcher(mProductPriceEditText));

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

        // Add a text watcher to the supplier phone edit text to track whether is empty or not
        mSupplierPhoneEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                // if there is no phone number entered, hide the button. Otherwise, show the call supplier button
                if(charSequence.length() == 0)
                    mCallSupplierButton.setVisibility(View.GONE);
                else
                    mCallSupplierButton.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        // Add a click listener responsible to open the phone app to call the supplier
        mCallSupplierButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri phoneUri = Uri.parse("tel:"+mSupplierPhoneEditText.getText().toString());
                Intent intent = new Intent(Intent.ACTION_DIAL, phoneUri);
                startActivity(intent);
            }
        });
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
        // Clear the fields when the data is invalid
        setInfoUI("", 0, 0, "","");
    }
}
