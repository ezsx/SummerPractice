package com.kubsu.eszx.fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.kubsu.eszx.ErrorMessages;
import com.kubsu.eszx.MainActivity;
import com.kubsu.eszx.R;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ConfirmDeleteDialogFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConfirmDeleteDialogFragment extends DialogFragment {


    /* Public Members */
    // supply keys for the Bundle
    public static final String TITLE_ID = "title";
    public static final String MESSAGE_ID = "message";

    /* Private Members */
    private Uri mContactUri; // Uri of selected contact
    private DetailFragment.OnDetailFragmentInteractionListener mListener;



    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder =
                new AlertDialog.Builder(Objects.requireNonNull(getActivity(),
                        ErrorMessages.ACTIVITY_NOT_NULL.toString()));

        // Get supplied args
        Bundle args = getArguments();
        if(args != null) {

            builder.setTitle(args.getString(TITLE_ID, ErrorMessages.SORRY.toString()));
            builder.setMessage(args.getString(MESSAGE_ID, ErrorMessages.AN_ERROR_OCCURRED.toString()));
            mContactUri = args.getParcelable(MainActivity.CONTACT_URI);
        } else {
            // supply default text if no argument were set
            builder.setTitle(ErrorMessages.SORRY.toString());
            builder.setMessage(ErrorMessages.AN_ERROR_OCCURRED.toString());
            return builder.create();
        }

        builder.setPositiveButton(R.string.button_delete, (dialog, which) -> {

            // Use Activity's ContentResolver to invoke delete on the AddressBoo
            getActivity().getContentResolver().delete(mContactUri, null, null);
            // notify listener
            Objects.requireNonNull(mListener, ErrorMessages.LISTENER_NOT_NULL.toString()).onContactDeleted();
        });

        builder.setNegativeButton(R.string.button_cancel, null);

        return builder.create();
    }

    public void setListener(
            DetailFragment.OnDetailFragmentInteractionListener mListener) {

        this.mListener = mListener;
    }

}