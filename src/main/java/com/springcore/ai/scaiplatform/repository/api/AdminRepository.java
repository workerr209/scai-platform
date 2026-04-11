package com.springcore.ai.scaiplatform.repository.api;


import com.springcore.ai.scaiplatform.domain.mapping.RoleMappingMenu;
import com.springcore.ai.scaiplatform.entity.GroupMenu;
import com.springcore.ai.scaiplatform.entity.RecordType;

import java.util.List;

public interface AdminRepository {

	List<GroupMenu> findAllGroupMenu();
	
	int deleteDataOfRecordTypeByIds(RecordType recordType, List<Long> ids);

	List<RoleMappingMenu> listMenuByUsername(String username);

}
