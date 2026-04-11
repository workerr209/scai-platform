package com.springcore.ai.scaiplatform.factory.query;

import java.util.List;

public interface QueryExecutor<T> {

    List<T> execute();
}
