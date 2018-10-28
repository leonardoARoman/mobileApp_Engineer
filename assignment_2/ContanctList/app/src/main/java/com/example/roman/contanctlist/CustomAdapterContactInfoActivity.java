package com.example.roman.contanctlist;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import model.Contact;

public class CustomAdapterContactInfoActivity extends BaseAdapter {

    public CustomAdapterContactInfoActivity(Context mContext, List<Contact> mList) {
        this.mContext = mContext;
        this.mList = mList;
    }

    private Context mContext;
    private List<Contact> mList;

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View v = View.inflate(mContext,R.layout.activity_custom_adapter,null);
        TextView mName = v.findViewById(R.id.contactTextView);
        mName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,ContactInfoActivity.class);
                intent.putExtra("contact",mList.get(position));
                mContext.startActivity(intent);
            }
        });
        mName.setText(mList.get(position).getName());
        return v;
    }
}
