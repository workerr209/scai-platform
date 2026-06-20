package com.springcore.ai.scaiplatform.core.service.api;

import com.springcore.ai.scaiplatform.core.dto.DocumentSearchReq;
import com.springcore.ai.scaiplatform.core.entity.Document;

import java.util.List;

public interface DocumentService {
    Document save(Document form);

    Document generateFlow(Document doc);

    Document submitFlow(Document doc);

    List<Document> search(DocumentSearchReq form);

    Document searchById(Long id);

    boolean deleteById(Long id);

}
