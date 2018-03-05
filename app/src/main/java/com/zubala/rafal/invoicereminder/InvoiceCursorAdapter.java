package com.zubala.rafal.invoicereminder;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.zubala.rafal.invoicereminder.data.DateUtils;
import com.zubala.rafal.invoicereminder.data.InvoiceContract;

import java.util.Date;

/**
 * Created by rzubala on 01.03.18.
 */

public class InvoiceCursorAdapter extends RecyclerView.Adapter<InvoiceCursorAdapter.InvoiceViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    final private InvoiceOnClickHandler mClickHandler;

    public InvoiceCursorAdapter(Context context, InvoiceOnClickHandler handler) {
        this.mContext = context;
        this.mClickHandler = handler;
    }

    @Override
    public InvoiceCursorAdapter.InvoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.invoice_layout, parent, false);

        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InvoiceCursorAdapter.InvoiceViewHolder holder, int position) {
        int idIndex = mCursor.getColumnIndex(InvoiceContract.InvoiceEntry._ID);
        int descriptionIndex = mCursor.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_DESCRIPTION);
        int amountIndex = mCursor.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_AMOUNT);
        int dateIndex = mCursor.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_DATE);
        int currencyIndex = mCursor.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_CURRENCY);
        int paidIndex = mCursor.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_PAID);

        mCursor.moveToPosition(position);

        final int id = mCursor.getInt(idIndex);
        String description = mCursor.getString(descriptionIndex);
        Double amount = mCursor.getDouble(amountIndex);
        Long timestamp = mCursor.getLong(dateIndex);
        String currency = mCursor.getString(currencyIndex);
        boolean paid = mCursor.getInt(paidIndex) > 0;

        Date date = new Date();
        date.setTime(timestamp);

        holder.itemView.setTag(id);
        holder.invoiceDescriptionView.setText(description);
        holder.invoiceDateView.setText(DateUtils.formatDate(mContext, date));
        holder.invoiceAmountView.setText(amount.toString());
        holder.invoiceCurrencyView.setText(" "+currency);

        if (paid) {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPaid));
        } else {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorUnpaid));
        }

    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;
        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    public interface InvoiceOnClickHandler {
        void onClick(long id);
    }

    class InvoiceViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView invoiceDescriptionView;

        TextView invoiceAmountView;

        TextView invoiceCurrencyView;

        TextView invoiceDateView;

        public InvoiceViewHolder(View itemView) {
            super(itemView);

            invoiceDescriptionView = (TextView) itemView.findViewById(R.id.invoiceDescription);
            invoiceAmountView = (TextView) itemView.findViewById(R.id.invoiceAmount);
            invoiceCurrencyView = (TextView) itemView.findViewById(R.id.invoiceCurrency);
            invoiceDateView = (TextView) itemView.findViewById(R.id.invoiceDate);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View itemView) {
            int adapterPosition = getAdapterPosition();
            mCursor.moveToPosition(adapterPosition);
            int idIndex = mCursor.getColumnIndex(InvoiceContract.InvoiceEntry._ID);
            long id = mCursor.getLong(idIndex);
            mClickHandler.onClick(id);
        }
    }
}
