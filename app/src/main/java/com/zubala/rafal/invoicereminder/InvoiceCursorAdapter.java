package com.zubala.rafal.invoicereminder;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.zubala.rafal.invoicereminder.sync.ReminderTasks;
import com.zubala.rafal.invoicereminder.utils.AlarmUtils;
import com.zubala.rafal.invoicereminder.utils.DateUtils;
import com.zubala.rafal.invoicereminder.data.InvoiceContract;
import com.zubala.rafal.invoicereminder.utils.NumberUtils;

import java.util.Date;

/**
 * Created by rzubala on 01.03.18.
 */

public class InvoiceCursorAdapter extends RecyclerView.Adapter<InvoiceCursorAdapter.InvoiceViewHolder> {

    private Cursor mCursor;
    private Context mContext;
    private ViewSwitcher mSwitcher;
    final private InvoiceOnClickHandler mClickHandler;
    private static final int VIEW_TYPE_TODAY = 0;
    private static final int VIEW_TYPE_NORMAL_DAY = 1;
    private static final String TAG = InvoiceCursorAdapter.class.getSimpleName();

    public InvoiceCursorAdapter(Context context, InvoiceOnClickHandler handler, ViewSwitcher mViewSwitcher) {
        this.mContext = context;
        this.mClickHandler = handler;
        this.mSwitcher = mViewSwitcher;
    }

    @Override
    public InvoiceCursorAdapter.InvoiceViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        int layoutId;
        switch (viewType) {
            case VIEW_TYPE_TODAY: {
                layoutId = R.layout.invoice_layout_today;
                break;
            }
            case VIEW_TYPE_NORMAL_DAY: {
                layoutId = R.layout.invoice_layout;
                break;
            }
            default:
                throw new IllegalArgumentException("Invalid view type, value of " + viewType);
        }
        View view = LayoutInflater.from(mContext).inflate(layoutId, parent, false);
        view.setFocusable(true);
        return new InvoiceViewHolder(view);
    }

    @Override
    public int getItemViewType(int position) {
        int dateIndex = mCursor.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_DATE);
        int paidIndex = mCursor.getColumnIndex(InvoiceContract.InvoiceEntry.COLUMN_PAID);
        mCursor.moveToPosition(position);
        boolean paid = mCursor.getInt(paidIndex) > 0;
        Long timestamp = mCursor.getLong(dateIndex);
        Long todayTimestamp = DateUtils.getSqlSelectionForToday();
        if (!paid && timestamp <= todayTimestamp) {
            return VIEW_TYPE_TODAY;
        }
        return VIEW_TYPE_NORMAL_DAY;
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
        holder.invoiceAmountView.setText(NumberUtils.formatNumberCurrency(amount));
        holder.invoiceCurrencyView.setText(" "+currency);

        int type = getItemViewType(position);
        if (paid) {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorPaid));
            if (type == VIEW_TYPE_NORMAL_DAY) {
                holder.imageViewCheck.setImageResource(R.drawable.check);
                holder.imageViewCheck.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
            }
        } else {
            holder.itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorUnpaid));
            if (type == VIEW_TYPE_NORMAL_DAY) {
                holder.imageViewCheck.setImageResource(R.drawable.close);
                holder.imageViewCheck.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
            }
        }
        if (type == VIEW_TYPE_TODAY) {
            holder.imageViewCheck.setColorFilter(mContext.getResources().getColor(R.color.colorRed));
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    private void showItems(boolean show) {
        if (show && R.id.recyclerViewInvoices == mSwitcher.getNextView().getId()) {
            mSwitcher.showNext();
        } else if (!show && R.id.text_empty == mSwitcher.getNextView().getId()) {
            mSwitcher.showNext();
        }
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            showItems(false);
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;
        if (c != null) {
            this.notifyDataSetChanged();
        }
        showItems(getItemCount() > 0);
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

        ImageView imageViewCheck;

        public InvoiceViewHolder(View itemView) {
            super(itemView);

            invoiceDescriptionView = (TextView) itemView.findViewById(R.id.invoiceDescription);
            invoiceAmountView = (TextView) itemView.findViewById(R.id.invoiceAmount);
            invoiceCurrencyView = (TextView) itemView.findViewById(R.id.invoiceCurrency);
            invoiceDateView = (TextView) itemView.findViewById(R.id.invoiceDate);
            imageViewCheck = (ImageView) itemView.findViewById(R.id.imageViewCheck);

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
