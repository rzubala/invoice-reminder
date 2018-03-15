package com.zubala.rafal.invoicereminder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.preference.PreferenceManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ViewSwitcher;

import com.zubala.rafal.invoicereminder.data.InvoiceContract;
import com.zubala.rafal.invoicereminder.utils.DateUtils;

import java.util.LinkedList;
import java.util.List;

public class MainActivity
        extends AppCompatActivity
        implements LoaderManager.LoaderCallbacks<Cursor>,
        InvoiceCursorAdapter.InvoiceOnClickHandler,
        View.OnClickListener {

    private RecyclerView mRecyclerView;

    private ViewSwitcher mViewSwitcher;

    private InvoiceCursorAdapter mAdapter;

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final int INVOICE_LOADER_ID = 0;

    private Boolean isFabOpen = false;

    private FloatingActionButton fab, fabAddInvoice, fabAddCyclic;

    private Animation fabOpen, fabClose, rotateForward, rotateBackward;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewInvoices);
        mViewSwitcher = (ViewSwitcher) findViewById(R.id.switcher);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new InvoiceCursorAdapter(this, this, mViewSwitcher);
        mRecyclerView.setAdapter(mAdapter);

        fab = (FloatingActionButton)findViewById(R.id.fab);
        fabAddInvoice = (FloatingActionButton)findViewById(R.id.fab_add_invoice);
        fabAddCyclic = (FloatingActionButton)findViewById(R.id.fab_add_cyclic);
        fabOpen = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fab_open);
        fabClose = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fab_close);
        rotateForward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_forward);
        rotateBackward = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.rotate_backward);
        fab.setOnClickListener(this);
        fabAddInvoice.setOnClickListener(this);
        fabAddCyclic.setOnClickListener(this);

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

        ActionBar actionBar = this.getSupportActionBar();
        if (actionBar != null) {
            actionBar.setIcon(R.drawable.ic_sale_time);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }

        getSupportLoaderManager().initLoader(INVOICE_LOADER_ID, null, this);
    }

    @Override
    public void onClick(long id) {
        Intent invoiceDetailIntent = new Intent(MainActivity.this, InvoiceActivity.class);
        Uri uriForInvoiceClicked = InvoiceContract.InvoiceEntry.buildInvoiceUriWithId(id);
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
            list.add(""+ DateUtils.getSqlSelectionForToday());
            list.add(""+0);
        }
        String[] selectionArguments = list.toArray(new String[list.size()]);

        return selectionArguments;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        switch (id){
            case R.id.fab:
                handleFabAnimation();
                break;
            case R.id.fab_add_invoice:
                Intent startInvoiceActivity = new Intent(MainActivity.this, InvoiceActivity.class);
                startActivity(startInvoiceActivity);
                break;
            case R.id.fab_add_cyclic:

                break;
        }
    }

    private void handleFabAnimation(){
        if(isFabOpen){
            fab.startAnimation(rotateBackward);
            fabAddInvoice.startAnimation(fabClose);
            fabAddCyclic.startAnimation(fabClose);
            fabAddInvoice.setClickable(false);
            fabAddCyclic.setClickable(false);
            isFabOpen = false;
        } else {
            fab.startAnimation(rotateForward);
            fabAddInvoice.startAnimation(fabOpen);
            fabAddCyclic.startAnimation(fabOpen);
            fabAddInvoice.setClickable(true);
            fabAddCyclic.setClickable(true);
            isFabOpen = true;
        }
    }
}
