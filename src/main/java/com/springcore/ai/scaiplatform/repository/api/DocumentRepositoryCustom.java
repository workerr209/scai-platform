package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.dto.DocumentSearchReq;
import com.springcore.ai.scaiplatform.entity.Document;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepositoryCustom {
    List<Document> searchByCriteria(DocumentSearchReq criteria);
}
