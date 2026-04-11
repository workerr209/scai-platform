package com.springcore.ai.scaiplatform.domain.mapping;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@NoArgsConstructor
@AllArgsConstructor
public @Data class GeneralMapping implements GenericMapping {

    private Integer id;
    private String code;
    private String name;
}
