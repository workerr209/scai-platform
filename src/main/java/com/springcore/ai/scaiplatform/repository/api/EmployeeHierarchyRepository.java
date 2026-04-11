package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.dto.SupervisorInfo;
import com.springcore.ai.scaiplatform.entity.EmployeeHierarchy;
import com.springcore.ai.scaiplatform.entity.EmployeeHierarchyId;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeHierarchyRepository extends JpaRepository<EmployeeHierarchy, EmployeeHierarchyId> {

    boolean existsByIdAncestoridAndIdDescendantid(Long ancestorid, Long descendantid);

    @Modifying
    @Transactional
    @Query(value = """
    DELETE FROM am_employee_hierarchy\s
    WHERE EXISTS (
        SELECT 1 FROM am_employee_hierarchy sub1\s
        WHERE sub1.ancestorid = :employeeId\s
        AND sub1.descendantid = am_employee_hierarchy.descendantid
    )
    AND EXISTS (
        SELECT 1 FROM am_employee_hierarchy sub2\s
        WHERE sub2.descendantid = :employeeId\s
        AND sub2.ancestorid != sub2.descendantid\s
        AND sub2.ancestorid = am_employee_hierarchy.ancestorid
    )
   \s""", nativeQuery = true)
    void deleteOldHierarchy(@Param("employeeId") Long employeeId);

    @Query(value = "SELECT e.id FROM am_employee e " +
            "JOIN am_employee_hierarchy h ON e.id = h.ancestorid " +
            "WHERE h.descendantid = :employeeId " +
            "ORDER BY h.depth DESC", nativeQuery = true)
    List<Long> getAncestorIds(@Param("employeeId") Long employeeId);

    @Query("""
    SELECT h.ancestor.id emId, h.ancestor.code emCode, h.ancestor.name emName, h.depth level
    FROM EmployeeHierarchy h\s
    WHERE h.descendant.id = :employeeId\s
    AND h.depth > 0\s
    ORDER BY h.depth ASC
""")
    List<SupervisorInfo> findSupervisors(@Param("employeeId") Long employeeId);

    @Modifying
    @Query(value = """
        INSERT IGNORE INTO am_employee_hierarchy (ancestorid, descendantid, depth)\s
        VALUES (:empId, :empId, 0)
   \s""", nativeQuery = true)
    void insertSelfReference(@Param("empId") Long employeeId);

    /**
     * 3. ผูกสายสัมพันธ์เข้ากับหัวหน้าใหม่ (เอาสายหัวหน้าใหม่ มา Cross Join กับสายลูกน้องของตัวเอง)
     */
    @Modifying
    @Query(value = """
        INSERT INTO am_employee_hierarchy (ancestorid, descendantid, depth)
        SELECT supertree.ancestorid, subtree.descendantid, supertree.depth + subtree.depth + 1
        FROM am_employee_hierarchy AS supertree
        CROSS JOIN am_employee_hierarchy AS subtree
        WHERE supertree.descendantid = :newMgrId
        AND subtree.ancestorid = :empId
    """, nativeQuery = true)
    void insertNewHierarchy(@Param("empId") Long employeeId, @Param("newMgrId") Long newManagerId);

}