package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT u.id FROM User u WHERE u.emid.id = :employeeId")
    Long findUserIdByEmployeeId(@Param("employeeId") Long employeeId);
}