package com.footybot.repository;

import com.footybot.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // This method allows Spring Security to find a user by their username
    Optional<User> findByUsername(String username);
}