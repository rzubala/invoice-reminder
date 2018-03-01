package com.zubala.rafal.invoicereminder.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kolarz on 01.03.18.
 */

public class InvoiceDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "invoicesDb.db";

    private static final int VERSION = 1;

    public InvoiceDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "  + InvoiceContract.InvoiceEntry.TABLE_NAME + " (" +
                InvoiceContract.InvoiceEntry._ID                + " INTEGER PRIMARY KEY, " +
                InvoiceContract.InvoiceEntry.COLUMN_DESCRIPTION + " TEXT NOT NULL, " +
                InvoiceContract.InvoiceEntry.COLUMN_AMOUNT      + " REAL NOT NULL" +
                InvoiceContract.InvoiceEntry.COLUMN_CURRENCY    + " TEXT " +
                InvoiceContract.InvoiceEntry.COLUMN_DATE        + " INTEGER NOT NULL" +
                InvoiceContract.InvoiceEntry.COLUMN_PAID        + " BOOLEAN " +
                ");";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + InvoiceContract.InvoiceEntry.TABLE_NAME);
        onCreate(db);
    }
}
