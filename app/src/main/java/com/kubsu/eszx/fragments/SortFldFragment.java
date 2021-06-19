package com.kubsu.eszx.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;

import androidx.fragment.app.Fragment;

import com.kubsu.eszx.R;
import com.kubsu.eszx.data.AddressBookDatabaseDescription;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SortFldFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SortFldFragment extends Fragment implements View.OnClickListener{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String FLD_SORT = "fld_sort";
    private String fldSort;
    private OnSortFldFragmentInteractionListener mListener;


    public SortFldFragment() {
        // Required empty public constructor
    }

    @Override
    public void onClick(View v){
        RadioButton rb = (RadioButton)v;
        String lbl = rb.getText().toString();
        String s = "";
        if (lbl.equals(rb.getContext().getString(R.string.label_name_f))) {
             s = AddressBookDatabaseDescription.Contact.COLUMN_NAME_F;
        }
        if (lbl.equals(rb.getContext().getString(R.string.label_name_i))) {
             s = AddressBookDatabaseDescription.Contact.COLUMN_NAME_I;
        }
        if (lbl.equals(rb.getContext().getString(R.string.label_name_o))) {
             s = AddressBookDatabaseDescription.Contact.COLUMN_NAME_O;
        }
        if (lbl.equals(rb.getContext().getString(R.string.label_email))) {
             s = AddressBookDatabaseDescription.Contact.COLUMN_EMAIL;
        }
        if (lbl.equals(rb.getContext().getString(R.string.label_login))) {
             s = AddressBookDatabaseDescription.Contact.COLUMN_LOGIN;
        }
        if (lbl.equals(rb.getContext().getString(R.string.label_phone))) {
             s = AddressBookDatabaseDescription.Contact.COLUMN_PHONE;
        }
        if (lbl.equals(rb.getContext().getString(R.string.label_pwd))) {
            s = AddressBookDatabaseDescription.Contact.COLUMN_PWD;
        }
        mListener.onSortCompleted(s,lbl);
    }

//    private final View.OnClickListener mSortButtonClicked = v -> {
//
//        View view = Objects.requireNonNull(getView()).findFocus();
//        if( view != null) {
//            InputMethodManager imm =
//                    (InputMethodManager) Objects.requireNonNull(getActivity(),
//                            ErrorMessages.ACTIVITY_NOT_NULL.toString()).getSystemService(Context.INPUT_METHOD_SERVICE);
//            Objects.requireNonNull(imm).hideSoftInputFromWindow(view.getWindowToken(), 0);
//            saveContact(); // save contact to the database
//        }
//    };

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SortFldFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SortFldFragment newInstance(String pFldSort) {
        SortFldFragment fragment = new SortFldFragment();
        Bundle args = new Bundle();
        args.putString(FLD_SORT, pFldSort);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            fldSort = getArguments().getString(FLD_SORT);
        }
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);
        if(context instanceof SortFldFragment.OnSortFldFragmentInteractionListener) {
            mListener = (SortFldFragment.OnSortFldFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement SortFldFragment.OnSortFldFragmentInteractionListener");
        }
    }

    @Override
    public View onCreateView(@org.jetbrains.annotations.NotNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View v = inflater.inflate(R.layout.fragment_sort_fld, container, false);
        (v.findViewById(R.id.radioButton)).setOnClickListener(this);
        (v.findViewById(R.id.radioButton6)).setOnClickListener(this);
        (v.findViewById(R.id.radioButton2)).setOnClickListener(this);
        (v.findViewById(R.id.radioButton3)).setOnClickListener(this);
        (v.findViewById(R.id.radioButton4)).setOnClickListener(this);
        (v.findViewById(R.id.radioButton5)).setOnClickListener(this);
        (v.findViewById(R.id.radioButton7)).setOnClickListener(this);
        return v;
    }

    public interface OnSortFldFragmentInteractionListener {

        // called when contact is saved
        void onSortCompleted(String fldSort,String fldLabel);
    }

}