package com.zubala.rafal.invoicereminder;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.zubala.rafal.invoicereminder.data.InvoiceContract;
import com.zubala.rafal.invoicereminder.databinding.ActivityInvoiceBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InvoiceActivity extends AppCompatActivity {

    private Calendar myCalendar;

    ActivityInvoiceBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invoice);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        myCalendar = Calendar.getInstance();

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_invoice);

        attachDatePicker();
    }

    private void attachDatePicker() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateField();
            }

        };
        mBinding.dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(InvoiceActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateDateField() {
        SimpleDateFormat sdf = new SimpleDateFormat(getString(R.string.dateFormat), Locale.getDefault());
        mBinding.dateField.setText(sdf.format(myCalendar.getTime()));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickAddInvoice(View view) {
        String dateStr = mBinding.dateField.getText().toString();
        String description = mBinding.descriptionField.getText().toString();
        String amountStr = mBinding.numberField.getText().toString();
        String currency = mBinding.currencyField.getText().toString();
        boolean paid = mBinding.checkBox.isChecked();

        if (dateStr.isEmpty() || description.isEmpty() || amountStr.isEmpty()) {
            Toast.makeText(getBaseContext(), getString(R.string.newInvoiceError), Toast.LENGTH_LONG).show();
            return;
        }

        Double amount = null;
        try {
            amount = Double.valueOf(amountStr);
        } catch (Exception ex) {
            Toast.makeText(getBaseContext(), getString(R.string.invoiceAmountError), Toast.LENGTH_LONG).show();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_DATE, dateStr);
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_DESCRIPTION, description);
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_CURRENCY, currency);
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_AMOUNT, amount);
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_PAID, paid);

        Uri uri = getContentResolver().insert(InvoiceContract.InvoiceEntry.CONTENT_URI, contentValues);

        finish();
    }
}
