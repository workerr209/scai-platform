package com.springcore.ai.scai_platform.repository.api;


import com.springcore.ai.scai_platform.domain.mapping.RoleMappingMenu;
import com.springcore.ai.scai_platform.entity.GroupMenu;
import com.springcore.ai.scai_platform.entity.RecordType;

import java.util.List;

public interface AdminRepository {

	List<GroupMenu> findAllGroupMenu();
	
	int deleteDataOfRecordTypeByIds(RecordType recordType, List<Long> ids);

	List<RoleMappingMenu> listMenuByUsername(String username);

}
