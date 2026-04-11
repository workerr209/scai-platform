package com.springcore.ai.scaiplatform.controller;


import com.springcore.ai.scaiplatform.entity.Instruction;
import com.springcore.ai.scaiplatform.service.impl.InstructionServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v0/dataset/instruction")
public class InstructionController {

    private static final Logger log = LoggerFactory.getLogger(InstructionController.class);
    private final InstructionServiceImpl service;

    @Autowired
    public InstructionController(InstructionServiceImpl service) {
        this.service = service;
    }

    // --- 1. READ ALL: ดึงข้อมูลทั้งหมด ---
    /**
     * URI: GET /api/v1/dataset/instruction
     */
    @GetMapping
    public List<Instruction> getAllInstructions() {
        log.info("Fetching all instructions from the dataset.");
        return service.fetchAllInstructions();
    }

    // --- 2. READ BY ID: ดึงข้อมูลตาม ID ---
    /**
     * URI: GET /api/v1/dataset/instruction/{id}
     */
    @GetMapping("/{id}")
    public ResponseEntity<Instruction> getInstructionById(@PathVariable Long id) {
        Instruction instruction = service.fetchInstructionById(id);

        if (instruction != null) {
            log.info("Found instruction with ID: {}", id);
            return ResponseEntity.ok(instruction);
        } else {
            log.warn("Instruction not found for ID: {}", id);
            return ResponseEntity.notFound().build();
        }
    }

    // --- 3. CREATE: เพิ่มข้อมูลใหม่ ---
    /**
     * URI: POST /api/v1/dataset/instruction
     */
    @PostMapping
    public ResponseEntity<Instruction> createInstruction(@RequestBody Instruction instruction) {
        try {
            // ID จะถูกสร้างโดย DB อัตโนมัติ
            return ResponseEntity.status(HttpStatus.CREATED).body(service.createInstruction(instruction));
        } catch (Exception e) {
            log.error("Error creating instruction.", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * URI: PUT /api/v1/dataset/instruction/{id}
     */
    @PostMapping("/batch")
    public ResponseEntity<List<Instruction>> createInstructions(@RequestBody List<Instruction> instructions) {
        try {
            return ResponseEntity.status(HttpStatus.CREATED).body(instructions.stream()
                    .map(service::createInstruction)
                    .toList());
        } catch (Exception e) {
            log.error("Error creating instruction.", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * URI: PUT /api/v1/dataset/instruction/{id}
     */
    @PutMapping("/{id}")
    public ResponseEntity<Instruction> updateInstruction(@PathVariable Long id, @RequestBody Instruction instructionDetails) {
        Instruction updatedInstruction = service.updateInstruction(id, instructionDetails);
        if (updatedInstruction != null) {
            return ResponseEntity.ok(updatedInstruction);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // --- 5. DELETE: ลบข้อมูล ---
    /**
     * URI: DELETE /api/v1/dataset/instruction/{id}
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteInstruction(@PathVariable Long id) {
        if (service.deleteInstruction(id)) {
            return ResponseEntity.noContent().build(); // 204 No Content
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
