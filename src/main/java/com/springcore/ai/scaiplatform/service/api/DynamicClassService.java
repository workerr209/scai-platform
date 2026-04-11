package com.springcore.ai.scaiplatform.service.api;


import com.springcore.ai.scaiplatform.entity.RecordType;

public interface DynamicClassService {
    Class<?> getMappingClass(RecordType recordType);
    void removeMappingClass(String recordTypeName);
}
