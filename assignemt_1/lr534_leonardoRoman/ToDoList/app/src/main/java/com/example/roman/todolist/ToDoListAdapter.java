package com.example.roman.todolist;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import model.ToDoList;

/*
 Custom adapter class for custom item listView
*/
public class ToDoListAdapter extends BaseAdapter{

    private Context mContext;
    private List<ToDoList> list;
    // Constructor

    public ToDoListAdapter(Context mContext, List<ToDoList> list) {
        this.mContext = mContext;
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
    public View getView(int position, View convertView, ViewGroup parent) {
        // In Android, objects in the view layer are typically inflated
        // from XML within a layout file. This gets all the widgets from
        // the xml file
        View v = View.inflate(mContext,R.layout.todolist_item,null);
        TextView mTask = v.findViewById(R.id.item_tittle);
        TextView mDescription = v.findViewById(R.id.item_description);
        // To get task and description from the current object and assign it to
        // the EditText widgets from the todolist_item.xml file
        mTask.setText(list.get(position).getTask());
        mDescription.setText(list.get(position).getDescription());
        return v;
    }
}
