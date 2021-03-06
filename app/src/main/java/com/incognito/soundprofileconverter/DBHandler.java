package com.incognito.soundprofileconverter;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class DBHandler extends SQLiteOpenHelper {

    private static final String DB_NAME = "soundprofileconverter";
    private static final int DB_VERSION = 1;

    // TABLE: whitelisted_contacts
    private static final String TABLE_NAME = "whitelisted_contacts";
    private static final String ID_COL = "id";
    private static final String NAME_COL = "name";
    private static final String PHONE_NUM_COL = "phone_num";

    public DBHandler(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ("
                + ID_COL + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + NAME_COL + " TEXT,"
                + PHONE_NUM_COL + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void removeContact(WhitelistedContacts contact) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ TABLE_NAME + " WHERE " +
                PHONE_NUM_COL + " = \""+contact.getContactPhoneNumber()+"\"");
        db.close();
    }

    public int addNewContact(WhitelistedContacts contact) {
        if (getContact(contact) != null) {
            return -1;
        }
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put(NAME_COL, contact.getContactName());
        values.put(PHONE_NUM_COL, contact.getContactPhoneNumber());

        db.insert(TABLE_NAME, null, values);

        db.close();
        Log.i("SQLite: Added ", contact.getContactName());

        return 1;
    }

    public WhitelistedContacts getContact(WhitelistedContacts contact) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " +
                PHONE_NUM_COL + " = \""+contact.getContactPhoneNumber()+"\"", null);

        WhitelistedContacts mcontact = null;

        if (cursor.moveToFirst()) {
            do {
                Log.i("DB", "Inside");
                mcontact = new WhitelistedContacts(
                        cursor.getString(1),
                        cursor.getString(2));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return mcontact;
    }

    public ArrayList<WhitelistedContacts> getContacts() {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME, null);

        ArrayList<WhitelistedContacts> whitelistedContacts = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                whitelistedContacts.add(new WhitelistedContacts(
                        cursor.getString(1),
                        cursor.getString(2)));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return whitelistedContacts;
    }
}
