package com.kubsu.eszx;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.snackbar.Snackbar;
import com.kubsu.eszx.fragments.AddEditFragment;
import com.kubsu.eszx.fragments.ContactsFragment;
import com.kubsu.eszx.fragments.DetailFragment;
import com.kubsu.eszx.fragments.SortFldFragment;

import java.util.List;

public class MainActivity extends AppCompatActivity
        implements ContactsFragment.OnContactFragmentInteractionListener,
        DetailFragment.OnDetailFragmentInteractionListener,
        AddEditFragment.OnAddEditFragmentInteractionListener,
        SortFldFragment.OnSortFldFragmentInteractionListener {
    // Key for storing a contact's Uri in a Bundle passed to a fragment
    public static final String CONTACT_URI = "contact_uri";
    public static final String CONTACT_FRAGMENT_TAG = "contact_fragment";
    public static final String FIND_ACTION = "find_action";

    // Private Members
    private ContactsFragment mContactsFragment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


//        FloatingActionButton fab = findViewById(R.id.fab);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                        .setAction("Action", null).show();
//            }
//        });

        // Handle fragments and configuration changes
        // If layout contains fragmentContainer, the phone layout is in use
        // create and display a ContactsFragment
        if(savedInstanceState == null && findViewById(R.id.fragmentContainer) != null) {

            // Create ContactsFragment
            mContactsFragment = new ContactsFragment();
            // Add the fragment to the FrameLayout
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.add(R.id.fragmentContainer, mContactsFragment, CONTACT_FRAGMENT_TAG);
            transaction.commit(); // display ContactsFragment

        } else { // get references to Fragments that have already been restored
            mContactsFragment = (ContactsFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.contactsFragment);

        }



    }

    private void resetFind() {
        mContactsFragment.setFilter("");
        mContactsFragment.updateContactList(); // refresh contacts
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//         Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//        return true;
//    }

//    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_find) {
            if (item.isChecked()) {
                item.setTitle("Найти");
                item.setChecked(false);
            } else {
                displayFindFragment(R.id.fragmentContainer);
                item.setTitle("Сбросить");
                item.setChecked(true);
            }
            return false;
        }

        return super.onOptionsItemSelected(item);
    }

    // Display DetailFragment for selected contact
    @Override
    public void onContactSelected(Uri contactUri) {

        if(findViewById(R.id.fragmentContainer) != null) { // phone
            displayContact(contactUri, R.id.fragmentContainer);
        }
        else { // tablet
            // removes top of back stack
            getSupportFragmentManager().popBackStack();
            displayContact(contactUri, R.id.rightPaneContainer);
        }
    }

   //    SortFldFragment.OnSortFldFragmentInteractionListener
    @Override
    public void onSortCompleted(String fldSort, String fldLabel) {
        getSupportFragmentManager().popBackStack();
//        .setFilter_str(filter);
        mContactsFragment.setOrder(fldSort,fldLabel);
        mContactsFragment.updateContactList(); // refresh contacts
    }

    // display AddEditFragment to add contact
    @Override
    public void onAddContact() {

        if(findViewById(R.id.fragmentContainer) != null) // phone
            displayAddEditFragment(R.id.fragmentContainer, null);
        else // tablet
//             !!!  displayAddEditFragment(R.id.rightPaneContainer, null);
            displayAddEditFragment(R.id.fragmentContainer, null);
    }

    @Override
    public void onFindBtn(boolean isOn) {
        if (isOn){
            displayFindFragment(R.id.fragmentContainer);

            Snackbar.make(findViewById(R.id.fragmentContainer) , "Заполните поля поиска ", Snackbar.LENGTH_LONG)
                    .setAction("Поиск->", null).show();

        }
                else resetFind();
    }


    @Override
    public void onSortBtn() {
        displaySortFragment(R.id.fragmentContainer);

        Snackbar.make(findViewById(R.id.fragmentContainer) , "Выберите поле для сортировки", Snackbar.LENGTH_LONG)
                .setAction("Сортировка->", null).show();


    }

    @Override
    public void onMailSend(CharSequence email) {
        Intent emailIntent = new Intent(Intent.ACTION_SEND);
// The intent does not have a URI, so declare the "text/plain" MIME type
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, new String[] {email.toString()}); // recipients
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Email message text");
        //emailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("content://path/to/email/attachment"));
// You can also attach multiple items by passing an ArrayList of Uris
// Verify it resolves
        PackageManager packageManager = getPackageManager();
        List<ResolveInfo> activities = packageManager.queryIntentActivities(emailIntent, 0);
        boolean isIntentSafe = activities.size() > 0;

// Start an activity if it's safe
        if (isIntentSafe) {
            startActivity(emailIntent);
        }
    }


    // return to contact list when displayed contact deleted
    @Override
    public void onContactDeleted() {
        // remove top of back stack
        getSupportFragmentManager().popBackStack();
        mContactsFragment.updateContactList(); // refresh contact
    }

    // display the AddEditFragment to edit an existing contact
    @Override
    public void onEditContact(Uri contactUri) {

        if(findViewById(R.id.fragmentContainer) != null) {// phone
            displayAddEditFragment(R.id.fragmentContainer, contactUri);
        }
        else {// tablet
            displayAddEditFragment(R.id.fragmentContainer, contactUri);
            //displayAddEditFragment(R.id.rightPaneContainer, contactUri);
        }
    }

    // update GUI after new contact or updated contact saved
    @Override
    public void onAddEditCompleted(Uri contactUri) {

        // removes top of back stack
        getSupportFragmentManager().popBackStack();
        mContactsFragment.updateContactList(); // refresh contacts

        if(findViewById(R.id.fragmentContainer) == null) { // tablet

            // removes top of back stack
            getSupportFragmentManager().popBackStack();

            // on tablet, display contact that was just added or edited
            displayContact(contactUri, R.id.rightPaneContainer);

        }
    }

    @Override
    public void onSearchCompleted(String filter){
        // removes top of back stack
        getSupportFragmentManager().popBackStack();
//        .setFilter_str(filter);
        mContactsFragment.setFilter(filter);
        mContactsFragment.updateContactList(); // refresh contacts

    }



    private void displayContact(Uri contactUri, int viewID) {

        DetailFragment detailFragment = new DetailFragment();

        // specify contact's Uri as an argument to the DetailFragment
        Bundle args = new Bundle();
        args.putParcelable(CONTACT_URI, contactUri);
        detailFragment.setArguments(args);

        // Use a FragmentTransaction to display the DetailFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, detailFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // display DetailFragment
    }

    // Display fragment to adding a new or editing an existing contact
    private void displayAddEditFragment(int viewID, Uri contactUri) {

        AddEditFragment addEditFragment = new AddEditFragment();

        // if editing existing contact, provide contactUri as an argument
        if(contactUri != null) {
            Bundle args = new Bundle();
            args.putParcelable(CONTACT_URI, contactUri);
            addEditFragment.setArguments(args);
        }

        // Use a FragmentTransaction to display the AddEditFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes AddEditFragment to display
    }

    // Display fragment to adding a new or editing an existing contact
    private void displayFindFragment(int viewID) {

        AddEditFragment addEditFragment = new AddEditFragment();

        Bundle args = new Bundle();
        args.putParcelable(CONTACT_URI, null);
        args.putInt(FIND_ACTION, 1);
        addEditFragment.setArguments(args);


        // Use a FragmentTransaction to display the AddEditFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, addEditFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes AddEditFragment to display
    }

    private void displaySortFragment(int viewID) {

        SortFldFragment sortFldFragment = new SortFldFragment();

        Bundle args = new Bundle();
        args.putParcelable(CONTACT_URI, null);
        args.putInt(FIND_ACTION, 1);
        sortFldFragment.setArguments(args);


        // Use a FragmentTransaction to display the AddEditFragment
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(viewID, sortFldFragment);
        transaction.addToBackStack(null);
        transaction.commit(); // causes AddEditFragment to display
    }


}
