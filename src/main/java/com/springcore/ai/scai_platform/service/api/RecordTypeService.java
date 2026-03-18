package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.entity.RecordType;
import io.reactivex.rxjava3.core.Flowable;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface RecordTypeService {

	List<RecordType> getAllRecordType(Pageable pageable);

	Boolean reloadAllRecordType();

	Optional<RecordType> getRecordTypeOptional(String recordTypeName);

	RecordType getRecordType(String recordTypeName);

	Boolean reloadRecordTypeByName(String recordTypeName);

	Flowable<Class<?>> loadClassFromRecordTypeName(String recordTypeName);
}
