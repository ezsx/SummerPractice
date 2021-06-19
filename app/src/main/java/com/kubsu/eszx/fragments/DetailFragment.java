package com.kubsu.eszx.fragments;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.kubsu.eszx.ErrorMessages;
import com.kubsu.eszx.MainActivity;
import com.kubsu.eszx.R;
import com.kubsu.eszx.data.AddressBookDatabaseDescription;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DetailFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DetailFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    /* Public Members */
    public static final String CONFIRM_DELETE_TAG = "confirm_delete";

    /* Private Members */
    private static final int CONTACT_LOADER = 0;

    private OnDetailFragmentInteractionListener mListener;
    private Uri mContactUri;

    private TextView mNameFTextView; // displays contact's name
    private TextView mNameITextView; // displays contact's phone
    private TextView mNameOTextView; // displays contact's email
    private TextView mPhoneTextView; // displays contact's street
    private TextView mEmailTextView; // displays contact's city
    private TextView mLoginTextView; // displays contact's state
    private TextView mPwdTextView; // displays contact's zip

    public DetailFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if(context instanceof OnDetailFragmentInteractionListener) {
            mListener = (OnDetailFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnDetailFragmentInteractionListener");
        }
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // fragment has menu item to display

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_detail, container, false);

        // get the TextViews
        mNameFTextView = view.findViewById(R.id.nameFTextView);
        mNameITextView = view.findViewById(R.id.nameITextView);
        mNameOTextView = view.findViewById(R.id.nameOTextView);
        mPhoneTextView = view.findViewById(R.id.phoneTextView);
        mEmailTextView = view.findViewById(R.id.emailTextView);
        mLoginTextView = view.findViewById(R.id.loginTextView);
        mPwdTextView = view.findViewById(R.id.pwdTextView);

        // Get Bundle of args then extract the contact's uri
        Bundle args = getArguments();
        if(args != null) {
            mContactUri = args.getParcelable(MainActivity.CONTACT_URI);
        }
        Button btnMail = view.findViewById(R.id.btnMail);
        btnMail.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onMailSend(mEmailTextView.getText());
            }
        });


        return  view;
    }

    // Initialize a loader when this fragment's activity is created
    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(CONTACT_LOADER, null, this);
    }

    @Override
    public void onDetach() {

        super.onDetach();
        mListener = null;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {

        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_fragment_detail, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_edit :
                mListener.onEditContact(mContactUri);
                return true;

            case R.id.action_delete :
                deleteContact();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {

        // Construct the new query in the form of a Cursor Loader. Use the id
        // parameter to construct and return different loaders.
        String[] projection = null;     // null projection returns all columns
        String where = null;            // null selection returns all rows
        String[] whereArgs = null;      // no selection arguments
        String sortOrder = null;        // no sorting

        // Query URI
        Uri queryUri = mContactUri; // Uri of contact to display

        // Create the new Cursor Loader
        return new CursorLoader(
                Objects.requireNonNull(getActivity(), ErrorMessages.ACTIVITY_NOT_NULL.toString()),
                queryUri, projection, where, whereArgs, sortOrder);
    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        // If the contact exist in the database, display adapter
        if(cursor != null && cursor.moveToFirst()) {

            // Get the value for each data item
            String name_f = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_NAME_F));
            String name_i = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_NAME_I));
            String email = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_EMAIL));
            String name_o = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_NAME_O));
            String phone = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_PHONE));
            String login = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_LOGIN));
            String pwd = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_PWD));

            // fill TextViews with the retrieved data
            mNameFTextView.setText(name_f);
            mNameITextView.setText(name_i);
            mNameOTextView.setText(name_o);
            mPhoneTextView.setText(phone);
            mEmailTextView.setText(email);
            mLoginTextView.setText(login);
            mPwdTextView.setText(pwd);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {}

    /**
     * Uses FragmentManager to display the confirmDelete DialogFragment
     */
    private void deleteContact() {

        Bundle args = new Bundle();
        args.putParcelable(MainActivity.CONTACT_URI, mContactUri);
        args.putString(ConfirmDeleteDialogFragment.TITLE_ID, getString(R.string.confirm_title));
        args.putString(ConfirmDeleteDialogFragment.MESSAGE_ID, getString(R.string.confirm_message));

        ConfirmDeleteDialogFragment deleteDialogFragment = new ConfirmDeleteDialogFragment();
        deleteDialogFragment.setListener(mListener);
        deleteDialogFragment.setArguments(args);
        deleteDialogFragment.show(Objects.requireNonNull(getFragmentManager(),
                ErrorMessages.FRAGMENT_MANAGER_NOT_NULL.toString()), CONFIRM_DELETE_TAG);

    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface OnDetailFragmentInteractionListener {

        // called when a contact is deleted
        void onContactDeleted();

        // pass Uri of contact to edit to the OnDetailFragmentInteractionListener
        void onEditContact(Uri contactUri);
        void onMailSend(CharSequence email);
    }

}