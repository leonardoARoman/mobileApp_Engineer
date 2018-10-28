package com.example.roman.contanctlist;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import model.Contact;

public class ContactInfoActivity extends AppCompatActivity {
    private TextView contactNameInfo, contactNumberInfo;
    private ListView contactRelationshipList;
    private Contact contact;
    private CustomAdapterContactInfoActivity adapter;
    private static final String TAG = "ContactInfoActivity";
    private final Context context = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_info);
        Log.d(TAG, "onCreate() called");
        contactNameInfo = (TextView) findViewById(R.id.contactNameInfo);
        contactNumberInfo = (TextView) findViewById(R.id.contactNumberInfo);
        contactRelationshipList = (ListView) findViewById(R.id.contactRelationshipList);
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart() called");
        contact = (Contact) getIntent().getSerializableExtra("contact");
        contactNameInfo.setText(contact.getName());
        contactNumberInfo.setText(contact.getPhoneNumber());
        adapter = new CustomAdapterContactInfoActivity(context,contact.getFriends());
        contactRelationshipList.setAdapter(adapter);
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
}
