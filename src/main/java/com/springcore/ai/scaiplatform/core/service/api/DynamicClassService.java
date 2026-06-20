package com.springcore.ai.scaiplatform.core.service.api;


import com.springcore.ai.scaiplatform.core.entity.RecordType;

public interface DynamicClassService {
    Class<?> getMappingClass(RecordType recordType);
    void removeMappingClass(String recordTypeName);
}
