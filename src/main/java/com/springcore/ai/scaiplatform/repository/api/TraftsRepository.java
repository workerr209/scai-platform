package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.entity.Trafts;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface TraftsRepository extends JpaRepository<Trafts, Long> {
    Optional<Trafts> findByEmployeeIdAndIscurrentTrue(Long employeeId);

    @Query(value = "SELECT * FROM am_trafts WHERE employeeid = :emId " +
            "ORDER BY effectivedate DESC, id DESC LIMIT 1",
            nativeQuery = true)
    Optional<Trafts> findLatestTraftByEmployeeId(@Param("emId") Long emId);

    @Query(value = """
    SELECT * FROM (
        SELECT *, ROW_NUMBER() OVER (PARTITION BY employeeid ORDER BY effectivedate DESC, id DESC) as rn
        FROM am_trafts
        WHERE employeeid IN :employeeIds
    ) as ranked_trafts
    WHERE rn = 1
    """, nativeQuery = true)
    List<Trafts> findLatestTraftsByEmployeeIds(@Param("employeeIds") Set<Long> employeeIds);

    @EntityGraph(attributePaths = {"employee", "manager", "department", "position", "jobRoles"})
    List<Trafts> findAllByIscurrentTrue();

    @EntityGraph(attributePaths = {"employee", "position", "department", "jobRoles"})
    List<Trafts> findAllByManagerIsNullAndIscurrentTrue();

    @EntityGraph(attributePaths = {"employee", "position", "department", "jobRoles"})
    List<Trafts> findAllByManagerIdAndIscurrentTrue(Long managerId);

    boolean existsByManagerIdAndIscurrentTrue(Long managerId);
}