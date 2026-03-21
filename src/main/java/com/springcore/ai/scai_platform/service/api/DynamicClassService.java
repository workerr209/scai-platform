package com.springcore.ai.scai_platform.service.api;

import com.springcore.ai.scai_platform.entity.RecordType;

public interface DynamicClassService {
    Class<?> getMappingClass(RecordType recordType);
    void removeMappingClass(String recordTypeName);
}
