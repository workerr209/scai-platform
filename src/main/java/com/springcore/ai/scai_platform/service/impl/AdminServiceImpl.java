package com.springcore.ai.scai_platform.service.impl;

import com.fasterxml.jackson.databind.json.JsonMapper;
import com.springcore.ai.scai_platform.domain.mapping.RoleMappingMenu;
import com.springcore.ai.scai_platform.dto.FormMasterDeleteDTO;
import com.springcore.ai.scai_platform.dto.ManuByUserDTO;
import com.springcore.ai.scai_platform.entity.GroupMenu;
import com.springcore.ai.scai_platform.entity.RecordType;
import com.springcore.ai.scai_platform.factory.query.QueryExecutor;
import com.springcore.ai.scai_platform.factory.query.QueryExecutorFactory;
import com.springcore.ai.scai_platform.repository.api.AdminRepository;
import com.springcore.ai.scai_platform.service.api.AdminService;
import com.springcore.ai.scai_platform.service.api.RecordTypeService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
public class AdminServiceImpl implements AdminService {

    @PersistenceContext
    EntityManager em;

    private final AdminRepository adminRepository;
    private final RecordTypeService recordTypeService;
    private final JsonMapper jsonMapper;

    public AdminServiceImpl(AdminRepository adminRepository, RecordTypeService recordTypeService, JsonMapper jsonMapper) {
        this.adminRepository = adminRepository;
        this.recordTypeService = recordTypeService;
        this.jsonMapper = jsonMapper;
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
    public <T> List<T> findDataOfRecordTypeByCriteria(String recordTypeName, MultiValueMap<String, String> param) {

        Optional<RecordType> recordTypeOpt = this.recordTypeService.getRecordType(recordTypeName);
        if (recordTypeOpt.isPresent()) {
            log.debug("Query recordType = {}", recordTypeOpt.get().getName());
            final QueryExecutorFactory queryExecutorFactory = new QueryExecutorFactory(em, recordTypeOpt.get(), param);
            final QueryExecutor<T> queryExecutor = queryExecutorFactory.getQueryExecutor();
            return queryExecutor.execute();
        } else {
            throw new IllegalArgumentException();
        }

    }

    @Override
    @Transactional
    public int deleteDataOfRecordTypeByIds(String recordTypeName, FormMasterDeleteDTO param) {
        Optional<RecordType> recordType = this.recordTypeService.getRecordType(recordTypeName);
        return recordType.map(acRecordtype -> this.adminRepository.deleteDataOfRecordTypeByIds(acRecordtype, param.getIds())).orElse(0);

    }

    @Override
    @Transactional
    public <T> T saveDataOfRecordType(Class<T> clazz, Map<String, Object> param) {

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
