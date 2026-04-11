package com.springcore.ai.scaiplatform.controller;

import com.springcore.ai.scaiplatform.dto.DocumentSearchReq;
import com.springcore.ai.scaiplatform.entity.Document;
import com.springcore.ai.scaiplatform.service.api.DocumentService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/document")
public class DocumentController {

    private final DocumentService documentService;

    @Autowired
    public DocumentController(DocumentService documentService) {
        this.documentService = documentService;
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/save")
    public ResponseEntity<Document> save(@RequestBody Document doc) {
        return ResponseEntity.ok(documentService.save(doc));
    }

    @PostMapping("/search")
    public ResponseEntity<List<Document>> search(@RequestBody DocumentSearchReq criteria) {
        return ResponseEntity.ok(documentService.search(criteria));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            boolean isDeleted = documentService.deleteById(id);
            if (isDeleted) {
                Map<String, Object> map = new HashMap<>();
                map.put("message", "Deleted successfully");
                return ResponseEntity.ok().body(map);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.internalServerError()
                    .body("{\"error\": \"" + e.getMessage() + "\"}");
        }
    }

    @GetMapping("/search/{id}")
    public ResponseEntity<Document> searchById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.searchById(id));
    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/generate-flow")
    @ResponseBody
    public ResponseEntity<Document> generateFlow(@RequestBody Document doc) {
        return ResponseEntity.ok(documentService.generateFlow(doc));

    }

    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/submit-flow")
    @ResponseBody
    public ResponseEntity<Document> submitFlow(@RequestBody Document doc) {
        return ResponseEntity.ok(documentService.submitFlow(doc));
    }

}
