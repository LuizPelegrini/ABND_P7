package com.example.android.abnd_p7;

import android.content.ContentUris;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class DetailsActivity extends AppCompatActivity {

    @BindView(R.id.decrease_quantity_button) ImageView mDecreaseQuantityImageView;
    @BindView(R.id.increase_quantity_button) ImageView mIncreaseQuantityImageView;
    @BindView(R.id.quantity_text) TextView mProductQuantityTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details_layout);
        ButterKnife.bind(this);

        mDecreaseQuantityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductQuantityTextView.setText("0");
            }
        });

        mIncreaseQuantityImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mProductQuantityTextView.setText("1");
            }
        });
        // Gets a possible intent coming from the MainActivity
        // TODO: Query the database using a loader to retrieve info from the product
        Intent intent = getIntent();
        Log.d("TAG", "ID: " + String.valueOf(ContentUris.parseId(intent.getData())));
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
                // TODO: insert the product into the database, show SUCCESS message, go to MainActivity
                break;
            case R.id.delete_product_action:
                // TODO: show confirmation dialog, delete if accepted, go to MainActivity
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        //TODO: hide the delete option if the activity has been initialized at the creation mode
        return super.onPrepareOptionsMenu(menu);
    }
}
