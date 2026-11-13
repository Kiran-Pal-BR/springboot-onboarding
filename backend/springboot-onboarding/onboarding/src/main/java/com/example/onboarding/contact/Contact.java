package com.example.onboarding.contact;

import java.util.Objects;

public record Contact(String id, String name, String email, String phoneNumber) {
    public Contact{
        Objects.requireNonNull(id,"Contact Id cannot be null");
        Objects.requireNonNull(name,"Contact name cannot be null");
        Objects.requireNonNull(email,"Contact email cannot be null");

        if(id.isBlank() || name.isBlank()){
            throw new IllegalArgumentException("ID and Name cannot be blank.");
        }
    }

}
