package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    @Query("SELECT e FROM Employee e WHERE NOT EXISTS " +
            "(SELECT t FROM Trafts t WHERE t.employee = e AND t.iscurrent = true)")
    List<Employee> findUnassignedEmployees();

}
