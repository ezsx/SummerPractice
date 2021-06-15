package com.kubsu.eszx.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.kubsu.eszx.LogWrapper;
import com.kubsu.eszx.data.AddressBookDatabaseDescription.Contact;

public class AddressBookDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "AddressBook.db";
    private static final int DATABASE_VERSION = 2;

    // SQL for creating table
    private static final String CREATE_CONTACTS_TABLE =
            "CREATE TABLE " + Contact.TABLE_NAME + "(" +
                    Contact.COLUMN_ID + " integer primary key, " +
                    Contact.COLUMN_NAME_F + " TEXT, " +
                    Contact.COLUMN_NAME_I + " TEXT, " +
                    Contact.COLUMN_EMAIL + " TEXT, " +
                    Contact.COLUMN_NAME_O + " TEXT, " +
                    Contact.COLUMN_PHONE + " TEXT, " +
                    Contact.COLUMN_LOGIN + " TEXT, " +
                    Contact.COLUMN_PWD + " TEXT);";



    public AddressBookDatabaseHelper(@Nullable Context context) {

        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /* Creates the contacts table when the database is created */
    @Override
    public void onCreate(SQLiteDatabase db) {

        LogWrapper.d("creating the contacts table...");
        LogWrapper.d("Create table string = " + CREATE_CONTACTS_TABLE);
        db.execSQL(CREATE_CONTACTS_TABLE); // create the contact table
        LogWrapper.d("contact table created");
        LogWrapper.d("exit");
    }

    /* Defines how to upgrade the database when the schema changes */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.beginTransaction();
        try {
            // создаем таблицу должностей
            db.execSQL("drop table " + Contact.TABLE_NAME + ";");
            db.execSQL(CREATE_CONTACTS_TABLE); // create the contact table
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }


    }

}
