package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.entity.InkQuest.InkNote;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface InkNoteRepository extends JpaRepository<InkNote, Long>, JpaSpecificationExecutor<InkNote> {

    List<InkNote> findByEmIdOrderByUpdatedAtDesc(Long emId);

    Optional<InkNote> findByIdAndEmId(Long id, Long emId);

    @Transactional
    void deleteByIdAndEmId(Long id, Long emId);
}
