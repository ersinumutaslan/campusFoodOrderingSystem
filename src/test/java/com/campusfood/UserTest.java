package com.campusfood;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void createsUserWithGivenRole() {
        User user = new User("Ayşe Yılmaz", "ayse@campus.edu", Role.STUDENT);

        assertEquals("Ayşe Yılmaz", user.getName());
        assertEquals("ayse@campus.edu", user.getEmail());
        assertEquals(Role.STUDENT, user.getRole());
    }

    @Test
    void emailWithoutAtSignIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("Ayşe Yılmaz", "ayse.campus.edu", Role.STUDENT));
    }

    @Test
    void missingRoleIsRejected() {
        assertThrows(IllegalArgumentException.class,
                () -> new User("Ayşe Yılmaz", "ayse@campus.edu", null));
    }
}
