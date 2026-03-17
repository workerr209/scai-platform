package com.springcore.ai.scai_platform.repository.impl;

import com.springcore.ai.scai_platform.domain.mapping.RoleMappingMenu;
import com.springcore.ai.scai_platform.entity.GroupMenu;
import com.springcore.ai.scai_platform.entity.RecordType;
import com.springcore.ai.scai_platform.repository.api.AdminRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
public class AdminRepositoryImpl implements AdminRepository {
	
	@PersistenceContext // or even @Autowired
    private EntityManager em;

	@Override
	public List<GroupMenu> findAllGroupMenu() {
		return null;
	}

	@Override
//	@Transactional /*This ANotation use for the DB table that not have field name "ID" , Patchara K.*/
	public int deleteDataOfRecordTypeByIds(RecordType recordType, List<Long> ids) {
		Class<?> forName;
		int executeUpdate = ids.size();
		try {
			forName = Class.forName(recordType.getClassName());
			ids.forEach(id -> em.remove(em.getReference(forName, id)));
		} catch (ClassNotFoundException ex) {
			log.error("deleteDataOfRecordTypeByIds", ex);
		}
		return executeUpdate;
	}
	
	@Override
	public List<RoleMappingMenu> listMenuByUsername(String username) {
		Map<String, Object> filterMap = new HashMap<>();
		filterMap.put("username", username);
        String sqlQuery = "select recordTypeName, recordTypeLabel, username, entityType  from ( "
                + "select f.name recordTypeName, f.label recordTypeLabel, a.username, f.entityType, f.menuOrder from as_user a  "
                + "join As_Authorize d on d.username_id = a.id "
                + "join As_Authorizedetail e on e.parent_id = d.id "
                + "join ac_recordtype f on f.id = e.recordtype_id "
                + "where a.username = :username and a.inactive = 0"
                + "union "
                + "select f.name recordTypeName, f.label recordTypeLabel, a.username, f.entityType, f.menuOrder from as_user a  "
                + "join as_groupuserDetail g on g.username = a.id "
                + "join As_Authorize d on d.groupuser_id = g.parent_id "
                + "join As_Authorizedetail e on e.parent_id = d.id "
                + "join ac_recordtype f on f.id = e.recordtype_id "
                + "where a.username = :username and a.inactive = 0) ax "
                + "order by menuOrder , entityType, recordTypeLabel ";
        Query nativeQuery = em.createNativeQuery(sqlQuery, RoleMappingMenu.class);
        filterMap.forEach(nativeQuery::setParameter);
		return nativeQuery.getResultList();
	}


}
