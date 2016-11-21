package com.udacity.stockhawk.ui;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Iris on 17/11/2016.
 */

public class DetailActivity extends AppCompatActivity {
    private Uri mStockUri;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mStockUri = getIntent().getData();

        Log.v("DETAIL", mStockUri.toString());
    }
}
