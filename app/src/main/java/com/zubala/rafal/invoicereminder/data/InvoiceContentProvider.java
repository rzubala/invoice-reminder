package com.zubala.rafal.invoicereminder.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

/**
 * Created by kolarz on 01.03.18.
 */

public class InvoiceContentProvider extends ContentProvider {

    public static final int INVOICES = 100;
    public static final int INVOICE_WITH_ID = 101;

    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private InvoiceDbHelper mInvoiceDbHelper;

    public static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(InvoiceContract.AUTHORITY, InvoiceContract.PATH_INVOICES, INVOICES);
        uriMatcher.addURI(InvoiceContract.AUTHORITY, InvoiceContract.PATH_INVOICES + "/#", INVOICE_WITH_ID);
        return uriMatcher;
    }

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mInvoiceDbHelper = new InvoiceDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        final SQLiteDatabase db = mInvoiceDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor retCursor;
        switch (match) {
            case INVOICES:
                retCursor =  db.query(InvoiceContract.InvoiceEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        InvoiceContract.InvoiceEntry.COLUMN_DATE + " ASC");
                break;
            case INVOICE_WITH_ID:
                String idString = uri.getLastPathSegment();
                String[] selectionArguments = new String[]{idString};
                retCursor = db.query(
                        InvoiceContract.InvoiceEntry.TABLE_NAME,
                        projection,
                        InvoiceContract.InvoiceEntry._ID + " = ? ",
                        selectionArguments,
                        null,
                        null,
                        InvoiceContract.InvoiceEntry.COLUMN_DATE + " ASC");
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        retCursor.setNotificationUri(getContext().getContentResolver(), uri);
        return retCursor;
    }

    @Override
    public Uri insert(@NonNull Uri uri, ContentValues values) {
        final SQLiteDatabase db = mInvoiceDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri returnUri;
        switch (match) {
            case INVOICES:
                long id = db.insert(InvoiceContract.InvoiceEntry.TABLE_NAME, null, values);
                if ( id > 0 ) {
                    returnUri = ContentUris.withAppendedId(InvoiceContract.InvoiceEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return returnUri;
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        return 0;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        return null;
    }

}
