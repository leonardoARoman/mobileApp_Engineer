package com.example.roman.contanctlist.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.example.roman.contanctlist.CustomAdapterActivity;
import com.example.roman.contanctlist.R;

import java.util.ArrayList;
import java.util.LinkedList;

import model.Contact;
import model.ContactList;

public class ContactDetailsFragment extends Fragment {
    private EditText contactName, contactNumber;
    private ListView relationshipList;
    private Button addPerson;
    private CustomAdapterActivity adapter;
    private ArrayList<Contact> selectedContacts;
    private final ContactList contactList = ContactList.getInstance();
    private static final String TAG = "ContactDetailsFragment";

    public ContactDetailsFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView() called");
        View v = inflater.inflate(R.layout.fragment_contact_details, container, false);
        contactName = (EditText) v.findViewById(R.id.contactName);
        contactNumber = (EditText) v.findViewById(R.id.contactNumber);
        relationshipList = (ListView) v.findViewById(R.id.relationshipList);
        addPerson = (Button) v.findViewById(R.id.addPerson);
        selectedContacts = new ArrayList<>();
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
                }
            }
        });
        return v;
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
        adapter = new CustomAdapterActivity(super.getContext(),contacts);
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
                selectedContacts.add(contact);
            }
        }
        c.setFriends(selectedContacts);
        // ADD TO FRIEND DB
    }
}
