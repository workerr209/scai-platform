package com.springcore.ai.scaiplatform.chapterly.repository;

import com.springcore.ai.scaiplatform.chapterly.entity.ChapterlyInboxConversation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChapterlyInboxConversationRepository extends JpaRepository<ChapterlyInboxConversation, Long> {
    Optional<ChapterlyInboxConversation> findByParticipantOneIdAndParticipantTwoId(Long participantOneId, Long participantTwoId);

    Optional<ChapterlyInboxConversation> findByIdAndParticipantOneIdOrIdAndParticipantTwoId(
            Long participantOneConversationId,
            Long participantOneId,
            Long participantTwoConversationId,
            Long participantTwoId
    );

    List<ChapterlyInboxConversation> findByParticipantOneIdOrParticipantTwoIdOrderByLastMessageAtDesc(
            Long participantOneId,
            Long participantTwoId
    );
}
