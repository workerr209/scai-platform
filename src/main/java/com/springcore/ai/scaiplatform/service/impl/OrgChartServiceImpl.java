package com.springcore.ai.scaiplatform.service.impl;

import com.springcore.ai.scaiplatform.dto.OrgChartNodeDTO;
import com.springcore.ai.scaiplatform.entity.Employee;
import com.springcore.ai.scaiplatform.entity.Trafts;
import com.springcore.ai.scaiplatform.repository.api.EmployeeHierarchyRepository;
import com.springcore.ai.scaiplatform.repository.api.EmployeeRepository;
import com.springcore.ai.scaiplatform.repository.api.TraftsRepository;
import com.springcore.ai.scaiplatform.service.api.OrgChartService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrgChartServiceImpl implements OrgChartService {

    private final TraftsRepository traftsRepository;
    private final EmployeeHierarchyRepository hierarchyRepository;
    private final EmployeeRepository employeeRepository;

    @Override
    public List<OrgChartNodeDTO> getOrgChartTree() {
        List<Trafts> allCurrent = traftsRepository.findAllByIscurrentTrue();

        Map<Long, OrgChartNodeDTO> nodeMap = allCurrent.stream().collect(Collectors.toMap(
                traft -> traft.getEmployee().getId(),
                this::convertToDTO
        ));

        List<OrgChartNodeDTO> rootNodes = new ArrayList<>();

        nodeMap.values().forEach(node -> {
            OrgChartNodeDTO parent = (node.getManagerId() != null) ? nodeMap.get(node.getManagerId()) : null;

            if (parent == null) {
                rootNodes.add(node);
            } else {
                parent.getChildren().add(node);
            }
        });

        return rootNodes;
    }

    @Override
    public List<Long> getPathToRoot(Long employeeId) {
        return hierarchyRepository.getAncestorIds(employeeId);
    }

    @Override
    public List<OrgChartNodeDTO> getRoots() {
        return traftsRepository.findAllByManagerIsNullAndIscurrentTrue()
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrgChartNodeDTO> getChildren(Long managerId) {
        return traftsRepository.findAllByManagerIdAndIscurrentTrue(managerId)
                .stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<OrgChartNodeDTO> getUnassignedEmployees() {
        List<Employee> unassigned = employeeRepository.findUnassignedEmployees();
        Set<Long> emIds = unassigned.stream()
                .map(Employee::getId)
                .collect(Collectors.toSet());

        List<Trafts> activeTrafts = traftsRepository.findLatestTraftsByEmployeeIds(emIds);
        Map<Long, Trafts> traftMap = activeTrafts.stream()
                .collect(Collectors.toMap(t -> t.getEmployee().getId(), t -> t));

        return unassigned.stream().map(emp -> {
            Trafts currentTraft = traftMap.get(emp.getId());

            String posName = (currentTraft != null && currentTraft.getPosition() != null)
                    ? currentTraft.getPosition().getName()
                    : "Waiting for positioning.";

            return OrgChartNodeDTO.builder()
                    .id(emp.getId())
                    .code(emp.getCode())
                    .name(emp.getName())
                    .positionName(posName)
                    .build();
        }).collect(Collectors.toList());
    }

    private OrgChartNodeDTO convertToDTO(Trafts traft) {
        OrgChartNodeDTO dto = OrgChartNodeDTO.builder()
                .id(traft.getEmployee().getId())
                .code(traft.getEmployee().getCode())
                .name(traft.getEmployee().getName())
                .positionName(traft.getPosition() != null ? traft.getPosition().getName() : "Waiting for positioning.")
                .build();

        List<Trafts> subordinates = traftsRepository.findAllByManagerIdAndIscurrentTrue(traft.getEmployee().getId());
        if (subordinates != null && !subordinates.isEmpty()) {
            dto.setChildren(subordinates.stream()
                    .map(this::convertToDTO)
                    .collect(Collectors.toList()));
        } else {
            dto.setChildren(new ArrayList<>());
        }

        return dto;
    }
}
