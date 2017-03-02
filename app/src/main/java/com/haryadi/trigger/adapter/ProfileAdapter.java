package com.haryadi.trigger.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.haryadi.trigger.R;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.touch_helper.ItemTouchHelperAdapter;
import com.haryadi.trigger.utils.ChangeSettings;

/**
 * AIzaSyAWot9ZEWXoCj8LtO7CTf7HJeGUIUpfDAA
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> implements ItemTouchHelperAdapter {

    Context mContext;
    Cursor mCursor;
    OnItemClickListener mListener;
    TextView emptyView;
    CoordinatorLayout coordinatorLayout;
    ContentValues deleteValues;
    int deletePosition = -1;
    private View.OnClickListener undoListener;

    @Override
    public void onItemDismiss(int position) {

        mCursor.moveToPosition(position);
        deleteValues = new ContentValues();
        deleteValues.put(TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME, mCursor.getString(ChangeSettings.INDEX_TRIGGER_NAME));
        deleteValues.put(TriggerContract.TriggerEntry.COLUMN_NAME, mCursor.getString(ChangeSettings.INDEX_NAME));
        deleteValues.put(TriggerContract.TriggerEntry.COLUMN_CONNECT, mCursor.getString(ChangeSettings.INDEX_CONNECT));
        deleteValues.put(TriggerContract.TriggerEntry.COLUMN_ISBLUETOOTHON, mCursor.getString(ChangeSettings.INDEX_ISBLUETOOTHON));
        deleteValues.put(TriggerContract.TriggerEntry.COLUMN_ISWIFION, mCursor.getString(ChangeSettings.INDEX_ISWIFION));
        deleteValues.put(TriggerContract.TriggerEntry.COLUMN_MEDIAVOL, mCursor.getString(ChangeSettings.INDEX_MEDIAVOL));
        deleteValues.put(TriggerContract.TriggerEntry.COLUMN_RINGVOL, mCursor.getString(ChangeSettings.INDEX_RINGVOL));
        deleteValues.put(TriggerContract.TriggerEntry.COLUMN_TRIGGER_POINT, mCursor.getString(ChangeSettings.INDEX_TRIGGER_POINT));
        deletePosition = position;
        long id = mCursor.getLong(mCursor.getColumnIndex(TriggerContract.TriggerEntry._ID));
        String where = TriggerContract.TriggerEntry.TABLE_NAME + "." +
                TriggerContract.TriggerEntry._ID + " = ?";
        String[] args = new String[]{Long.toString(id)};
        mContext.getContentResolver().delete(TriggerContract.TriggerEntry.CONTENT_URI, where, args);
        notifyItemRemoved(position);

        Toast.makeText(mContext, mContext.getString(R.string.trigger_deleted), Toast.LENGTH_LONG).show();
        Snackbar.make(coordinatorLayout, mContext.getString(R.string.trigger_deleted), Snackbar.LENGTH_LONG)
                .setAction("UNDO", undoListener)
                .show();

    }

    @Override
    public void onItemStatusChanged(int position) {

    }

    public interface OnItemClickListener {
        void onClick(Uri uri, String trigger);
    }


    public ProfileAdapter(Context c, TextView textView, CoordinatorLayout layout, OnItemClickListener listener) {
        mContext = c;
        mListener = listener;
        coordinatorLayout = layout;
        emptyView = textView;
    }

    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        mCursor.moveToPosition(position);
        String name = mCursor.getString(ChangeSettings.INDEX_TRIGGER_NAME);
        String triggerPoint = mCursor.getString(ChangeSettings.INDEX_TRIGGER_POINT);
        holder.profileConnect.setText(mCursor.getString(ChangeSettings.INDEX_CONNECT));
        if (triggerPoint.equals(mContext.getString(R.string.wifi))) {
            holder.profileName.setText(name.substring(1, name.length() - 1));
            holder.profileImage.setImageResource(R.drawable.wifi_green);
        } else if (triggerPoint.equals(mContext.getString(R.string.bluetooth))) {
            holder.profileImage.setImageResource(R.drawable.bluetooth_green);
            holder.profileName.setText(name);
        } else {
            holder.profileImage.setImageResource(R.drawable.location_green);
            holder.profileName.setText(name);
        }
    }

    @Override
    public int getItemCount() {
        if (mCursor != null) {
            return mCursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor newCursor) {
        mCursor = newCursor;
        notifyDataSetChanged();
        emptyView.setVisibility(getItemCount() == 0 ? View.VISIBLE : View.INVISIBLE);
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView profileName;
        TextView profileConnect;
        ImageView profileImage;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            profileName = (TextView) itemView.findViewById(R.id.item_triggerName);
            profileConnect = (TextView) itemView.findViewById(R.id.item_connect);
            profileImage = (ImageView) itemView.findViewById(R.id.profile_image);
            itemView.setOnClickListener(this);
            undoListener = new View.OnClickListener() {

                @Override
                public void onClick(View view) {
                    if (deleteValues != null) {
                        mContext.getContentResolver().insert(TriggerContract.TriggerEntry.CONTENT_URI, deleteValues);
                        deleteValues = null;
                    }
                }
            };
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            mCursor.moveToPosition(pos);
            String triggerPoint = mCursor.getString(ChangeSettings.INDEX_TRIGGER_POINT);
            Uri uri = TriggerContract.TriggerEntry.buildTaskUri(mCursor.getLong(ChangeSettings.INDEX_TRIGGER_ID));
            mListener.onClick(uri, triggerPoint);
        }
    }
}
