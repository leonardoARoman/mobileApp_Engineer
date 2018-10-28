package model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ContactList implements Serializable {
    private List<Contact> contactList;
    private static ContactList instance;

    private ContactList() { contactList = new ArrayList<>(); }

    public static ContactList getInstance(){
        if(instance == null)
            instance = new ContactList();
        return instance;
    }

    public void addContact(Contact contact){
        contactList.add(contact);
    }

    public List<Contact> getContactList() {
        return contactList;
    }

    public void setContactList(List<Contact> contactList) {
        this.contactList = contactList;
    }
}
