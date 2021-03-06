package com.kubsu.eszx.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.kubsu.eszx.ItemDivider;
import com.kubsu.eszx.LogWrapper;
import com.kubsu.eszx.R;
import com.kubsu.eszx.adapters.ContactsAdapter;
import com.kubsu.eszx.data.AddressBookDatabaseDescription;
import android.widget.RemoteViews;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ContactsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ContactsFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor>,
        ContactsAdapter.ContactClickListener{

    private static final int CONTACT_LOADER = 0;

    // used to inform MainActivity when a contact is selected
    private OnContactFragmentInteractionListener mListener;
    // Adapter for RecyclerView
    private ContactsAdapter mAdapter;

    private  CursorLoader mCursor;
    private String filter_str;
    private String fld_sorted = AddressBookDatabaseDescription.Contact.COLUMN_NAME_F;
    private String lbl_sorted = "Фамилия.";


    // constructor
    public ContactsFragment() {

    }

    public void setOrder(String s,String l){
        fld_sorted = s;
        lbl_sorted = l;
        getLoaderManager().restartLoader(CONTACT_LOADER, null, this);
    }


    public void setFilter(String s){
        filter_str = s;
        getLoaderManager().restartLoader(CONTACT_LOADER, null, this);
    }


    /**
     * Set OnContactFragmentInteractionListener when fragment attached
     * @param context
     */
    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if(context instanceof OnContactFragmentInteractionListener) {
            mListener = (OnContactFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnContactFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // Fragment has menu items to display

        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        RecyclerView recycler = view.findViewById(R.id.contactRecycler);

        // Recycler should display items in a vertical list
        recycler.setLayoutManager(new LinearLayoutManager(getActivity()));
        // Create RecyclerView's adapter and item click listner
        mAdapter = new ContactsAdapter(this);
        recycler.setAdapter(mAdapter);

        // Attach a custom ItemDecorator to draw dividers between list items
        recycler.addItemDecoration(new ItemDivider(Objects.requireNonNull(getContext())));

        // improves performance if RecyclerView's layout size never changes
        recycler.setHasFixedSize(true);

        FloatingActionButton addButtonFAB = view.findViewById(R.id.addButton);
        addButtonFAB.setOnClickListener(v -> {
            LogWrapper.d("Adding new contact");
            mListener.onAddContact();
        });

       ToggleButton tgBtnFind = view.findViewById(R.id.tblBtnFind);
        tgBtnFind.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    mListener.onFindBtn(tgBtnFind.isChecked()); //displayFindFragment(R.id.fragmentContainer);
            }
        });

        Button btnSort = view.findViewById(R.id.btnSort);
        btnSort.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onSortBtn();
            }
        });
        btnSort.setText(lbl_sorted);
//        view.refreshDrawableState();

        return view;
    }

    // Initialize a loader when this fragment's activity is created
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CONTACT_LOADER, null, this);
    }

    /**
     * Remove OnContactFragmentInteractionListener when fragment detached
     */
    @Override
    public void onDetach() {

        super.onDetach();
        mListener = null;
    }

    // Called from MainActivity when other fragment's update the database
    public void updateContactList() {
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void onContactClick(Uri contactUri) {

        if(mListener == null) { return; }
        LogWrapper.d("User touches contact #" + contactUri.getLastPathSegment());
        mListener.onContactSelected(contactUri);
    }

    /**
     * Called by LoaderManager to create a loader
     * @param id the Id of the loader
     * @param args
     * @return {CursorLoader}
     */
    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        String[] s_arr = new String[3];
        s_arr[0] = AddressBookDatabaseDescription.Contact.COLUMN_ID;
        s_arr[1] = AddressBookDatabaseDescription.Contact.COLUMN_NAME_F+"||' '||"+fld_sorted+" as  name_f";
        s_arr[2] = fld_sorted;

        // Create an appropriate CursorLoader based on the id argument
        switch (id) {

            case CONTACT_LOADER:
                mCursor =
                new CursorLoader(Objects.requireNonNull(getActivity()),
                        AddressBookDatabaseDescription.Contact.CONTENT_URI,      // Uri of contacts table
                        s_arr,           // null projection returns all columns
                        filter_str,           // null selection returns all rows
                        null,       // no selection args
                        fld_sorted  + " COLLATE NOCASE ASC");
                return mCursor;

            default: return null;
        }
    }

    /**
     * Called by LoaderManager when loading completes
     * @param loader the loader
     * @param cursor the cursor
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {
        mAdapter.swapCursor(cursor);
    }

    /**
     * Called by LoaderManager when the loader is being reset
     * @param loader the cursor loader
     */
    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnContactFragmentInteractionListener {

        void onContactSelected(Uri contactUri); // called when a contact is selected
        void onAddContact(); // called with add button is pressed
        void onFindBtn(boolean isOn);
        void onSortBtn();
    }


}