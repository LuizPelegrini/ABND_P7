package com.example.android.abnd_p7.adapter;

import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.widget.CursorAdapter;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.abnd_p7.DetailsActivity;
import com.example.android.abnd_p7.R;
import com.example.android.abnd_p7.data.StoreContract;

import java.text.NumberFormat;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductRecyclerAdapter extends RecyclerView.Adapter<ProductRecyclerAdapter.ViewHolder> {

    private static final String TAG = "ProductRecyclerAdapter";

    private CursorAdapter mCursorAdapter;
    private Context mContext;
    private NumberFormat mNumberFormat;

    public ProductRecyclerAdapter(Context context, Cursor cursor){
        mContext = context;
        mCursorAdapter = new ProductAdapter(context, cursor);
        mNumberFormat = NumberFormat.getCurrencyInstance(Locale.US);
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.product_name_text) TextView mProductName;
        @BindView(R.id.product_price_text) TextView mProductPrice;
        @BindView(R.id.product_quantity_text) TextView mProductQuantity;

        private long id;

        public ViewHolder(@NonNull View itemView, Context context) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            // Define click listener for each item in the recycler view
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // Creates the intent
                    Intent intent = new Intent(context, DetailsActivity.class);

                    Log.d(TAG, "Id: " + id);

                    // Creates the uri based on the product id
                    Uri uri = ContentUris.withAppendedId(StoreContract.ProductEntry.CONTENT_URI, id);

                    // Put the uri in the intent
                    intent.setDataAndType(uri, context.getContentResolver().getType(uri));

                    // Starts the DetailsActivity with this intent
                    context.startActivity(intent);
                }
            });
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int position) {
        return new ViewHolder(mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), viewGroup), mContext);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        // Get the cursor and moves it to the item position I want to show
        Cursor cursor = mCursorAdapter.getCursor();
        cursor.moveToPosition(position);

        // Gets the index of columns...
        int nameColumnIndex = cursor.getColumnIndex(StoreContract.ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(StoreContract.ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(StoreContract.ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int idColumnIndex = cursor.getColumnIndex(StoreContract.ProductEntry._ID);

        // To use them to fetch the data from the column
        String name = cursor.getString(nameColumnIndex);
        String price = formatPrice(cursor.getInt(priceColumnIndex));
        String quantity = String.valueOf(cursor.getInt(quantityColumnIndex));
        long id = cursor.getLong(idColumnIndex);

        // and set the ViewHolder's views data
        viewHolder.mProductName.setText(name);
        viewHolder.mProductPrice.setText(price);
        viewHolder.mProductQuantity.setText(quantity);
        viewHolder.id = id;
    }

    @Override
    public int getItemCount() {
        return mCursorAdapter.getCount();
    }

    /** Called to set the cursor data */
    public void changeCursor(Cursor newCursor){
        mCursorAdapter.swapCursor(newCursor);
        notifyDataSetChanged();
    }

    /** Formats the price value so it shows with two decimal digits */
    private String formatPrice(int price){

        double d = price / 100d;

        return mNumberFormat.format(d);
//        int dollars = price / 100;
//
//        int cents = price % 100;
//        String centsString = "";
//
//        if(cents < 10){
//            centsString = "0";
//        }
//
//        centsString += String.valueOf(cents);
//
//        return String.valueOf(dollars) + "." + centsString;
    }
}
