package com.example.onboarding.contact;

import com.example.onboarding.contact.exception.ContactNotFoundException;
import com.example.onboarding.contact.exception.DuplicateContactException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ContactManager {
    // Logger initialization
    private static final Logger log = LoggerFactory.getLogger(ContactManager.class);
    private final Map<String, Contact> contactStore = new HashMap<>();

    public void addContact(Contact contact){
        Objects.requireNonNull(contact,"Contact cannot be null");
        if(contactStore.containsKey(contact.id())){
            log.error("Failed to add contact. ID {} already exists",contact.id());
            throw new DuplicateContactException("Contact with ID " + contact.id() + "already exists.");
        }
        contactStore.put(contact.id(),contact);
        log.info("Successfully added contact with ID: {}",contact.id());

    }
    // Find contact by ID
    public Contact findContactById(String id){
        Contact contact = contactStore.get(id);
        if(contact == null){
            log.warn("Contact with ID {} not found.", id);
            throw new ContactNotFoundException("Contact with Id " +id + " not found." );
        }
        log.debug("Found contact with ID: {}", id);
        return contact;
    }

    // Find contact by name using Optional(handles case hwere name doesn't exist)
    public Optional <Contact>findContactByName(String name){
        Objects.requireNonNull(name,"Name cannot be null");
        log.debug("Searching contact by name: {}", name);
        return contactStore.values().stream()
                .filter(contact -> contact.name().equalsIgnoreCase(name))
                .findFirst();
    }

    //Filter contact by domain/email suffix using stream.
    public List<Contact>findContactByEmailDomain(String domain){
        Objects.requireNonNull(domain,"Domain cannot be null");
        log.debug("Filtering contacts by email domain: {}", domain);
        return contactStore.values().stream()
                .filter(contact -> contact.email().endsWith(domain))
                .toList();
    }

    //Search contact match a query string in email or name
    public List<Contact>searchContacts(String keyword){
        if (keyword == null || keyword.isBlank()){
            return List.of();
        }
        log.debug("Searching contacts with keyword: {}", keyword);
        String lowerKeyword = keyword.toLowerCase();
        return contactStore.values().stream()
                .filter(c -> c.name().toLowerCase().contains(lowerKeyword) ||
                        c.email().toLowerCase().contains(lowerKeyword))
                .toList();
    }



    // Retrieve all contacts as an unmodifiable list
    public List<Contact> getAllContacts() {
        log.debug("Retrieving all contacts. Total count: {}", contactStore.size());
        return List.copyOf(contactStore.values());
    }

    // Delete a contact by ID
    public void deleteContact(String id){
    if (!contactStore.containsKey(id)){
        log.warn("Deletion failed. Contact with ID {} not found.", id);
        throw new ContactNotFoundException("Cannot delete. Contact with ID "+ id + " not found");
    }
    contactStore.remove(id);
    log.info("Successfully deleted contact with ID: {}", id);
    }


    //get total contact.
    public int getContactCount(){

        return contactStore.size();
    }
}
