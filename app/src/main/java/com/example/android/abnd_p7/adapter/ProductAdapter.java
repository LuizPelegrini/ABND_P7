package com.example.android.abnd_p7.adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.widget.CursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.abnd_p7.R;
import com.example.android.abnd_p7.data.StoreContract.ProductEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductAdapter extends CursorAdapter {

    static class ViewHolder{
        @BindView(R.id.product_name_text) TextView mProductName;
        @BindView(R.id.product_price_text) TextView mProductPrice;
        @BindView(R.id.product_quantity_text) TextView mProductQuantity;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }

    public ProductAdapter(Context context, Cursor cursor) {
        super(context, cursor, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder viewHolder = (ViewHolder)view.getTag();

        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);

        String name = cursor.getString(nameColumnIndex);
        String price = formatPrice(cursor.getInt(priceColumnIndex));
        String quantity = String.valueOf(cursor.getInt(quantityColumnIndex));

        viewHolder.mProductName.setText(name);
        viewHolder.mProductPrice.setText(price);
        viewHolder.mProductQuantity.setText(quantity);
    }

    private String formatPrice(int price){
        return "0.00";
    }
}
