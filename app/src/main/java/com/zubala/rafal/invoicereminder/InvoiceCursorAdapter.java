package com.zubala.rafal.invoicereminder;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by rzubala on 01.03.18.
 */

public class InvoiceCursorAdapter extends RecyclerView.Adapter<InvoiceCursorAdapter.InvoiceViewHolder> {

    private Cursor mCursor;
    private Context mContext;

    public InvoiceCursorAdapter(Context context) {
        this.mContext = context;
    }

    @Override
    public InvoiceCursorAdapter.InvoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.invoice_layout, parent, false);

        return new InvoiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(InvoiceCursorAdapter.InvoiceViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    class InvoiceViewHolder extends RecyclerView.ViewHolder {

        TextView invoiceDescriptionView;

        TextView invoiceAmountView;

        TextView invoiceDateView;

        public InvoiceViewHolder(View itemView) {
            super(itemView);

            /*
            invoiceDescriptionView = (TextView) itemView.findViewById(R.id.);
            invoiceAmountView = (TextView) itemView.findViewById(R.id.);
            invoiceDateView = (TextView) itemView.findViewById(R.id.);
            */
        }
    }
}
