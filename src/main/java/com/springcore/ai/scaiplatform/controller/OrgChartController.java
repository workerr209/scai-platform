package com.springcore.ai.scaiplatform.controller;

import com.springcore.ai.scaiplatform.dto.MoveEmployeeRequest;
import com.springcore.ai.scaiplatform.dto.OrgChartNodeDTO;
import com.springcore.ai.scaiplatform.service.api.EmployeeMoveService;
import com.springcore.ai.scaiplatform.service.api.OrgChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/org-chart")
@RequiredArgsConstructor
public class OrgChartController {

    private final EmployeeMoveService moveService;
    private final OrgChartService orgChartService;

    @GetMapping("/tree")
    public ResponseEntity<List<OrgChartNodeDTO>> getFullTree() {
        return ResponseEntity.ok(orgChartService.getOrgChartTree());
    }

    @PostMapping("/move")
    public ResponseEntity<String> moveEmployee(@RequestBody MoveEmployeeRequest request) {
        try {
            moveService.moveEmployee(request.getEmployeeId(), request.getNewManagerId());
            return ResponseEntity.ok("success");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/unassign/{employeeId}")
    public ResponseEntity<String> unassignEmployee(@PathVariable Long employeeId) {
        moveService.unassignEmployee(employeeId);
        return ResponseEntity.ok("Successfully unassigned");
    }

    @GetMapping("/path/{id}")
    public ResponseEntity<List<Long>> getPath(@PathVariable Long id) {
        return ResponseEntity.ok(orgChartService.getPathToRoot(id));
    }

    @GetMapping("/roots")
    public ResponseEntity<List<OrgChartNodeDTO>> getRoots() {
        return ResponseEntity.ok(orgChartService.getRoots());
    }

    @GetMapping("/children/{managerId}")
    public ResponseEntity<List<OrgChartNodeDTO>> getChildren(@PathVariable Long managerId) {
        return ResponseEntity.ok(orgChartService.getChildren(managerId));
    }

    @GetMapping("/unassigned")
    public ResponseEntity<List<OrgChartNodeDTO>> getUnassignedEmployees() {
        return ResponseEntity.ok(orgChartService.getUnassignedEmployees());
    }

}
