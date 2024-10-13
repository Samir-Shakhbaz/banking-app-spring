package com.sash.banking_app_spring.repositories;

import com.sash.banking_app_spring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    User findByResetToken(String token);

    boolean existsByPassword(String encodedPassword);

//    User findById(Long id);
}
