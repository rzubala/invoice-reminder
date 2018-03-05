package com.zubala.rafal.invoicereminder;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.app.NavUtils;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Toast;

import com.zubala.rafal.invoicereminder.data.DateUtils;
import com.zubala.rafal.invoicereminder.data.InvoiceContract;
import com.zubala.rafal.invoicereminder.databinding.ActivityInvoiceBinding;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class InvoiceActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private Calendar myCalendar;

    private ActivityInvoiceBinding mBinding;

    private Uri mUri;

    private Integer id = null;

    private static final String TAG = InvoiceActivity.class.getSimpleName();

    private static final int ID_INVOICE_LOADER = 577;

    public static final String[] INVOICE_DETAIL_PROJECTION = {
            InvoiceContract.InvoiceEntry.COLUMN_DATE,
            InvoiceContract.InvoiceEntry.COLUMN_DESCRIPTION,
            InvoiceContract.InvoiceEntry.COLUMN_AMOUNT,
            InvoiceContract.InvoiceEntry.COLUMN_CURRENCY,
            InvoiceContract.InvoiceEntry.COLUMN_PAID,
            InvoiceContract.InvoiceEntry._ID

    };

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

        mUri = getIntent().getData();
        if (mUri == null) {
            Log.d(TAG, "mURI null");
        } else {
            Log.d(TAG, "mURI: " + mUri.toString());
            mBinding.button.setText(R.string.update);
            getSupportLoaderManager().initLoader(ID_INVOICE_LOADER, null, this);
        }

        attachDatePicker();
    }

    private void attachDatePicker() {
        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateField(myCalendar.getTime());
            }

        };
        mBinding.dateField.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(InvoiceActivity.this, date, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }

    private void updateDateField(Date date) {
        mBinding.dateField.setText(DateUtils.formatDate(this, date));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        return super.onOptionsItemSelected(item);
    }

    public void onClickPaid(View view) {
        boolean paid = mBinding.checkBox.isChecked();
        if (paid) {
            mBinding.checkBox.setText(R.string.paid_label);
        } else {
            mBinding.checkBox.setText(R.string.not_paid_label);
        }
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

        Date date = DateUtils.parseDate(this, dateStr);
        if (date == null) {
            Toast.makeText(getBaseContext(), getString(R.string.newDateError), Toast.LENGTH_LONG).show();
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
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_DATE, date.getTime());
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_DESCRIPTION, description);
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_CURRENCY, currency);
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_AMOUNT, amount);
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_PAID, paid);

        if (id == null) {
            Uri uri = getContentResolver().insert(InvoiceContract.InvoiceEntry.CONTENT_URI, contentValues);
        } else {
            Uri uri = InvoiceContract.InvoiceEntry.CONTENT_URI;
            uri = uri.buildUpon().appendPath(""+id).build();
            getContentResolver().update(uri, contentValues, null, null);
        }

        finish();
    }

    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle loaderArgs) {
        switch (loaderId) {
            case ID_INVOICE_LOADER:
                return new CursorLoader(this,
                        mUri,
                        INVOICE_DETAIL_PROJECTION,
                        null,
                        null,
                        null);
            default:
                throw new RuntimeException("Loader Not Implemented: " + loaderId);
        }
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        boolean cursorHasValidData = false;
        if (data != null && data.moveToFirst()) {
            cursorHasValidData = true;
        }
        if (!cursorHasValidData) {
            return;
        }
        String description = data.getString(data.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_DESCRIPTION));
        mBinding.descriptionField.setText(description);

        Double amount = data.getDouble(data.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_AMOUNT));
        mBinding.numberField.setText(amount.toString());

        String currency = data.getString(data.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_CURRENCY));
        mBinding.currencyField.setText(currency);

        boolean paid = data.getInt(data.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_PAID)) > 0;
        mBinding.checkBox.setChecked(paid);

        Long timestamp = data.getLong(data.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_DATE));
        Date date = new Date();
        date.setTime(timestamp);
        updateDateField(date);

        id = data.getInt(data.getColumnIndex(InvoiceContract.InvoiceEntry._ID));

        if (paid) {
            mBinding.checkBox.setText(R.string.paid_label);
        } else {
            mBinding.checkBox.setText(R.string.not_paid_label);
        }

        Log.d(TAG, "loaded id:" + id);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }
}
