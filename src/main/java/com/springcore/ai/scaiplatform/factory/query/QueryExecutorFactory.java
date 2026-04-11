package com.springcore.ai.scaiplatform.factory.query;

import com.springcore.ai.scaiplatform.domain.type.QueryType;
import com.springcore.ai.scaiplatform.entity.RecordType;
import com.springcore.ai.scaiplatform.properties.ApplicationProperties;
import com.springcore.ai.scaiplatform.service.api.DynamicClassService;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.util.MultiValueMap;

@AllArgsConstructor
public final class QueryExecutorFactory {

    @NonNull
    private final ApplicationProperties applicationProperties;

    @NonNull
    private final DynamicClassService dynamicClassService;

    @NonNull
    private final EntityManager em;

    @NonNull
    private final RecordType recordType;

    @NonNull
    private final MultiValueMap<String, String> param;

    public <T> QueryExecutor<T> getQueryExecutor() {
        if (QueryType.SQL.equals(recordType.getCustomQueryType())) {
            return new SQLQueryExecutor<T>(applicationProperties, dynamicClassService, em, recordType, param);
        } else {
            return new HQLQueryExecution<T>(dynamicClassService, em, recordType, param);
        }
    }
}
