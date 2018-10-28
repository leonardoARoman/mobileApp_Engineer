package com.example.roman.contanctlist;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.*;

import com.example.roman.contanctlist.database.DatabaseManager;
import com.google.gson.Gson;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;

import model.Contact;
import model.ContactList;

public class AddContactActivity extends AppCompatActivity {

    private EditText contactName, contactNumber;
    private ListView relationshipList;
    private Button addPerson;
    private CustomAdapterActivity adapter;
    private DatabaseManager db;
    private final Context context = this;
    private static final String TAG = "AddContactActivity";
    private static final String file = "Contacts";
    private static final String title = "Contact list";
    private final ContactList contactList = ContactList.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
        setContentView(R.layout.activity_add_contact);
        db = new DatabaseManager(context);
        contactName = (EditText) findViewById(R.id.contactName);
        contactNumber = (EditText) findViewById(R.id.contactNumber);
        relationshipList = (ListView) findViewById(R.id.relationshipList);
        addPerson = (Button) findViewById(R.id.addPerson);
        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = contactName.getText().toString();
                String phone = contactNumber.getText().toString();
                if(!name.isEmpty() && !phone.isEmpty()){
                    Contact contact = new Contact(name,phone);
                    contactList.addContact(contact);
                    setRelationshipList(contact);
                    contactName.getText().clear();
                    contactNumber.getText().clear();
                    adapter = new CustomAdapterActivity(context,contactList.getContactList());
                    relationshipList.setAdapter(adapter);
                    db.addContact(contact);
                }
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        LinkedList<Contact> contacts = new LinkedList<>();
        for (Contact contact: contactList.getContactList()){
            if (contact.isChecked()) {
                contacts.addFirst(contact);
            } else {
                contacts.addLast(contact);
            }

        }
        adapter = new CustomAdapterActivity(context,contacts);
        relationshipList.setAdapter(adapter);
    }

    @Override
    public void onResume() {
        super.onResume();
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

    private void setRelationshipList(Contact c){
        for(Contact contact: contactList.getContactList()){
            if (contact.isChecked()){
                contact.addFriend(c);
                c.addFriend(contact);
                contact.setChecked(false);
            }
        }
        db.addFriends(c);
    }
}
