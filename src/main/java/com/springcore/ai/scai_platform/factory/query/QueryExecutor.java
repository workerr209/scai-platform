package com.springcore.ai.scai_platform.factory.query;

import java.util.List;

public interface QueryExecutor<T> {

    List<T> execute();
}
