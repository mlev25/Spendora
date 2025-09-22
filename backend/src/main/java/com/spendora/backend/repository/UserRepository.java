package com.spendora.backend.repository;

import com.spendora.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    //These could be useful for login and registration methods
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    boolean existByUsername(String username);
    boolean existByEmail(String email);
}
