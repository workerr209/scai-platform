package com.springcore.ai.scaiplatform.service.api;

import com.springcore.ai.scaiplatform.dto.DocumentSearchReq;
import com.springcore.ai.scaiplatform.entity.Document;

import java.util.List;

public interface DocumentService {
    Document save(Document form);

    Document generateFlow(Document doc);

    Document submitFlow(Document doc);

    List<Document> search(DocumentSearchReq form);

    Document searchById(Long id);

    boolean deleteById(Long id);

}
