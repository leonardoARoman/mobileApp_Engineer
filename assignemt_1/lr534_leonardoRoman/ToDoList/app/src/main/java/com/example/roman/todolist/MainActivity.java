package com.example.roman.todolist;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import model.ToDoList;

public class MainActivity extends AppCompatActivity {
    // Widget fields
    private Button mAddItemToList;
    private ListView mListOfItems;
    private EditText mInput_task_title, mInput_task_description;
    // Data structures
    private List<ToDoList> list;     // To create a custom adapter Obj.
    private ToDoListAdapter adapter; // Custom adapter object (Most relevant object here)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
        setContentView(int layoutResID));
        This method inflates a layout and puts it on screen. When a layout is inflated,
        each widget in the layout file is instantiated as defined by its attributes.
        You specify which layout to inflate by passing in the layout’s resource ID.
        */
        setContentView(R.layout.activity_main);
        list = new ArrayList<>();
         /*
        public View findViewById(int id)
        This method accepts a resource ID of a widget and returns a View object.
        */
        mListOfItems = (ListView)findViewById(R.id.listOfItems);
        mAddItemToList = (Button)findViewById(R.id.addItemToList);
        mInput_task_title = (EditText)findViewById(R.id.input_task_title);
        mInput_task_description = (EditText)findViewById(R.id.input_task_description);

        addItemToList();
    }
    private void addItemToList(){
        /*
        .setOnClickListener(OnClickListener)
        Set the button to start listening for an event, click event.
        The setOnClickListener(OnClickListener) method takes a listener as its argument. In
        particular, it takes an object that implements OnClickListener.
        */
        mAddItemToList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                String tittle = mInput_task_title.getText().toString();
                String description = mInput_task_description.getText().toString();
                if (!tittle.isEmpty() && !description.isEmpty()){
                    ToDoList item = new ToDoList(tittle,description);
                    System.out.println(item);
                    list.add(item);
                    mInput_task_title.getText().clear();
                    mInput_task_description.getText().clear();
                    adapter = new ToDoListAdapter(getApplicationContext(),list);
                    mListOfItems.setAdapter(adapter);
                }
            }
        });

        /*
        To delete item on listView using onItemLongClick()
        */
        mListOfItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                System.out.println("Let go yo!");
                list.remove(mListOfItems.getItemAtPosition(position));
                adapter = new ToDoListAdapter(getApplicationContext(),list);
                mListOfItems.setAdapter(adapter);
                return false;
            }
        });
    }
}
