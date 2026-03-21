package com.springcore.ai.scai_platform.factory.query;

import com.springcore.ai.scai_platform.domain.type.QueryType;
import com.springcore.ai.scai_platform.entity.RecordType;
import com.springcore.ai.scai_platform.properties.ApplicationProperties;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.util.MultiValueMap;

@AllArgsConstructor
public final class QueryExecutorFactory {

    @NonNull
    private final ApplicationProperties applicationProperties;

    @NonNull
    private final EntityManager em;

    @NonNull
    private final RecordType recordType;

    @NonNull
    private final MultiValueMap<String, String> param;

    public <T> QueryExecutor<T> getQueryExecutor() {
        if (QueryType.SQL.equals(recordType.getCustomQueryType())) {
            return new SQLQueryExecutor<T>(applicationProperties, em, recordType, param);
        } else {
            return new HQLQueryExecution<T>(em, recordType, param);
        }
    }
}
