package com.springcore.ai.scaiplatform.core.service.api;

import com.springcore.ai.scaiplatform.core.entity.Instruction;

import java.util.List;

public interface InstructionService {

    List<Instruction> fetchAllInstructions();

    Instruction fetchInstructionById(Long id);

    Instruction createInstruction(Instruction instruction);

    Instruction updateInstruction(Long id, Instruction instructionDetails);

    boolean deleteInstruction(Long id);
}
