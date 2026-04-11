package com.springcore.ai.scaiplatform.service.impl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.springcore.ai.scaiplatform.domain.mapping.RoleMappingMenu;
import com.springcore.ai.scaiplatform.dto.FormMasterDeleteDTO;
import com.springcore.ai.scaiplatform.dto.ManuByUserDTO;
import com.springcore.ai.scaiplatform.entity.GroupMenu;
import com.springcore.ai.scaiplatform.entity.RecordType;
import com.springcore.ai.scaiplatform.factory.query.QueryExecutor;
import com.springcore.ai.scaiplatform.factory.query.QueryExecutorFactory;
import com.springcore.ai.scaiplatform.properties.ApplicationProperties;
import com.springcore.ai.scaiplatform.repository.api.AdminRepository;
import com.springcore.ai.scaiplatform.service.api.AdminService;
import com.springcore.ai.scaiplatform.service.api.DynamicClassService;
import com.springcore.ai.scaiplatform.service.api.RecordTypeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @PersistenceContext
    EntityManager em;
    private final ApplicationProperties applicationProperties;
    private final JsonMapper jsonMapper;
    private final AdminRepository adminRepository;
    private final RecordTypeService recordTypeService;
    private final DynamicClassService dynamicClassService;


    public AdminServiceImpl(ApplicationProperties applicationProperties, JsonMapper jsonMapper, AdminRepository adminRepository, RecordTypeService recordTypeService, DynamicClassService dynamicClassService) {
        this.applicationProperties = applicationProperties;
        this.jsonMapper = jsonMapper;
        this.adminRepository = adminRepository;
        this.recordTypeService = recordTypeService;
        this.dynamicClassService = dynamicClassService;
    }

    @Override
    public List<GroupMenu> findAllAllowMenuByUsername(String username) {
        return adminRepository.findAllGroupMenu();
    }

    @Override
    public <T> T findDataOfRecordTypeById(Class<T> clazz, Long id) {
        return em.find(clazz, id);
    }

    @Override
    @Transactional(readOnly = true)
    public <T> List<T> findDataOfRecordTypeByCriteria(String recordTypeName, MultiValueMap<String, String> param) {
        Optional<RecordType> recordTypeOpt = this.recordTypeService.getRecordTypeOptional(recordTypeName);
        if (recordTypeOpt.isPresent()) {
            RecordType recordType = recordTypeOpt.get();
            log.debug("Query recordType = {}", recordTypeName);
            final QueryExecutorFactory queryExecutorFactory = new QueryExecutorFactory(applicationProperties, dynamicClassService, em, recordType, param);
            final QueryExecutor<T> queryExecutor = queryExecutorFactory.getQueryExecutor();
            List<T> resultList = queryExecutor.execute();
            if ("AC_RecordType".equals(recordTypeName)) {
                ListUtils.partition(resultList, 20) // RecordType.fieldList >> @BatchSize(size = 20)
                        .stream()
                        .map(item -> (RecordType) item.get(0))
                        .forEach(RecordType::getFieldsCnt);
            }
            return resultList;
        } else {
            throw new IllegalArgumentException();
        }
    }

    @Override
    @Transactional
    public int deleteDataOfRecordTypeByIds(String recordTypeName, FormMasterDeleteDTO param) {
        Optional<RecordType> recordType = this.recordTypeService.getRecordTypeOptional(recordTypeName);
        return recordType.map(acRecordtype -> this.adminRepository.deleteDataOfRecordTypeByIds(acRecordtype, param.getIds())).orElse(0);
    }

    @Override
    @Transactional
    public <T> T saveDataOfRecordType(Class<T> clazz, Map<String, Object> param) {
        Object id = param.get("id");
        if (id != null) {
            return updateDataOfRecordType(clazz, param);
        }

        final T entity = jsonMapper.convertValue(param, clazz);
        em.persist(entity);

        return entity;
    }

    @Override
    @Transactional
    public <T> T updateDataOfRecordType(Class<T> clazz, Map<String, Object> param) {
        T entity = jsonMapper.convertValue(param, clazz);
        entity = em.merge(entity);
        return entity;
    }

    @Override
    public List<ManuByUserDTO> listMenuByUsername(String username) {
        List<RoleMappingMenu> listMenu = adminRepository.listMenuByUsername(username);
        LinkedList<ManuByUserDTO> list = new LinkedList<>();
        ManuByUserDTO menu;
        for (RoleMappingMenu obj : listMenu) {
            menu = new ManuByUserDTO();
            menu.setEntityType(obj.getEntityType());
            menu.setRecordTypeLabel(obj.getRecordTypeLabel());
            menu.setRecordTypeName(obj.getRecordTypeName());
            menu.setUsername(username);

            if (StringUtils.equals("F", obj.getEntityType()) || StringUtils.equals("R", obj.getEntityType())) {
                menu.setPathForm("/formMaster/" + obj.getRecordTypeName());
            } else if (StringUtils.equals("U", obj.getEntityType())) {
                menu.setPathForm("/" + obj.getRecordTypeName());
            }
            list.add(menu);
        }
        return list;
    }
}
