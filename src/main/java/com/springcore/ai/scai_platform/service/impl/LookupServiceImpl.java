package com.springcore.ai.scai_platform.service.impl;

import com.springcore.ai.scai_platform.dto.LookupItem;
import com.springcore.ai.scai_platform.entity.RecordType;
import com.springcore.ai.scai_platform.repository.api.DynamicRepository;
import com.springcore.ai.scai_platform.service.api.AdminService;
import com.springcore.ai.scai_platform.service.api.LookupService;
import com.springcore.ai.scai_platform.service.api.RecordTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class LookupServiceImpl implements LookupService {

    private final AdminService adminService;
    private final DynamicRepository dynamicRepository;
    private final RecordTypeService recordTypeService;

    @Autowired
    public LookupServiceImpl(AdminService adminService, DynamicRepository dynamicRepository, RecordTypeService recordTypeService) {
        this.adminService = adminService;
        this.dynamicRepository = dynamicRepository;
        this.recordTypeService = recordTypeService;
    }

    private RecordType getRecordType(String recordTypeName) {
        try {
            return recordTypeService.getRecordType(recordTypeName);
        } catch (Exception ignored) { }
        return null;
    }

    @Override
    public List<LookupItem> getDynamicLookup(String clazzName) {
        try {
            RecordType recordType = getRecordType(clazzName);
            if (recordType != null) {
                return adminService.findDataOfRecordTypeByCriteria(clazzName, new LinkedMultiValueMap<>()).stream()
                        .map(r -> LookupItem.builder()
                                .id((Long) getValueByReflection(r, "id"))
                                .code((String) getValueByReflection(r, "code"))
                                .name((String) getValueByReflection(r, "name"))
                                .build())
                        .toList();
            }
        } catch (Exception e) {
            log.warn("dynamicRepository.getDynamicLookup findDataOfRecordTypeByCriteria Failed.", e);
        }

        try {
            return dynamicRepository.fetchData(clazzName)
                    .stream()
                    .map(r -> LookupItem.builder()
                            .id((Long) getValueByReflection(r, "id"))
                            .code((String) getValueByReflection(r, "code"))
                            .name((String) getValueByReflection(r, "name"))
                            .build())
                    .toList();
        } catch (Exception e) {
            log.warn("dynamicRepository.getDynamicLookup dynamicRepository.fetchData Failed.", e);
        }

        return Collections.emptyList();
    }

    @Override
    public Object getValueByReflection(Object object, String fldName) {
        try {
            java.lang.reflect.Field field = object.getClass().getDeclaredField(fldName);
            field.setAccessible(true);
            return field.get(object);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }
}
