package com.kubsu.eszx.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.kubsu.eszx.ErrorMessages;
import com.kubsu.eszx.MainActivity;
import com.kubsu.eszx.R;
import com.kubsu.eszx.data.AddressBookDatabaseDescription;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

public class AddEditFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor>  {

    /* Private Members */
    private OnAddEditFragmentInteractionListener mListener;
    private static final int CONTACT_LOADER = 0; 	// constant used to identify the Loader
    private Uri mContactUri; // Uri of selected contact
    private boolean mAddingNewContact = true;
    private boolean mFind = false;

    // EditTexts for contact information
    private TextInputLayout mNameFTextInputLayout;
    private TextInputLayout mPhoneTextInputLayout;
    private TextInputLayout mEmailTextInputLayout;
    private TextInputLayout mNameITextInputLayout;
    private TextInputLayout mNameOTextInputLayout;
    private TextInputLayout mLoginTextInputLayout;
    private TextInputLayout mPwdTextInputLayout;

    private FloatingActionButton mSaveContactFAB;
    private CoordinatorLayout mCoordinatorLayout;

    private final View.OnClickListener mSaveContactButtonClicked = v -> {

        View view = Objects.requireNonNull(getView()).findFocus();
        if( view != null) {
            InputMethodManager imm =
                    (InputMethodManager) Objects.requireNonNull(getActivity(),
                            ErrorMessages.ACTIVITY_NOT_NULL.toString()).getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
            saveContact(); // save contact to the database
        }
    };

    // detects when the text in the nameTextInputLayout's EditText changes
    // to hide or show saveButtonFAB
    private final TextWatcher mNameChangedListener = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateSaveButtonFAB(s.toString());
        }

        @Override
        public void afterTextChanged(Editable s) { }
    };

    public AddEditFragment() {
        // Required empty public constructor
    }


    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if(context instanceof OnAddEditFragmentInteractionListener) {
            mListener = (OnAddEditFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnAddEditFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        super.onCreateView(inflater, container, savedInstanceState);
        setHasOptionsMenu(true); // Fragment has menu items to display

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_add_edit, container, false);

        mNameFTextInputLayout = view.findViewById(R.id.nameFTextInputLayout);
        Objects.requireNonNull(mNameFTextInputLayout.getEditText(),
                ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString()).addTextChangedListener(mNameChangedListener);

        mPhoneTextInputLayout = view.findViewById(R.id.phoneTextInputLayout);
        mEmailTextInputLayout = view.findViewById(R.id.emailTextInputLayout);
        mNameITextInputLayout = view.findViewById(R.id.nameITextInputLayout);
        mNameOTextInputLayout = view.findViewById(R.id.nameOTextInputLayout);
        mLoginTextInputLayout = view.findViewById(R.id.loginTextInputLayout);
        mPwdTextInputLayout = view.findViewById(R.id.pwdTextInputLayout);

        mSaveContactFAB = view.findViewById(R.id.saveFAB);
        mSaveContactFAB.setOnClickListener(mSaveContactButtonClicked); // set FAB event's listener
        updateSaveButtonFAB(null);

        mCoordinatorLayout = Objects.requireNonNull(getActivity(),
                ErrorMessages.ACTIVITY_NOT_NULL.toString()).findViewById(R.id.coordinatorLayout);

        Bundle args = getArguments(); // null if creating new contact
        if(args != null) {
            mAddingNewContact = false;
            mContactUri = args.getParcelable(MainActivity.CONTACT_URI);
            mFind = (args.getInt(MainActivity.FIND_ACTION) == 1);
        }

        return  view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        if(mContactUri != null) {
            getLoaderManager().initLoader(CONTACT_LOADER, null, this);
        }
    }

    @Override
    public void onDetach() {

        super.onDetach();
        mListener = null;
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

    /**
     * called by LoaderManager when loading completes
     * @param loader the loader
     * @param cursor the cursor
     */
    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor cursor) {

        // if contact exists in the database, display its data
        if(cursor != null && cursor.moveToFirst()) {

            // Retrieve the EditTexts
            EditText nameFEditText = Objects.requireNonNull(mNameFTextInputLayout.getEditText(),
                    ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString());

            EditText phoneEditText = Objects.requireNonNull(mPhoneTextInputLayout.getEditText(),
                    ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString());

            EditText emailEditText = Objects.requireNonNull(mEmailTextInputLayout.getEditText(),
                    ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString());

            EditText nameIEditText = Objects.requireNonNull(mNameITextInputLayout.getEditText(),
                    ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString());

            EditText nameOEditText = Objects.requireNonNull(mNameOTextInputLayout.getEditText(),
                    ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString());

            EditText loginEditText = Objects.requireNonNull(mLoginTextInputLayout.getEditText(),
                    ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString());

            EditText pwdEditText = Objects.requireNonNull(mPwdTextInputLayout.getEditText(),
                    ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString());

            // Retrieve the data from the cursor
            String nameF = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_NAME_F));
            String nameI = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_NAME_I));
            String email = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_EMAIL));
            String nameO = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_NAME_O));
            String phone = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_PHONE));
            String login = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_LOGIN));
            String pwd = cursor.getString(cursor.getColumnIndex(AddressBookDatabaseDescription.Contact.COLUMN_PWD));

            // set and display the cursor data into the EditText
            nameFEditText.setText(nameF);
            phoneEditText.setText(phone);
            emailEditText.setText(email);
            nameIEditText.setText(nameI);
            nameOEditText.setText(nameO);
            loginEditText.setText(login);
            pwdEditText.setText(pwd);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {	}

    // saves contact information to the database
    private void saveContact() {

        // Create ContentValues object containing contact's key-value pairs
        ContentValues contentValues = new ContentValues();

        String name = Objects.requireNonNull(mNameFTextInputLayout.getEditText(),
                ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString()).getText().toString();

        String phone = Objects.requireNonNull(mPhoneTextInputLayout.getEditText(),
                ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString()).getText().toString();

        String email = Objects.requireNonNull(mEmailTextInputLayout.getEditText(),
                ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString()).getText().toString();

        String nameI = Objects.requireNonNull(mNameITextInputLayout.getEditText(),
                ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString()).getText().toString();

        String nameO = Objects.requireNonNull(mNameOTextInputLayout.getEditText(),
                ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString()).getText().toString();

        String login = Objects.requireNonNull(mLoginTextInputLayout.getEditText(),
                ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString()).getText().toString();

        String pwd = Objects.requireNonNull(mPwdTextInputLayout.getEditText(),
                ErrorMessages.TEXT_INPUT_LAYOUT_NO_EDITTEXT.toString()).getText().toString();

        contentValues.put(AddressBookDatabaseDescription.Contact.COLUMN_NAME_F, name);
        contentValues.put(AddressBookDatabaseDescription.Contact.COLUMN_NAME_I, nameI);
        contentValues.put(AddressBookDatabaseDescription.Contact.COLUMN_EMAIL, email);
        contentValues.put(AddressBookDatabaseDescription.Contact.COLUMN_NAME_O, nameO);
        contentValues.put(AddressBookDatabaseDescription.Contact.COLUMN_PHONE, phone);
        contentValues.put(AddressBookDatabaseDescription.Contact.COLUMN_LOGIN, login);
        contentValues.put(AddressBookDatabaseDescription.Contact.COLUMN_PWD, pwd);
        if (mFind) doSearch(contentValues); else doSaveContact(contentValues);
    }

    private void doSaveContact(ContentValues contentValues) {

        if(mAddingNewContact) {
            // use Activity's ContentResolver to invoke
            // insert on the AddressBookContentProvider
            Uri newContactUri =
                    Objects.requireNonNull(getActivity(), ErrorMessages.ACTIVITY_NOT_NULL.toString())
                            .getContentResolver().insert(AddressBookDatabaseDescription.Contact.CONTENT_URI, contentValues);

            if(newContactUri != null) {
                Snackbar.make(mCoordinatorLayout, R.string.contact_added, Snackbar.LENGTH_LONG).show();
                mListener.onAddEditCompleted(newContactUri);
            } else {
                Snackbar.make(mCoordinatorLayout, R.string.contact_not_added, Snackbar.LENGTH_LONG).show();
            }

        } else { // updating contact

            // use Activity's ContentResolver to invoke
            // update on the AddressBookContentProvider
            int updatedRows =
                    Objects.requireNonNull(getActivity(), ErrorMessages.ACTIVITY_NOT_NULL.toString())
                            .getContentResolver().update(mContactUri, contentValues, null, null);

            if(updatedRows > 0) {
                mListener.onAddEditCompleted(mContactUri);
                Snackbar.make(mCoordinatorLayout, R.string.contact_updated, Snackbar.LENGTH_LONG).show();
            } else {
                Snackbar.make(mCoordinatorLayout, R.string.contact_not_updated, Snackbar.LENGTH_LONG).show();
            }

        }

    }

    private void doSearch(ContentValues contentValues) {

//     TODO Поиск
//        com.kubsu.eszx.data.AddressBookDatabaseDescription.Contact.COLUMN_NAME+" like '%s%'"
        StringBuilder sb = new StringBuilder("0=0");
         Iterator<Map.Entry<String, Object>> it =  contentValues.valueSet().iterator();
            while(it.hasNext()) {
                Map.Entry obj =(Map.Entry)it.next();
                sb.append(" and "+ obj.getKey()+" like '%"+obj.getValue()+"%' ");
            }
        String s = sb.toString();
        mListener.onSearchCompleted(s);
    }

    // Show saveButtonFAB only if name is not empty
    private void updateSaveButtonFAB(String input) {

        // if there is a name for the contact, show the FAB
        if(!TextUtils.isEmpty(input)) {
            mSaveContactFAB.show();
        } else {
            mSaveContactFAB.hide();
        }
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     *
     */
    public interface OnAddEditFragmentInteractionListener {

        // called when contact is saved
        void onAddEditCompleted(Uri contactUri);
        void onSearchCompleted(String filter);
    }


}
