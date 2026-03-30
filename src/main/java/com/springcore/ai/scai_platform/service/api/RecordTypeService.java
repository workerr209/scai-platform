package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.entity.RecordType;
import io.reactivex.rxjava3.core.Flowable;
import org.springframework.data.domain.Pageable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface RecordTypeService {

	List<RecordType> getAllRecordType(Pageable pageable);

	List<Object> searchDataOfRecordTypeWithPage(Pageable pageable, String recordTypeName, MultiValueMap<String, String> param);

	Boolean reloadAllRecordType();

	Optional<RecordType> getRecordTypeOptional(String recordTypeName);

	RecordType getRecordType(String recordTypeName);

	Boolean reloadRecordTypeByName(String recordTypeName);

	Flowable<Class<?>> loadClassFromRecordTypeName(String recordTypeName);

	LinkedMultiValueMap<String, String> buildDefaultFilters(String recordTypeName);

	void convertLookupToLong(String recordTypeName, Map<String, Object> param);
}
