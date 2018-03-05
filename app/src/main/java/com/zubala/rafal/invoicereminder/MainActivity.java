package com.zubala.rafal.invoicereminder;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.zubala.rafal.invoicereminder.data.InvoiceContract;
import com.zubala.rafal.invoicereminder.data.InvoiceDbHelper;
import com.zubala.rafal.invoicereminder.data.TestUtil;

import java.util.LinkedList;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        InvoiceCursorAdapter.InvoiceOnClickHandler {

    private RecyclerView mRecyclerView;

    private InvoiceCursorAdapter mAdapter;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int INVOICE_LOADER_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewInvoices);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new InvoiceCursorAdapter(this, this);
        mRecyclerView.setAdapter(mAdapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent startInvoiceActivity = new Intent(MainActivity.this, InvoiceActivity.class);
                startActivity(startInvoiceActivity);
            }
        });

        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }
            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {
                int id = (int) viewHolder.itemView.getTag();
                String stringId = Integer.toString(id);
                Uri uri = InvoiceContract.InvoiceEntry.CONTENT_URI;
                uri = uri.buildUpon().appendPath(stringId).build();

                getContentResolver().delete(uri, null, null);
                getSupportLoaderManager().restartLoader(INVOICE_LOADER_ID, null, MainActivity.this);
            }
        }).attachToRecyclerView(mRecyclerView);


        //TODO only for tests
        /*
        InvoiceDbHelper dbHelper = new InvoiceDbHelper(this);
        SQLiteDatabase mDb = dbHelper.getWritableDatabase();
        TestUtil.insertFakeData(mDb);
        TestUtil.clearData(mDb);
        */

        getSupportLoaderManager().initLoader(INVOICE_LOADER_ID, null, this);
    }

    @Override
    public void onClick(long id) {
        Intent invoiceDetailIntent = new Intent(MainActivity.this, InvoiceActivity.class);
        Uri uriForInvoiceClicked = InvoiceContract.InvoiceEntry.buildWeatherUriWithId(id);
        invoiceDetailIntent.setData(uriForInvoiceClicked);
        startActivity(invoiceDetailIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Intent startSettingsActivity = new Intent(this, SettingsActivity.class);
            startActivity(startSettingsActivity);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(INVOICE_LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {
            Cursor mInvoiceData = null;
            @Override
            protected void onStartLoading() {
                if (mInvoiceData != null) {
                    deliverResult(mInvoiceData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(InvoiceContract.InvoiceEntry.CONTENT_URI,
                            null,
                            getSelectionArgs(),
                            getSelectionArguments(),
                            InvoiceContract.InvoiceEntry.COLUMN_DATE);
                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mInvoiceData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    private String getSelectionArgs() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showPaid = sharedPreferences.getBoolean(getString(R.string.pref_show_paid_key), getResources().getBoolean(R.bool.pref_show_paid));
        boolean showHistory = sharedPreferences.getBoolean(getString(R.string.pref_show_history_key), getResources().getBoolean(R.bool.pref_show_history));

        String selection = "";
        if (!showPaid) {
            selection = InvoiceContract.InvoiceEntry.COLUMN_PAID + " = ? ";
        }
        if (!showHistory) {
            if (!selection.isEmpty()) {
                selection += " and ";
            }
            selection += " ( " + InvoiceContract.InvoiceEntry.COLUMN_DATE + " >= ? or " + InvoiceContract.InvoiceEntry.COLUMN_PAID + " = ? ) ";
        }
        return selection;
    }

    private String[] getSelectionArguments() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean showPaid = sharedPreferences.getBoolean(getString(R.string.pref_show_paid_key), getResources().getBoolean(R.bool.pref_show_paid));
        boolean showHistory = sharedPreferences.getBoolean(getString(R.string.pref_show_history_key), getResources().getBoolean(R.bool.pref_show_history));

        List<String> list = new LinkedList<String>();
        if (!showPaid) {
            list.add(""+0);
        }
        if (!showHistory) {
            list.add(""+InvoiceContract.InvoiceEntry.getSqlSelectionForTodayOnwards());
            list.add(""+0);
        }
        String[] selectionArguments = list.toArray(new String[list.size()]);

        return selectionArguments;
    }
}
