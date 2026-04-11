package com.springcore.ai.scaiplatform.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class Utils {
    public static Class<?> declareClassName(String className) {
        if(StringUtils.isEmpty(className)) {
            return null;
        }
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            log.warn("Class not found: {}", className);
        }
        return null;
    }
}
