package com.springcore.ai.scaiplatform.core.repository.api;

import com.springcore.ai.scaiplatform.core.dto.DocumentSearchReq;
import com.springcore.ai.scaiplatform.core.entity.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepositoryCustom {
    List<Document> searchByCriteria(DocumentSearchReq criteria);
}
