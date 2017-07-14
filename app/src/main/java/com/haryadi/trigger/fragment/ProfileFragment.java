package com.haryadi.trigger.fragment;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.haryadi.trigger.R;
import com.haryadi.trigger.adapter.ProfileAdapter;
import com.haryadi.trigger.data.TriggerContract;
import com.haryadi.trigger.touch_helper.TouchhelperCallback;
import com.haryadi.trigger.utils.ChangeSettings;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {


    @BindView(R.id.recycler_profile)
    RecyclerView mRecyclerView;
    ProfileAdapter mAdapter;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.profile_empty_view)
    TextView emptyView;
    @BindView(R.id.profile_coord)
    CoordinatorLayout coord;

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
        ButterKnife.bind(this, rootView);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        mAdapter = new ProfileAdapter(getActivity(), emptyView, coord, new ProfileAdapter.OnItemClickListener() {
            @Override
            public void onClick(Uri uri, String trigger) {
                if (trigger.equals(getString(R.string.location))) {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    EditCreateLocationFragment editLocationDialogFragment = EditCreateLocationFragment.newInstance("Edit", true, uri,null, null);
                    editLocationDialogFragment.show(fm, "Edit");
                } else {
                    FragmentManager fm = getActivity().getSupportFragmentManager();
                    EditCreateProfileFragment editNameDialogFragment = EditCreateProfileFragment.newInstance("Edit", true, uri);
                    editNameDialogFragment.show(fm, "Edit");
                }
            }
        });
        mRecyclerView.setAdapter(mAdapter);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL);
        mRecyclerView.addItemDecoration(dividerItemDecoration);
        ItemTouchHelper.Callback callback = new TouchhelperCallback(mAdapter, getActivity());
        ItemTouchHelper helper = new ItemTouchHelper(callback);
        helper.attachToRecyclerView(mRecyclerView);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID, null, this);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = TriggerContract.TriggerEntry.CONTENT_URI;
        return new CursorLoader(getActivity(), uri, ChangeSettings.TRIGGER_COLUMNS, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}
