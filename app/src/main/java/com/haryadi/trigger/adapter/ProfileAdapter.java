package com.haryadi.trigger.adapter;

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

/**
 * Created by aharyadi on 1/25/17.
 */

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ProfileViewHolder> {

    Context mContext;
    Cursor mCursor;
    OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onClick(Uri uri);
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
            Uri uri = TriggerContract.TriggerEntry.buildTaskUri(mCursor.getLong(mCursor.getColumnIndex(TriggerContract.TriggerEntry._ID)));
            mListener.onClick(uri);
        }
    }
}
