package model;

import android.widget.CheckBox;
import android.widget.ListView;

import com.example.roman.contanctlist.R;

import java.io.Serializable;
import java.util.*;

public class Contact implements Serializable {
    private String name, phoneNumber;
    private List<Contact> friends;
    private boolean isChecked;

    public  Contact(String name, String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
        isChecked = false;
        friends = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public List<Contact> getFriends() {
        return friends;
    }

    public void setFriends(List<Contact> friends) {
        this.friends = friends;
    }

    public boolean isChecked() { return isChecked; }

    public void setChecked (boolean isChecked) {this.isChecked = isChecked; }

    public void addFriend(Contact contact){ friends.add(contact); }

}
