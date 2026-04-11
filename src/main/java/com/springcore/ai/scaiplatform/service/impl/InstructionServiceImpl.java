package com.springcore.ai.scaiplatform.service.impl;

import com.springcore.ai.scaiplatform.entity.Instruction;
import com.springcore.ai.scaiplatform.repository.api.InstructionRepository;
import com.springcore.ai.scaiplatform.service.api.InstructionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class InstructionServiceImpl implements InstructionService {
    private static final Logger log = LoggerFactory.getLogger(InstructionServiceImpl.class);
    private final InstructionRepository instructionRepository;

    @Autowired
    public InstructionServiceImpl(InstructionRepository instructionRepository) {
        this.instructionRepository = instructionRepository;
    }

    @Override
    public List<Instruction> fetchAllInstructions() {
        log.info("Fetching all instructions from the dataset.");
        return instructionRepository.findAll();
    }

    @Override
    public Instruction fetchInstructionById(Long id) {
        return instructionRepository.findById(id).orElse(null);
    }

    @Override
    public Instruction createInstruction(Instruction instruction) {
        Instruction savedInstruction = instructionRepository.save(instruction);
        log.info("New instruction created with ID: {}", savedInstruction.getId());
        return savedInstruction;
    }

    @Override
    public Instruction updateInstruction(Long id, Instruction instructionDetails) {
        Instruction existingInstruction = fetchInstructionById(id);
        if (existingInstruction != null) {
            existingInstruction.setInstruction(instructionDetails.getInstruction());
            existingInstruction.setOutput(instructionDetails.getOutput());
            Instruction updatedInstruction = instructionRepository.save(existingInstruction);
            log.info("Instruction updated for ID: {}", id);
            return updatedInstruction;
        } else {
            log.warn("Cannot update. Instruction not found for ID: {}", id);
            return null;
        }
    }

    @Override
    public boolean deleteInstruction(Long id) {
        if (instructionRepository.existsById(id)) {
            instructionRepository.deleteById(id);
            log.info("Instruction deleted for ID: {}", id);
            return true;
        } else {
            log.warn("Cannot delete. Instruction not found for ID: {}", id);
            return false;
        }
    }

}
