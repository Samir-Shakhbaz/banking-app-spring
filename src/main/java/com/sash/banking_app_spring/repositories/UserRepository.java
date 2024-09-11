package com.sash.banking_app_spring.repositories;

import com.sash.banking_app_spring.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
//    User findById(Long id);
}
