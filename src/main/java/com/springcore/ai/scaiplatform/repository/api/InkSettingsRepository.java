package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.entity.InkQuest.InkSettings;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface InkSettingsRepository extends JpaRepository<InkSettings, Long> {

    Optional<InkSettings> findByEmId(Long emId);

    boolean existsByEmId(Long emId);
}
