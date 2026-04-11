package com.springcore.ai.scaiplatform.service.api;

import com.springcore.ai.scaiplatform.dto.OrgChartNodeDTO;

import java.util.List;

public interface OrgChartService {
    List<OrgChartNodeDTO> getOrgChartTree();
    List<Long> getPathToRoot(Long employeeId);
    List<OrgChartNodeDTO> getRoots();
    List<OrgChartNodeDTO> getChildren(Long managerId);
    List<OrgChartNodeDTO> getUnassignedEmployees();
}
