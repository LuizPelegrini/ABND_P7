package com.example.android.abnd_p7.util;

import android.content.Context;
import android.widget.Toast;

import com.example.android.abnd_p7.R;
import com.example.android.abnd_p7.data.StoreContract;

public final class Message {

    // Shows an error toast when the input is not validated
    public static void showErrorToast(Context context, String errorString){
        String message;
        switch (errorString){
            case StoreContract.ProductEntry.COLUMN_PRODUCT_NAME:
                message = context.getString(R.string.no_name);
                break;
            case StoreContract.ProductEntry.COLUMN_PRODUCT_PRICE:
                message = context.getString(R.string.negative_price);
                break;
            case StoreContract.ProductEntry.COLUMN_PRODUCT_QUANTITY:
                message = context.getString(R.string.negative_quantity);
                break;
            case StoreContract.ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME:
                message = context.getString(R.string.no_supplier_name);
                break;
            default:
                message = context.getString(R.string.unknown_error);
                break;
        }

        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    // Show a toast message as is
    public static void showPlainTextToast(Context context, String plainMessage){
        Toast.makeText(context, plainMessage, Toast.LENGTH_SHORT).show();
    }

}
