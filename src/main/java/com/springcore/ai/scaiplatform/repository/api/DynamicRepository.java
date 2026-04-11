package com.springcore.ai.scaiplatform.repository.api;

import java.util.List;

public interface DynamicRepository {
    List<?> fetchData(String clazzLookup);
}