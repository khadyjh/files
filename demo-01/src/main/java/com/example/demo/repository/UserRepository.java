package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
@Transactional(readOnly = true)
public interface UserRepository extends JpaRepository<User,Long> {
    User findByEmail(String email);


    @Modifying
    @Transactional
    @Query(value = "UPDATE user a " +
            "SET a.enabled = TRUE WHERE a.email = ?1",nativeQuery = true)
    void enableUser(String email);
}
