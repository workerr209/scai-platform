package com.springcore.ai.scaiplatform.service.api;

import com.springcore.ai.scaiplatform.dto.LookupItem;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface LookupService {
    List<LookupItem> getDynamicLookup(String clazzLookup);

    Object getValueByReflection(Object object, String fldName);
}


