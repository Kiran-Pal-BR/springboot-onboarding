package com.example.onboarding.contact;

import com.example.onboarding.contact.exception.DuplicateContactException;
import com.example.onboarding.contact.exception.ContactNotFoundException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ContactManagerTest {
    private ContactManager contactManager;

    @BeforeEach
    void setup(){
        contactManager = new ContactManager();
    }

    @Test
    @DisplayName("Should add contact successfully and retrieve it")
    void testAddandFindContact(){
        Contact contact = new Contact("C-1","Alice Smith", "alice@example.com","9876543210");
        contactManager.addContact(contact);

        Contact found= contactManager.findContactById("C-1");
        assertNotNull(found);
        assertEquals("Alice Smith",found.name());
        assertEquals(1,contactManager.getContactCount());

    }
    @Test
    @DisplayName("Should throw error when adding contact with duplicate ID")
    void testAddDuplicateContact_ThrowsException(){
        Contact c1 = new Contact("C-1","Alice","alice@example.com", "1234567890");
        Contact c2 = new Contact("C-1","Bob","bob@example.com","0987654321");

        contactManager.addContact(c1);

        assertThrows(DuplicateContactException.class,()-> contactManager.addContact(c2));

    }

    @Test
    @DisplayName("Should throw exception when contact id is not found")
    void testFindNonExistentContact_ThrowsException(){
        assertThrows(ContactNotFoundException.class, () -> contactManager.findContactById("INVALID-ID"));
    }

    @Test
    @DisplayName("Should return all contacts correctly")
    void testGetAllContacts() {
        contactManager.addContact(new Contact("C-1", "Alice", "alice@example.com", "1111111111"));
        contactManager.addContact(new Contact("C-2", "Bob", "bob@example.com", "2222222222"));

        List<Contact> contacts = contactManager.getAllContacts();
        assertEquals(2, contacts.size());
    }
    @Test
    @DisplayName("should Delete contact Successfully")
    void testDeleteContact(){
        contactManager.addContact(new Contact("C-1","Alice","alice@gmail.com","1111111111"));
        contactManager.deleteContact("C-1");
            assertEquals(0, contactManager.getContactCount());
            assertThrows(ContactNotFoundException.class, () -> contactManager.findContactById("C-1"));
        }

}
