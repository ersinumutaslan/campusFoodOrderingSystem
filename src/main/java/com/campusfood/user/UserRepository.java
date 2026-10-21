package com.campusfood.user;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

/** Database access for users. Spring Data JPA generates the implementation. */
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
}
