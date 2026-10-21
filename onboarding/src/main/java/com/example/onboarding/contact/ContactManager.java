package com.example.onboarding.contact;

import com.example.onboarding.contact.exception.ContactNotFoundException;
import com.example.onboarding.contact.exception.DuplicateContactException;

import java.util.*;

public class ContactManager {
    private final Map<String, Contact> contactStore = new HashMap<>();

    public void addContact(Contact contact){
        Objects.requireNonNull(contact,"Contact cannot be null");
        if(contactStore.containsKey(contact.id())){
            throw new DuplicateContactException("Contact with ID " + contact.id() + "already exists.");
        }
        contactStore.put(contact.id(),contact);

    }
    // Find contact by ID
    public Contact findContactById(String id){
        Contact contact = contactStore.get(id);
        if(contact == null){
            throw new ContactNotFoundException("Contact with Id " +id + " not found." );
        }
        return contact;
    }


    // Retrieve all contacts as an unmodifiable list
    public List<Contact> getAllContacts() {
        return List.copyOf(contactStore.values());
    }

    // Delete a contact by ID
    public void deleteContact(String id){
    if (!contactStore.containsKey(id)){
        throw new ContactNotFoundException("Cannot delete. Contact with ID "+ id + " not found");
    }
    contactStore.remove(id);
    }


    //get total contact.
    public int getContactCount(){
    return contactStore.size();
    }
}
