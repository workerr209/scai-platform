package com.springcore.ai.scai_platform.factory.query;

import com.springcore.ai.scai_platform.domain.type.QueryType;
import com.springcore.ai.scai_platform.entity.RecordType;
import jakarta.persistence.EntityManager;
import lombok.AllArgsConstructor;
import lombok.NonNull;
import org.springframework.util.MultiValueMap;

@AllArgsConstructor
public final class QueryExecutorFactory {

    @NonNull
    final private EntityManager em;

    @NonNull
    final private RecordType recordType;

    @NonNull
    final private MultiValueMap<String, String> param;

    public <T> QueryExecutor<T> getQueryExecutor() {
        if (QueryType.SQL.equals(recordType.getCustomQueryType())) {
            return new SQLQueryExecutor<T>(em, recordType, param);
        } else {
            return new HQLQueryExecution<T>(em, recordType, param);
        }
    }
}
