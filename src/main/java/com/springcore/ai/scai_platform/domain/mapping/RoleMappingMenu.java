package com.springcore.ai.scai_platform.domain.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public @Data class RoleMappingMenu implements GenericMapping {
    private String recordTypeName;
    private String recordTypeLabel;
    private String username;
    private String entityType;
}
