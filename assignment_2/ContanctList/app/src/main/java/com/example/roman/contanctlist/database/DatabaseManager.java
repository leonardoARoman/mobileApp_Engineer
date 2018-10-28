package com.example.roman.contanctlist.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

import model.Contact;
import model.ContactList;

public class DatabaseManager extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "ContactDB";
    private static final String CONTACT_TABLE = "contact";
    private static final String FRIEND_TABLE = "friend";
    private static final String CONTACT_NAME   = "name";
    private static final String PHONE1 = "phone";
    private static final String FRIEND_NAME = "name";
    private static final String PHONE2  = "Cphone";

    public DatabaseManager(Context context) {
        super(context,DATABASE_NAME,null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACT_TABLE = "CREATE TABLE "+CONTACT_TABLE+" ("+
                PHONE1+" TEXT PRIMARY KEY,"+
                CONTACT_NAME+" TEXT)";

        String CREATE_FRIEND_TABLE = "CREATE TABLE "+FRIEND_TABLE+" ("+
                PHONE2 + " TEXT," +
                PHONE1 + " TEXT," +
                FRIEND_NAME + " TEXT," +
                "PRIMARY KEY ("+PHONE2+","+PHONE1+"), "+
                "FOREIGN KEY ("+PHONE2+") REFERENCES "+CONTACT_TABLE+"("+PHONE1+") "+
                "ON DELETE CASCADE)";
        // create contacts table
        db.execSQL(CREATE_CONTACT_TABLE);
        db.execSQL(CREATE_FRIEND_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    public void addContact(Contact contact){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. create ContentValues to add key "column"/value
        ContentValues values = new ContentValues();
        // 3. ADD THE VALUES
        values.put(PHONE1, contact.getPhoneNumber());
        values.put(CONTACT_NAME, contact.getName());
        // 4. INSERT VALUES TO TABLE
        db.insert(CONTACT_TABLE,null,values);
        // 5. CLOSE DB
        db.close();
    }

    public void addFriends(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        for(Contact c: contact.getFriends()){
            values.put(PHONE1, contact.getPhoneNumber());
            values.put(PHONE2, c.getPhoneNumber());
            values.put(FRIEND_NAME, c.getName());
            db.insert(FRIEND_TABLE,null, values);
        }
        db.close();
    }

    public void deleteContact(List<Contact> contacts){
        // 1. get reference to writable DB
        SQLiteDatabase db = this.getWritableDatabase();
        // 2. delete item from DB
        for (int i = 0; i < contacts.size(); i++){
            db.delete(CONTACT_TABLE,CONTACT_NAME+" = ?", new String[] {contacts.get(i).getName()});
            db.delete(FRIEND_TABLE,FRIEND_NAME+" = ?", new String[] {contacts.get(i).getName()});
        }
        db.close();
    }

    public void loadContacts(){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM "+CONTACT_TABLE;
        Cursor cursor = db.rawQuery(query,null);
        List<Contact> contacts = new ArrayList<>();
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                String name = cursor.getString(cursor.getColumnIndex(CONTACT_NAME));
                String number = cursor.getString(cursor.getColumnIndex(PHONE1));
                Contact contact = new Contact(name,number);
                contacts.add(contact);
                loadContactFriends(contact);
                cursor.moveToNext();
            }
        }
        ContactList.getInstance().setContactList(contacts);
    }

    private void loadContactFriends(Contact contact){
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT name, phone FROM "+FRIEND_TABLE+" WHERE "+PHONE1+" = "+contact.getPhoneNumber();
        Cursor cursor = db.rawQuery(query,null);
        if(cursor.moveToFirst()){
            while (!cursor.isAfterLast()){
                String name = cursor.getString(cursor.getColumnIndex(FRIEND_NAME));
                String number = cursor.getString(cursor.getColumnIndex(PHONE1));
                contact.addFriend(new Contact(name,number));
                cursor.moveToNext();
            }
        }
    }
}
