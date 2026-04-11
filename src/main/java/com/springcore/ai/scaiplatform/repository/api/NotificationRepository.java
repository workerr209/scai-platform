package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.entity.Notification;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findTop20ByUserIdOrderByCreateDateDesc(Long userId);

    Integer countByUserIdAndReadFalse(Long userId);

    @Modifying
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId AND n.read = false")
    void markAllAsReadByUserId(@Param("userId") Long userId);

    boolean existsByParentId(Long docId);

    @Transactional
    void deleteByParentId(Long docId);

}
