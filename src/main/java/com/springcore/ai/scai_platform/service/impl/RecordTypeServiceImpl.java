package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.entity.RecordType;
import com.springcore.ai.scai_platform.entity.RecordTypeField;
import com.springcore.ai.scai_platform.properties.ApplicationProperties;
import com.springcore.ai.scai_platform.repository.api.RecordTypeRepository;
import com.springcore.ai.scai_platform.service.api.RecordTypeService;
import io.reactivex.rxjava3.core.Flowable;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.util.StopWatch;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
public class RecordTypeServiceImpl implements RecordTypeService {
	private final Map<String, RecordType> allRecordType;
	private final Map<Pageable, Map<String, RecordType>> mapRecordTypeByPage;
	private final RecordTypeRepository recordTypeRepository;
	private final ApplicationProperties applicationProperties;

	@Autowired
	public RecordTypeServiceImpl(RecordTypeRepository recordTypeRepository, ApplicationProperties applicationProperties) {
		this.applicationProperties = applicationProperties;
		this.recordTypeRepository = recordTypeRepository;
		allRecordType = new ConcurrentHashMap<>(applicationProperties.getGeneral().getRecordtypeMapInitSize());
		mapRecordTypeByPage = new ConcurrentHashMap<>(applicationProperties.getGeneral().getRecordtypeMapInitSize());
	}
	
	@Override
	public Optional<RecordType> getRecordType(String recordTypeName) {
		ensureLoadAcRecordType(recordTypeName);
		RecordType acRecordtype = this.allRecordType.get(recordTypeName);
		Assert.notNull(acRecordtype, "RecordType : " + recordTypeName + " not found");
		return Optional.of(acRecordtype);
	}
	
	@Override
	public Boolean reloadRecordTypeByName(String recordTypeName) {
		final RecordType removed = this.allRecordType.remove(recordTypeName);
		if (removed != null) {
			removed.getRecordtypeFields()
					.stream()
					.map(RecordTypeField::getName)
					.forEach(this.allRecordType::remove);
		} else {
			return false;
		}

		return true;
	}

	@Override
	public Flowable<Class<?>> loadClassFromRecordTypeName(String recordTypeName) {
		return Flowable.just(getRecordType(recordTypeName))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.map(RecordType::getClassName)
				.filter(StringUtils::hasLength)
				.map(Class::forName);
	}

	private void ensureLoadAcRecordType(String recordTypeName) {
		if (this.allRecordType.containsKey(recordTypeName)) {
			log.debug("load recordtype: {} from memory.", recordTypeName);
		} else {
			this.allRecordType.computeIfAbsent(recordTypeName, name -> {
				RecordType rt = this.recordTypeRepository.findRecordTypeByName(name);
				if (rt == null) {
					throw new IllegalArgumentException("RecordType not found in DB: " + name);
				}
				log.debug("load recordtype: {} from database.", name);
				return rt;
			});
		}
	}

	@Override
	@Transactional(readOnly = true)
	public List<RecordType> getAllRecordType(Pageable pageable) {
		Map<String, RecordType> pageCache = mapRecordTypeByPage.computeIfAbsent(pageable, p -> {
			log.info("Cache miss for page: {}, fetching from DB...", p.getPageNumber());
			Slice<RecordType> slice = fetchRecordTypesByPages(p);
			List<RecordType> content = slice.getContent();
			Map<String, RecordType> resultMap = new ConcurrentHashMap<>(applicationProperties.getGeneral().getRecordtypeMapInitSize());
			content.forEach(rt -> {
				log.debug("RecordType: {}, Fields size: {}", rt.getName(), rt.getRecordtypeFields().size());
				resultMap.put(rt.getName(), rt);
				allRecordType.putIfAbsent(rt.getName(), rt);
			});
			return resultMap;
		});

		return new ArrayList<>(pageCache.values());
	}

	@Override
	public Boolean reloadAllRecordType() {
		StopWatch sw = new StopWatch();
		sw.start();

		try {
			List<RecordType> recordTypes = fetchAllRecordTypes();
			allRecordType.clear();
			mapRecordTypeByPage.clear();
			recordTypes.forEach(recordType -> allRecordType.putIfAbsent(recordType.getName(), recordType));

			sw.stop();
			log.info("Reload all RecordTypes success, found: {} items (took {} ms)", recordTypes.size(), sw.getTotalTimeMillis());
			return true;
		} catch (Exception e) {
			sw.stop();
			log.error("Error while reloading all RecordTypes after {} ms", sw.getTotalTimeMillis(), e);
			return false;
		}
	}

	private Slice<RecordType> fetchRecordTypesByPages(Pageable pageable) {
		StopWatch sw = new StopWatch();
		sw.start();

		try {
			Slice<RecordType> recordTypes = recordTypeRepository.readAllBy(pageable);
			sw.stop();
			log.info("Fetching RecordTypes by page {} from DB, (took {} ms)", pageable, sw.getTotalTimeMillis());
			return recordTypes;
		} catch (Exception e) {
			sw.stop();
			log.error("CRITICAL ERROR DURING FETCH: ", e); // ดูว่า e.getCause() คืออะไร
			throw e;
		}
	}

	private List<RecordType> fetchAllRecordTypes() {
		StopWatch sw = new StopWatch();
		sw.start();

		try {
			List<RecordType> recordTypes = recordTypeRepository.findAllWithFields();
			sw.stop();
			log.info("Fetching all RecordTypes from DB, found: {} items (took {} ms)", recordTypes.size(), sw.getTotalTimeMillis());
			return recordTypes;
		} catch (Exception e) {
			sw.stop();
			log.error("CRITICAL ERROR DURING FETCH: ", e); // ดูว่า e.getCause() คืออะไร
			throw e;
		}
	}

}
