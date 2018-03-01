package com.zubala.rafal.invoicereminder.data;

import android.content.ContentValues;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by rzubala on 01.03.18.
 */

public class TestUtil {
    public static void insertFakeData(SQLiteDatabase db) {
        if (db == null) {
            return;
        }
        List<ContentValues> list = new ArrayList<ContentValues>();

        for (int i=0;i<30;i++) {
            ContentValues cv = new ContentValues();
            cv.put(InvoiceContract.InvoiceEntry.COLUMN_DESCRIPTION, "Faktura nr "+i);
            cv.put(InvoiceContract.InvoiceEntry.COLUMN_AMOUNT, 125.34 + i + (i/10));
            cv.put(InvoiceContract.InvoiceEntry.COLUMN_CURRENCY, "PLN");
            cv.put(InvoiceContract.InvoiceEntry.COLUMN_DATE, new Date().getTime());
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
}
