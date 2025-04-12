package org.example.sachbookapi.Repository;

import org.example.sachbookapi.Entity.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserModel, Long> {
    Optional<UserModel> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<UserModel> findByUsername(String username);
    boolean existsByUsername(String username);
}