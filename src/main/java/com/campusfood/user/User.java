package com.campusfood.user;

/** A person using the system: a student, restaurant staff member, or admin. */
public class User {

    private final String name;
    private final String email;
    private final Role role;

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
