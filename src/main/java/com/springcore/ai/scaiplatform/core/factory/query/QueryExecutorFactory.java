package com.springcore.ai.scaiplatform.core.factory.query;

import com.springcore.ai.scaiplatform.core.domain.type.QueryType;
import com.springcore.ai.scaiplatform.core.entity.RecordType;
import com.springcore.ai.scaiplatform.core.properties.ApplicationProperties;
import com.springcore.ai.scaiplatform.core.service.api.DynamicClassService;
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
