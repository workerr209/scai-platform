package com.springcore.ai.scaiplatform.controller;

import com.springcore.ai.scaiplatform.dto.LookupItem;
import com.springcore.ai.scaiplatform.service.api.LookupService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/lookup")
@RequiredArgsConstructor
public class LookupController {
    private final LookupService lookupService;

    @GetMapping("fetchData/{clazzLookup}")
    @ResponseBody
    public ResponseEntity<List<LookupItem>> fetchData(@PathVariable String clazzLookup) {
        return ResponseEntity.ok(lookupService.getDynamicLookup(clazzLookup));
    }

}

