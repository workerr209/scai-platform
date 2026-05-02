package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.entity.InkQuest.InkWritingGoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InkWritingGoalRepository extends JpaRepository<InkWritingGoal, Long> {

    Optional<InkWritingGoal> findByEmId(Long emId);

    boolean existsByEmId(Long emId);
}
