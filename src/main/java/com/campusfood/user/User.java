package com.campusfood.user;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/** A person using the system: a student, restaurant staff member, or admin. Stored in the "users" table. */
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Enumerated(EnumType.STRING)
    private Role role;

    /** Used by JPA to create objects from database rows. */
    protected User() {
    }

    public User(String name, String email, Role role) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("User name must not be empty");
        }
        if (email == null || !email.contains("@")) {
            throw new IllegalArgumentException("User email must be a valid address");
        }
        if (role == null) {
            throw new IllegalArgumentException("User role must not be null");
        }
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }

    @Override
    public String toString() {
        return name + " <" + email + "> (" + role + ")";
    }
}
