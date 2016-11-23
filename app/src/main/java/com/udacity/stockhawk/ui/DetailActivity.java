package com.udacity.stockhawk.ui;

import android.database.Cursor;
import android.graphics.Color;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.udacity.stockhawk.R;
import com.udacity.stockhawk.data.Contract;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Created by Iris on 17/11/2016.
 */

public class DetailActivity extends AppCompatActivity implements
        LoaderManager.LoaderCallbacks<Cursor> {
    private final String LOG_TAG = DetailActivity.class.getSimpleName();
    private static final int STOCK_SYMBOL_LOADER = 0;
    private String mStockSymbol;
    private Uri mStockUri;
    private ArrayList<Entry> mStockData;
    private LineChart mChart;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Bundle extras = getIntent().getExtras();
        mStockSymbol = extras.getString(getString(R.string.intent_symbol));
        mStockUri = Contract.Quote.makeUriForStock(mStockSymbol);

        getSupportLoaderManager().initLoader(STOCK_SYMBOL_LOADER, null, this);

        setContentView(R.layout.activity_detail);
        setTitle(mStockSymbol);

        mChart = (LineChart) findViewById(R.id.stock_line_chart);

        // no description text
        mChart.getDescription().setEnabled(false);

        // get the legend (only possible after setting data)
        Legend l = mChart.getLegend();
        l.setEnabled(false);


        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setCenterAxisLabels(true);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setValueFormatter(new IAxisValueFormatter() {

            private SimpleDateFormat mFormat = new SimpleDateFormat("dd MMM yyyy");

            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return mFormat.format(new Date((long) value));
            }
        });

        YAxis yAxis = mChart.getAxisLeft();
        yAxis.setTextColor(Color.WHITE);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if ( mStockUri == null ) { return null; }

        return new CursorLoader(this,
                mStockUri,
                Contract.Quote.QUOTE_COLUMNS,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mStockData = new ArrayList<>();
        if (data != null && data.moveToFirst()) {
            String[] history = data.getString(Contract.Quote.POSITION_HISTORY)
                    .split("\\n+");

            // reverse the array
            List<String> listHistory = Arrays.asList(history);
            Collections.reverse(listHistory);
            history = (String[]) listHistory.toArray();

            for (String entry: history) {
                String[] set = entry.split(",\\s+");
                long time = Long.parseLong(set[0]);
                float value = Float.valueOf(set[1]);

                mStockData.add(new Entry(time, value));

            }

            LineDataSet dataSet = new LineDataSet(mStockData, mStockSymbol);
            dataSet.setLineWidth(2f);
            dataSet.setDrawCircles(false);
            dataSet.setDrawFilled(true);
            dataSet.setFillAlpha(65);

            LineData lineData = new LineData(dataSet);
            lineData.setValueTextColor(Color.WHITE);
            lineData.setValueTextSize(9f);

            mChart.setData(lineData);
            mChart.invalidate();

        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mStockData = null;
    }
}
