package com.example.roman.contanctlist;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.example.roman.contanctlist.database.DatabaseManager;
import java.util.ArrayList;
import java.util.List;
import model.Contact;
import model.ContactList;

public class MainActivity extends AppCompatActivity {
    private ListView contactList;
    private Button mAddContact, mDeleteContact;
    private ArrayList<Contact> contacts;
    private CustomAdapterActivity customAdapter;
    private ContactList list;
    private DatabaseManager db;
    private final Context context = this;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAddContact = (Button) findViewById(R.id.addContact);
        mDeleteContact = (Button) findViewById(R.id.deleteContact);
        contactList = (ListView) findViewById(R.id.contactList);
        db = new DatabaseManager(context);
        // IF APP CRASH COMMENT THIS LINE 35 PLEASE
        db.loadContacts();
        list = ContactList.getInstance();
        contacts = (ArrayList<Contact>) list.getContactList();
        customAdapter = new CustomAdapterActivity(context,contacts);
        contactList.setAdapter(customAdapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        mAddContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AddContactActivity.class);
                startActivity(intent);
            }
        });
        mDeleteContact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<Contact> unselected = new ArrayList<>();
                List<Contact> selected = new ArrayList<>();
                for (Contact c: contacts){
                    if (c.isChecked()){
                        selected.add(c);
                    }else {
                        unselected.add(c);
                    }
                }
                db.deleteContact(selected);
                for (Contact c: unselected){
                    for (Contact f: selected){
                        c.getFriends().remove(f);
                    }
                }
                customAdapter = new CustomAdapterActivity(context,unselected);
                contactList.setAdapter(customAdapter);
                list.setContactList(unselected);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        contactList.setAdapter(customAdapter);
        Log.d(TAG, "onResume() called");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "onStop() called");
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy() called");

    }
}
