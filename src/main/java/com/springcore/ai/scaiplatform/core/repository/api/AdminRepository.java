package com.springcore.ai.scaiplatform.core.repository.api;


import com.springcore.ai.scaiplatform.core.domain.mapping.RoleMappingMenu;
import com.springcore.ai.scaiplatform.core.entity.RecordType;

import java.util.List;

public interface AdminRepository {

	int deleteDataOfRecordTypeByIds(RecordType recordType, List<Long> ids);

	List<RoleMappingMenu> listMenuByUsername(String username);

}
