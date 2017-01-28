package com.haryadi.trigger.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.haryadi.trigger.R;
import com.haryadi.trigger.adapter.ProfileAdapter;
import com.haryadi.trigger.data.TriggerContract;

/**
 * Created by aharyadi on 1/23/17.
 */

public class ProfileFragment extends Fragment implements android.app.LoaderManager.LoaderCallbacks<Cursor> {


    RecyclerView mRecyclerView;
    ProfileAdapter mAdapter;

    private static final int LOADER_ID = 0;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.fragment_profile, container, false);
        mRecyclerView = (RecyclerView)rootView.findViewById(R.id.recycler_profile);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.VERTICAL,false));
        mAdapter = new ProfileAdapter(getActivity(), new ProfileAdapter.OnItemClickListener() {
            @Override
            public void onClick(Uri uri) {
                FragmentManager fm = getActivity().getSupportFragmentManager();
                EditCreateProfileFragment editNameDialogFragment = EditCreateProfileFragment.newInstance("Edit",true,uri);
                editNameDialogFragment.show(fm, "Edit");
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        getActivity().getLoaderManager().initLoader(LOADER_ID,null,this);
        return rootView;
    }


    @Override
    public android.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Log.v("Inside Loader","ddd");
        Uri uri = TriggerContract.TriggerEntry.CONTENT_URI;
        //  Cursor cursor = getActivity().getContentResolver().query(uri,null,null,null,null);
        return new android.content.CursorLoader(getActivity(),uri,null,null,null,null);
    }

    @Override
    public void onLoadFinished(android.content.Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.content.Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }


}
