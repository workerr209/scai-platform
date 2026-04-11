package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.entity.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction, Long> {
}
