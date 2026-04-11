package com.springcore.ai.scaiplatform.service.api;

import com.springcore.ai.scaiplatform.dto.FormMasterDeleteDTO;
import com.springcore.ai.scaiplatform.dto.ManuByUserDTO;
import com.springcore.ai.scaiplatform.entity.GroupMenu;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

public interface AdminService {

	List<GroupMenu> findAllAllowMenuByUsername(String username);

	<T> T  saveDataOfRecordType(Class<T> clazz, Map<String, Object> param);

//	Flowable<Integer> deleteDataOfRecordTypeByIds(String recordTypeName, FormMasterDeleteDTO param);
	int deleteDataOfRecordTypeByIds(String recordTypeName, FormMasterDeleteDTO param);

	<T> List<T> findDataOfRecordTypeByCriteria(String recordTypeName, MultiValueMap<String, String> param) throws ClassNotFoundException;

	<T> T  findDataOfRecordTypeById(Class<T> loadClassFromRecordTypeName, Long id);

	<T> T updateDataOfRecordType(Class<T> clazz, Map<String, Object> param);

	List<ManuByUserDTO> listMenuByUsername(String username);
}
