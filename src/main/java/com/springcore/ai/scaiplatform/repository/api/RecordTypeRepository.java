package com.springcore.ai.scaiplatform.repository.api;

import com.springcore.ai.scaiplatform.entity.RecordType;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface RecordTypeRepository extends JpaRepository<RecordType, Long> {
	
	// @Query("select p from RecordType p where p.name = :recordtypeName")
	@Query("select p from RecordType p LEFT JOIN FETCH p.fieldList where p.name = :recordtypeName")
	RecordType findRecordTypeByName(@Param("recordtypeName") String recordtypeName);

	@Query("SELECT DISTINCT r FROM RecordType r LEFT JOIN FETCH r.fieldList")
	List<RecordType> findAllWithFields();

	Slice<RecordType> readAllBy(Pageable pageable);

}
