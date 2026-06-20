package com.springcore.ai.scaiplatform.core.repository.api;

import com.springcore.ai.scaiplatform.core.entity.Instruction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstructionRepository extends JpaRepository<Instruction, Long> {
}
