package com.springcore.ai.scaiplatform.core.service.api;

import com.springcore.ai.scaiplatform.core.dto.LookupItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LookupService {
    List<LookupItem> getDynamicLookup(String clazzLookup);

    Object getValueByReflection(Object object, String fldName);
}


