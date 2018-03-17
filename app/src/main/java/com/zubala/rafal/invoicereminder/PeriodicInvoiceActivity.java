package com.zubala.rafal.invoicereminder;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.databinding.DataBindingUtil;
import android.graphics.Color;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.Toast;

import com.zubala.rafal.invoicereminder.data.InvoiceContract;
import com.zubala.rafal.invoicereminder.databinding.ActivityInvoiceBinding;
import com.zubala.rafal.invoicereminder.databinding.ActivityPeriodicInvoiceBinding;
import com.zubala.rafal.invoicereminder.utils.DateUtils;
import com.zubala.rafal.invoicereminder.utils.PeriodicUtils;

import java.util.Calendar;
import java.util.Currency;
import java.util.Date;
import java.util.Locale;

public class PeriodicInvoiceActivity extends AppCompatActivity {

    private ActivityPeriodicInvoiceBinding mBinding;

    private Calendar myCalendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_periodic_invoice);

        myCalendar = Calendar.getInstance();

        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_periodic_invoice);

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setIcon(R.drawable.ic_sale_time);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        mBinding.numberPicker.setMinValue(1);
        mBinding.numberPicker.setMaxValue(12);
        mBinding.numberPicker.setWrapSelectorWheel(true);

        mBinding.radioButtonMonth.setChecked(true);

        setLocaleCurrencyCode();

        attachDatePicker();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            NavUtils.navigateUpFromSameTask(this);
        }
        MainActivity.hideKeyboard(this);
        return super.onOptionsItemSelected(item);
    }

    private void setLocaleCurrencyCode() {
        Locale defaultLocale = Locale.getDefault();
        Currency currency = Currency.getInstance(defaultLocale);
        if (currency == null) {
            return;
        }
        String code = currency.getCurrencyCode();
        if (code == null) {
            return;
        }
        mBinding.currencyField.setText(code);
    }

    private void attachDatePicker() {
        final DatePickerDialog.OnDateSetListener dateFrom = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            myCalendar.set(Calendar.YEAR, year);
            myCalendar.set(Calendar.MONTH, monthOfYear);
            myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateField(mBinding.dateFieldFrom, myCalendar.getTime());
            }

        };
        mBinding.dateFieldFrom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            new DatePickerDialog(PeriodicInvoiceActivity.this, dateFrom, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        final DatePickerDialog.OnDateSetListener dateTo = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateField(mBinding.dateFieldTo, myCalendar.getTime());
            }

        };
        mBinding.dateFieldTo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(PeriodicInvoiceActivity.this, dateTo, myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }



    private void updateDateField(EditText field, Date date) {
        field.setText(DateUtils.formatDate(this, date));
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
        MainActivity.hideKeyboard(this);

        String dateFromStr = mBinding.dateFieldFrom.getText().toString();
        String dateToStr = mBinding.dateFieldTo.getText().toString();
        String description = mBinding.descriptionField.getText().toString();
        String amountStr = mBinding.numberField.getText().toString();
        String currency = mBinding.currencyField.getText().toString();
        boolean paid = mBinding.checkBox.isChecked();
        int frequency = mBinding.numberPicker.getValue();
        boolean month = mBinding.radioButtonMonth.isChecked();
        boolean week = mBinding.radioButtonWeek.isChecked();
        boolean day = mBinding.radioButtonDay.isChecked();

        if (description.isEmpty() || amountStr.isEmpty() || dateFromStr.isEmpty() || dateToStr.isEmpty()) {
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

        Date dateFrom = DateUtils.parseDate(this, dateFromStr);
        if (dateFrom == null) {
            Toast.makeText(getBaseContext(), getString(R.string.newDateError), Toast.LENGTH_LONG).show();
            return;
        }
        long timestampFrom = DateUtils.toUTCTimestamp(dateFrom);

        Date dateTo = DateUtils.parseDate(this, dateToStr);
        if (dateTo == null) {
            Toast.makeText(getBaseContext(), getString(R.string.newDateError), Toast.LENGTH_LONG).show();
            return;
        }
        long timestampTo = DateUtils.toUTCTimestamp(dateTo);

        if (timestampTo <= timestampFrom) {
            Toast.makeText(getBaseContext(), getString(R.string.dates_error), Toast.LENGTH_LONG).show();
            return;
        }

        PeriodicUtils.PeriodicType type = PeriodicUtils.PeriodicType.MONTH;
        if (week) {
            type = PeriodicUtils.PeriodicType.WEEK;
        } else if (day) {
            type = PeriodicUtils.PeriodicType.DAY;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_DESCRIPTION, description);
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_CURRENCY, currency);
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_AMOUNT, amount);
        contentValues.put(InvoiceContract.InvoiceEntry.COLUMN_PAID, paid);

        PeriodicUtils.createPeriodicPayments(this, timestampFrom, timestampTo, type, frequency, contentValues);

        finish();
    }
}
