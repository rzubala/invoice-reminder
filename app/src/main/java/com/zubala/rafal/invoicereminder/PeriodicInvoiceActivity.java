package com.zubala.rafal.invoicereminder;

import android.graphics.Color;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.NumberPicker;
import android.widget.RadioButton;

public class PeriodicInvoiceActivity extends AppCompatActivity {

    private NumberPicker mNumberPicker;

    private RadioButton mMonth, mWeek, mDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_periodic_invoice);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_sale_time);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mNumberPicker = (NumberPicker) findViewById(R.id.numberPicker);
        mNumberPicker.setMinValue(1);
        mNumberPicker.setMaxValue(12);
        mNumberPicker.setWrapSelectorWheel(true);
        mNumberPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal){
                //tv.setText("Selected Number : " + newVal);
            }
        });

        mMonth = (RadioButton) findViewById(R.id.radioButtonMonth);
        mWeek = (RadioButton) findViewById(R.id.radioButtonWeek);
        mDay = (RadioButton) findViewById(R.id.radioButtonDay);

        mMonth.setChecked(true);
    }
}
