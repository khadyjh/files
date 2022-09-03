package com.example.demo.repository;

import com.example.demo.entity.ConfirmationToken;
import com.example.demo.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

public interface ConfirmationTokenRepository extends JpaRepository<ConfirmationToken,Long>{

    Optional<ConfirmationToken> findByToken(String token);
    ConfirmationToken findByUser(User user);

}
