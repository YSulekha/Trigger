package com.haryadi.trigger.adapter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haryadi.trigger.R;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.touch_helper.ItemTouchHelperAdapter;

/**
 * Created by aharyadi on 1/25/17.
 * AIzaSyAWot9ZEWXoCj8LtO7CTf7HJeGUIUpfDAA
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> implements ItemTouchHelperAdapter {

    Context mContext;
    Cursor mCursor;
    OnItemClickListener mListener;

    @Override
    public void onItemDismiss(int position) {

        mCursor.moveToPosition(position);
        ContentValues deleteValues = new ContentValues();
        long id = mCursor.getLong(mCursor.getColumnIndex(TriggerContract.TriggerEntry._ID));
        String where = TriggerContract.TriggerEntry.TABLE_NAME + "." +
                TriggerContract.TriggerEntry._ID + " = ?";
        String[] args = new String[]{Long.toString(id)};
        mContext.getContentResolver().delete(TriggerContract.TriggerEntry.CONTENT_URI, where, args);
        notifyItemRemoved(position);

    }

    @Override
    public void onItemStatusChanged(int position) {

    }

    public interface OnItemClickListener {
        void onClick(Uri uri,String trigger);
    }


    public ProfileAdapter(Context c, OnItemClickListener listener){
        mContext = c;
        mListener = listener;
    }
    @Override
    public ProfileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.profile_item, parent, false);
        return new ProfileViewHolder(item);
    }

    @Override
    public void onBindViewHolder(ProfileViewHolder holder, int position) {
        mCursor.moveToPosition(position);
          holder.profileName.setText(mCursor.getString(mCursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_TRIGGER_NAME)));
          holder.profileConnect.setText(mCursor.getString(mCursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_ISWIFION)));
    }

    @Override
    public int getItemCount() {
        if(mCursor!=null){
            Log.v("Count", String.valueOf(mCursor.getCount()));
            return mCursor.getCount();
        }
        return 0;
    }

    public void swapCursor(Cursor newCursor){
        Log.v("swap Cursor","add");
        mCursor = newCursor;
        notifyDataSetChanged();
    }

    class ProfileViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView profileName;
        TextView profileConnect;

        public ProfileViewHolder(View itemView) {
            super(itemView);
            profileName = (TextView)itemView.findViewById(R.id.item_triggerName);
            profileConnect = (TextView)itemView.findViewById(R.id.item_connect);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            mCursor.moveToPosition(pos);
            String triggerPoint = mCursor.getString(mCursor.getColumnIndex(TriggerContract.TriggerEntry.COLUMN_TRIGGER_POINT));
            Uri uri = TriggerContract.TriggerEntry.buildTaskUri(mCursor.getLong(mCursor.getColumnIndex(TriggerContract.TriggerEntry._ID)));
            Log.v("Uri",uri.toString());
            mListener.onClick(uri,triggerPoint);
        }
    }
}
