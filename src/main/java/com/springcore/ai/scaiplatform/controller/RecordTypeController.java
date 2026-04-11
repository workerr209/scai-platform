package com.springcore.ai.scaiplatform.controller;

import com.springcore.ai.scaiplatform.dto.FormMasterDeleteDTO;
import com.springcore.ai.scaiplatform.entity.RecordType;
import com.springcore.ai.scaiplatform.service.api.AdminService;
import com.springcore.ai.scaiplatform.service.api.RecordTypeService;
import io.reactivex.rxjava3.core.Flowable;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/v1/recordtype")
public class RecordTypeController {

	private final AdminService adminService;
	private final RecordTypeService recordTypeService;
	
	@Autowired
	public RecordTypeController(AdminService adminService, RecordTypeService recordTypeService) {
		this.adminService = adminService;
		this.recordTypeService = recordTypeService;
	}

	@GetMapping("/list")
	public ResponseEntity<List<RecordType>> listRecordType(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "20") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(recordTypeService.getAllRecordType(pageable));
	}

	@GetMapping("/reload-all")
	public ResponseEntity<Boolean> reloadAllRecordType() {
		return ResponseEntity.ok(recordTypeService.reloadAllRecordType());
	}
	
	@GetMapping("/reload/{recordTypeName}")
	public ResponseEntity<Boolean> reloadRecordTypeByName(@PathVariable @Valid String recordTypeName) {
		return ResponseEntity.ok(recordTypeService.reloadRecordTypeByName(recordTypeName));
	}

	@GetMapping("/search/{recordTypeName}")
	public ResponseEntity<RecordType> searchRecordTypeByName(@PathVariable @Valid String recordTypeName) {
		return ResponseEntity.ok(recordTypeService.getRecordType(recordTypeName));
	}

	@GetMapping("/data/search/page/{recordTypeName}")
	public ResponseEntity<List<Object>> searchDataOfRecordTypeWithPage(@PathVariable @Valid String recordTypeName
			, @RequestParam(defaultValue = "0") int page
			, @RequestParam(defaultValue = "20") int size
			, @Valid @RequestParam MultiValueMap<String, String> param) {
		Pageable pageable = PageRequest.of(page, size);
		return ResponseEntity.ok(recordTypeService.searchDataOfRecordTypeWithPage(pageable, recordTypeName, param));
	}

	@GetMapping("/data/search/{recordTypeName}")
	public <T> List<T> searchDataOfRecordType(@PathVariable @Valid String recordTypeName, @Valid @RequestParam MultiValueMap<String, String> param) throws ClassNotFoundException {
		return adminService.findDataOfRecordTypeByCriteria(recordTypeName, param);
	}
	
	@GetMapping("/data/search/{recordTypeName}/{id}")
	public Flowable<?> searchDataOfRecordTypeById(@PathVariable @Valid String recordTypeName,
                                                  @PathVariable @Valid Long id) {
		return recordTypeService.loadClassFromRecordTypeName(recordTypeName).map(clazz -> adminService.findDataOfRecordTypeById(clazz, id));
	}

	@DeleteMapping("/data/delete/{recordTypeName}")
	public ResponseEntity<Integer> deleteDataOfRecordType(@PathVariable String recordTypeName,
                                                          @RequestBody FormMasterDeleteDTO param) {
		int deleteCountRecord = adminService.deleteDataOfRecordTypeByIds(recordTypeName, param);
		log.info("Deleted {} records", deleteCountRecord);
		return ResponseEntity.ok(deleteCountRecord);
	}
	
	@ResponseStatus(HttpStatus.CREATED)
	@PostMapping("/data/save/{recordTypeName}")
	public Object saveDataOfRecordType(@PathVariable String recordTypeName,
                                     @RequestBody Map<String, Object> param) throws IllegalArgumentException {
		recordTypeService.convertLookupToLong(recordTypeName, param);
		Flowable<Class<?>> flowable = recordTypeService.loadClassFromRecordTypeName(recordTypeName);
		Object item = flowable.map(clazz -> adminService.saveDataOfRecordType(clazz, param)).blockingFirst();
		return ResponseEntity.ok(item);
	}
	
	@PutMapping("/data/update/{recordTypeName}")
	public Flowable<Object> updateDataOfRecordType(@PathVariable String recordTypeName,
                                                   @RequestBody Map<String, Object> param) throws IllegalArgumentException {
		return recordTypeService.loadClassFromRecordTypeName(recordTypeName).map(clazz -> adminService.updateDataOfRecordType(clazz, param));
	}


}
