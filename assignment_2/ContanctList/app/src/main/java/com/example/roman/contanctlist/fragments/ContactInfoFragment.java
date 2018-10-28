package com.example.roman.contanctlist.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.roman.contanctlist.CustomAdapterActivity;
import com.example.roman.contanctlist.R;

import java.util.LinkedList;

import model.Contact;

public class ContactInfoFragment extends Fragment {

    private static final String TAG = "ContactInfoFragment";

    public ContactInfoFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v =  inflater.inflate(R.layout.fragment_contact_info, container, false);
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
