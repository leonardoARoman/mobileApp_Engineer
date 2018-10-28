package com.example.roman.contanctlist.fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.example.roman.contanctlist.CustomAdapterActivity;
import com.example.roman.contanctlist.R;
import java.util.ArrayList;
import model.Contact;
import model.ContactList;

public class ContactFragment extends Fragment {
    private ListView contactList;
    private ArrayList<Contact> contacts;
    private CustomAdapterActivity customAdapter;
    private final ContactList list = ContactList.getInstance();
    private static final String TAG = "ContactFragment";

    public ContactFragment() {}


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate() called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView() called");
        View v =  inflater.inflate(R.layout.fragment_contact, container, false);
        contactList = v.findViewById(R.id.contactList);
        contacts = (ArrayList<Contact>) list.getContactList();
        customAdapter = new CustomAdapterActivity(super.getContext(),contacts);
        contactList.setAdapter(customAdapter);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
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
