package com.zubala.rafal.invoicereminder.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import com.zubala.rafal.invoicereminder.utils.DateUtils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rzubala on 01.03.18.
 */

public class TestUtil {

    private static final String[] titlesEn = new String[] {
        "Electricity bill",
        "Water bill",
        "Phone bill",
        "Dry cleaning bill",
        "Cell phone bill",
        "Invoice No. 12345/2018",
        "Gas bill",
        "Loan repayment",
        "Internet bill",
        "Credit card repayment"
    };

    private static final String[] titlesPl = new String[] {
        "Faktura za prąd",
        "Faktura za wodę",
        "Faktura za telefon",
        "Opłata za pralnię",
        "Faktura za telefon komórkowy",
        "Faktura nr 12345/2018",
        "Faktura za gaz",
        "Spłata pożyczki",
        "Faktura za internet",
        "Spłata karty kredytowej"
    };

    private static final Double[] amounts = new Double[] {
        78.32,
        233.12,
        49.99,
        5.99,
        78.98,
        1235.32,
        23.87,
        500.00,
        60.00,
        234.22
    };

    private static final int[] offsets = new int[] {
        1,
        5,
        9,
        -1,
        3,
        30,
        -2,
        7,
        6,
        10
    };

    private static final boolean[] paid = new boolean[] {
            false,
            false,
            false,
            false,
            true,
            false,
            false,
            false,
            true,
            true
    };


    public static void insertFakeData(SQLiteDatabase db, boolean pl) {
        if (db == null) {
            return;
        }
        clearData(db);

        List<ContentValues> list = new ArrayList<ContentValues>();

        String[] titles;
        String currency;
        if (pl) {
            titles = titlesPl;
            currency = "PLN";
        } else {
            titles = titlesEn;
            currency = "USD";
        }

        long nowTimestamp = InvoiceContract.InvoiceEntry.getSqlSelectionForToday();

        for (int i=0;i<titles.length;i++) {
            ContentValues cv = new ContentValues();
            cv.put(InvoiceContract.InvoiceEntry.COLUMN_DESCRIPTION, titles[i]);
            cv.put(InvoiceContract.InvoiceEntry.COLUMN_AMOUNT, amounts[i]);
            cv.put(InvoiceContract.InvoiceEntry.COLUMN_PAID, paid[i]);
            cv.put(InvoiceContract.InvoiceEntry.COLUMN_CURRENCY, currency);
            long timestamp = nowTimestamp + offsets[i] * InvoiceContract.InvoiceEntry.DAY_IN_MILLIS;
            cv.put(InvoiceContract.InvoiceEntry.COLUMN_DATE, timestamp);
            list.add(cv);
        }

        try {
            db.beginTransaction();
            db.delete (InvoiceContract.InvoiceEntry.TABLE_NAME,null,null);
            for(ContentValues c:list){
                db.insert(InvoiceContract.InvoiceEntry.TABLE_NAME, null, c);
            }
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
        }
        finally {
            db.endTransaction();
        }
    }

    public static void clearData(SQLiteDatabase db) {
        if (db == null) {
            return;
        }
        try {
            db.beginTransaction();
            db.delete (InvoiceContract.InvoiceEntry.TABLE_NAME,null,null);
            db.setTransactionSuccessful();
        }
        catch (SQLException e) {
        }
        finally {
            db.endTransaction();
        }
    }
}
