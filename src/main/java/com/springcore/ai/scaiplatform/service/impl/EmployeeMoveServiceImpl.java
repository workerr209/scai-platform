package com.springcore.ai.scaiplatform.service.impl;

import com.springcore.ai.scaiplatform.entity.Employee;
import com.springcore.ai.scaiplatform.entity.Trafts;
import com.springcore.ai.scaiplatform.repository.api.EmployeeHierarchyRepository;
import com.springcore.ai.scaiplatform.repository.api.EmployeeRepository;
import com.springcore.ai.scaiplatform.repository.api.TraftsRepository;
import com.springcore.ai.scaiplatform.service.api.EmployeeMoveService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeMoveServiceImpl implements EmployeeMoveService {

    private final TraftsRepository traftsRepository;
    private final EmployeeHierarchyRepository hierarchyRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    @Transactional
    public void moveEmployee(Long employeeId, Long newManagerId) {
        // 1. Prevent moving an employee to be their own manager
        if (employeeId.compareTo(newManagerId) == 0) {
            throw new RuntimeException("Cannot move employee to be their own manager.");
        }

        // 2. Circular Reference Check (Using Closure Table ability)
        // If the "employee to move" is an ancestor of the "new manager", move is prohibited.
        boolean isSubordinate = hierarchyRepository.existsByIdAncestoridAndIdDescendantid(employeeId, newManagerId);
        if (isSubordinate) {
            throw new RuntimeException("Circular Reference Detected: Cannot move a manager under their own subordinate.");
        }

        LocalDateTime now = LocalDateTime.now();

        // 3. Retrieve employee and new manager data
        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new RuntimeException("Employee not found with ID: " + employeeId));
        Employee newManager = employeeRepository.findById(newManagerId)
                .orElseThrow(() -> new RuntimeException("New manager not found with ID: " + newManagerId));

        // 4. Handle Trafts (Support both existing staff and new recruits from unassigned list)
        Optional<Trafts> currentTraftOpt = traftsRepository.findLatestTraftByEmployeeId(employeeId);
        Trafts.TraftsBuilder newTraftBuilder = Trafts.builder()
                .employee(employee)
                .manager(newManager)
                .effectivedate(now)
                .iscurrent(true)
                .position(currentTraftOpt.map(Trafts::getPosition).orElse(null))
                .department(currentTraftOpt.map(Trafts::getDepartment).orElse(null))
                .trafttype("ORG_CHART_MOVE");

        if (currentTraftOpt.isPresent()) {
            // Case: Moving existing employee
            Trafts currentTraft = currentTraftOpt.get();
            currentTraft.setIscurrent(false);
            currentTraft.setEnddate(now);
            traftsRepository.save(currentTraft);

            // Copy department, position, and roles from the previous traft
            newTraftBuilder
                    .department(currentTraft.getDepartment())
                    .position(currentTraft.getPosition())
                    .jobRoles(new java.util.HashSet<>(currentTraft.getJobRoles()));
        } else {
            // Case: New employee (from unassigned list)
            newTraftBuilder.jobRoles(new java.util.HashSet<>());
        }

        Trafts build = newTraftBuilder.build();
        traftsRepository.save(build);

        // 5. Update Closure Table (Hierarchy Bulk Update)

        // 5.1 การันตีว่าทั้งคู่มี Self-Reference (depth=0) ป้องกันบั๊กตอน Move พนักงานใหม่
        hierarchyRepository.insertSelfReference(employeeId);
        hierarchyRepository.insertSelfReference(newManagerId);

        // 5.2 ตัดความสัมพันธ์กับหัวหน้าเก่าทั้งหมด
        hierarchyRepository.deleteOldHierarchy(employeeId);

        // 5.3 เชื่อมต่อกับหัวหน้าใหม่
        hierarchyRepository.insertNewHierarchy(employeeId, newManagerId);
    }

    @Override
    @Transactional
    public void unassignEmployee(Long employeeId) {
        boolean hasSubordinates = traftsRepository.existsByManagerIdAndIscurrentTrue(employeeId);
        if (hasSubordinates) {
            throw new RuntimeException("Cannot unassign an employee who has subordinates. Please reassign their subordinates first.");
        }

        Optional<Trafts> currentTraftOpt = traftsRepository.findByEmployeeIdAndIscurrentTrue(employeeId);
        if (currentTraftOpt.isPresent()) {
            Trafts currentTraft = currentTraftOpt.get();
            currentTraft.setIscurrent(false);
            currentTraft.setEnddate(LocalDateTime.now());
            traftsRepository.save(currentTraft);
        }

        hierarchyRepository.insertSelfReference(employeeId);
        hierarchyRepository.deleteOldHierarchy(employeeId);
    }
}
