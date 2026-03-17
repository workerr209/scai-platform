package com.springcore.ai.scai_platform.domain.type;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;

@NoArgsConstructor
@Data
@Slf4j
public class CustomProperty {

    public CustomProperty(String requestString) {
        if(StringUtils.isNotBlank(requestString)) {
            final JsonMapper jsonMapper = JsonMapper.builder()
                    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
                    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                    .build();

            try {
                TypeReference<HashMap<String,Object>> typeRef = new TypeReference<>() {};
                HashMap<String, Object> requestBean = jsonMapper.readValue(requestString, typeRef);
                this.roleValueMapping = (String) requestBean.get("roleValueMapping");
                this.whereFilter = (String) requestBean.get("whereFilter");
                this.whereUserName = (String) requestBean.get("whereUserName");
                this.tableWidth = (String) requestBean.get("tableWidth");
                this.tableDataAlign = (String) requestBean.get("tableDataAlign");
                this.propTagMap = (String) requestBean.get("propTagMap");
            } catch (JsonProcessingException e) {
                log.warn("Could not parse request object from JSON", e);
//                throw new RuntimeException(e);
            }

        }
    }

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String whereFilter;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String roleValueMapping;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String whereUserName;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tableWidth;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String tableDataAlign;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fieldCheckExportRedCell;
    
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String propTagMap;

}
