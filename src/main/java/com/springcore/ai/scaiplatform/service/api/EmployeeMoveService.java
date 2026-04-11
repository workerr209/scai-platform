package com.springcore.ai.scaiplatform.service.api;

public interface EmployeeMoveService {
    void moveEmployee(Long employeeId, Long newManagerId);
    void unassignEmployee(Long employeeId);
}
