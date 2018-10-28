package com.example.roman.contanctlist;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.*;
import android.widget.TextView;

import java.util.List;

import model.*;

public class CustomAdapterActivity extends BaseAdapter {
    private Context context;
    private List<Contact> list;

    public CustomAdapterActivity(Context context, List<Contact> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        final CheckBoxViewHolder holder;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            holder = new CheckBoxViewHolder();
            convertView = inflater.inflate(R.layout.activity_custom_adapter_checkbox,parent, false);

            holder.setCheckBox((CheckBox) convertView.findViewById(R.id.checkBox));
            holder.setTextView((TextView) convertView.findViewById(R.id.contact));

            convertView.setTag(holder);
        }else {
            holder = (CheckBoxViewHolder)convertView.getTag();
        }
        holder.getTextView().setText(list.get(position).getName());
        holder.getTextView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Contact c = list.get(position);
                Intent intent = new Intent(context, ContactInfoActivity.class);
                intent.putExtra("contact",c);
                context.startActivity(intent);
            }
        });
        holder.getCheckBox().setOnCheckedChangeListener(null);
        holder.getCheckBox().setChecked(list.get(position).isChecked());
        holder.getCheckBox().setTag(position);
        holder.getCheckBox().setOnCheckedChangeListener(
                new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        Contact c = list.get(position);
                        list.get(position).setChecked(isChecked);
                        System.out.println("is "+c.getName()+" checked?"+c.isChecked());
                    }
                }
        );
        return convertView;
    }
}
