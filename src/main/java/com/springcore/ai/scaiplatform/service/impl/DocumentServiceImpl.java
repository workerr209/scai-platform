package com.springcore.ai.scaiplatform.service.impl;

import com.springcore.ai.scaiplatform.domain.constant.DocumentStatus;
import com.springcore.ai.scaiplatform.dto.DocumentSearchReq;
import com.springcore.ai.scaiplatform.dto.NotificationDTO;
import com.springcore.ai.scaiplatform.dto.SupervisorInfo;
import com.springcore.ai.scaiplatform.entity.Document;
import com.springcore.ai.scaiplatform.entity.Employee;
import com.springcore.ai.scaiplatform.entity.FlowDoc;
import com.springcore.ai.scaiplatform.entity.FlowDocStep;
import com.springcore.ai.scaiplatform.entity.Notification;
import com.springcore.ai.scaiplatform.repository.api.DocumentRepository;
import com.springcore.ai.scaiplatform.repository.api.DocumentRepositoryCustom;
import com.springcore.ai.scaiplatform.repository.api.EmployeeHierarchyRepository;
import com.springcore.ai.scaiplatform.repository.api.EmployeeRepository;
import com.springcore.ai.scaiplatform.repository.api.FlowDocRepository;
import com.springcore.ai.scaiplatform.repository.api.NotificationRepository;
import com.springcore.ai.scaiplatform.repository.api.UserRepository;
import com.springcore.ai.scaiplatform.security.UserContext;
import com.springcore.ai.scaiplatform.service.api.DocumentService;
import com.springcore.ai.scaiplatform.service.api.NotificationService;
import jakarta.transaction.Transactional;
import jakarta.validation.ValidationException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
public class DocumentServiceImpl implements DocumentService {
    private final DocumentRepository documentRepository;
    private final DocumentRepositoryCustom documentRepositoryCustom;
    private final EmployeeRepository employeeRepository;
    private final FlowDocRepository flowDocRepository;
    private final EmployeeHierarchyRepository hierarchyRepository;
    private final NotificationService notificationService;
    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    @Autowired
    public DocumentServiceImpl(DocumentRepository documentRepository
            , DocumentRepositoryCustom documentRepositoryCustom
            , EmployeeRepository employeeRepository
            , FlowDocRepository flowDocRepository
            , EmployeeHierarchyRepository hierarchyRepository
            , NotificationService notificationService
            , NotificationRepository notificationRepository
            , UserRepository userRepository
    ) {
        this.documentRepository = documentRepository;
        this.documentRepositoryCustom = documentRepositoryCustom;
        this.employeeRepository = employeeRepository;
        this.flowDocRepository = flowDocRepository;
        this.hierarchyRepository = hierarchyRepository;
        this.notificationService = notificationService;
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
    }


    @Override
    public List<Document> search(DocumentSearchReq criteria) {
        return documentRepositoryCustom.searchByCriteria(criteria);
    }

    @Override
    public Document searchById(Long id) {
        Document doc = documentRepository.findById(id).orElseThrow(() -> new RuntimeException("Document Not Found"));
        Long docId = doc.getId();
        flowDocRepository.findByDocId(docId).ifPresent(flowDoc -> {
            List<FlowDocStep> steps = flowDoc.getSteps();
            Set<Long> emIds = steps.stream().map(FlowDocStep::getEmman).filter(Objects::nonNull).collect(Collectors.toSet());
            Map<Long, Employee> employeeMap = employeeRepository.findAllById(emIds).stream()
                    .collect(Collectors.toMap(Employee::getId, e -> e));
            steps.forEach(step -> step.setEmmanInfo(employeeMap.get(step.getEmman())));
            doc.setFlowDoc(flowDoc);
        });
        return doc;
    }

    @Override
    @Transactional
    public Document save(Document doc) {
        Long docId = doc.getId();
        log.debug("action {}", (docId != null) ? "UPDATE" : "INSERT");
        log.debug("save {}", doc);

        generateDocumentNo(doc);
        doc = documentRepository.save(doc);

        log.debug("saved document id={}", doc.getId());
        return doc;
    }

    @Transactional
    public boolean deleteById(Long id) {
        if (!documentRepository.existsById(id)) {
            return false;
        }

        documentRepository.deleteById(id);
        if (flowDocRepository.existsByDocId(id)) {
            flowDocRepository.deleteByDocId(id);
        }

        if (notificationRepository.existsByParentId(id)) {
            notificationRepository.deleteByParentId(id);
        }

        return true;
    }

    @Override
    @Transactional
    public Document generateFlow(Document doc) {
        Employee employeeLogin = UserContext.getEmployee();
        assert employeeLogin != null;

        Date today = new Date();
        int maxStep = 3; // Todo Get Max Step From Config
        doc.setDocumentStatus(DocumentStatus.DRAFT);

        FlowDoc flow = new FlowDoc();
        flow.setDocNo(doc.getDocumentNo());
        flow.setDocType(doc.getDocumentType());
        flow.setCreatedDate(today);
        flow.setRequestedDate(today);
        flow.setActiveStep(0);
        flow.setLastStep(BigDecimal.valueOf(maxStep));
        flow.setInactive(BigDecimal.ZERO);

        Long emId = doc.getEmId();
        List<SupervisorInfo> supervisors = hierarchyRepository.findSupervisors(emId);
        Map<Integer, SupervisorInfo> supervisorMap = supervisors.stream().collect(Collectors.toMap(SupervisorInfo::getLevel, sup -> sup));

        List<FlowDocStep> steps = new ArrayList<>();
        FlowDocStep step;

        // Requester
        {
            // Case User LogIn Is Requester Step 0 Have 1
            // Case Requester Not User LogIn Step 0 Have 2
            step = new FlowDocStep();
            step.setFlowDoc(flow);
            step.setStepno(0);
            step.setEmmanInfo(employeeLogin);
            step.setEmman(employeeLogin.getId());
            step.setIsActive(1);
            step.setIsend(BigDecimal.ZERO);
            step.setMailstat(BigDecimal.ZERO);
            step.setReqCancel(0);
            step.setActionType("REQUEST");
            step.setActionDate(today);
            steps.add(step);

            if (employeeLogin.getId().compareTo(emId) != 0) {
                FlowDocStep owner = new FlowDocStep();
                owner.setFlowDoc(flow);
                owner.setStepno(0);
                employeeRepository.findById(emId).ifPresent(owner::setEmmanInfo);
                owner.setEmman(emId);
                owner.setIsActive(0);
                owner.setIsend(BigDecimal.ZERO);
                owner.setMailstat(BigDecimal.ZERO);
                owner.setReqCancel(0);
                owner.setActionType("OWNER");
                steps.add(owner);
            }
        }

        for (int i = 1; i <= 3; i++) {
            step = new FlowDocStep();
            step.setFlowDoc(flow);
            step.setStepno(i);

            SupervisorInfo supInfo = supervisorMap.get(i);
            Long approverId;
            if (supInfo != null) {
                approverId = supInfo.getEmId();
                step.setEmmanInfo(Employee.builder()
                        .id(approverId)
                        .code(supInfo.getEmCode())
                        .name(supInfo.getEmName())
                        .build());
            } else if (!supervisors.isEmpty()) {
                SupervisorInfo findSup = supervisors.get(supervisors.size() - 1);
                approverId = findSup.getEmId();
                step.setEmmanInfo(Employee.builder()
                        .id(approverId)
                        .code(findSup.getEmCode())
                        .name(findSup.getEmName())
                        .build());
            } else {
                approverId = emId;
            }

            step.setActionType("WAITING");
            step.setEmman(approverId);
            step.setIsActive(0);
            step.setIsend(i == 3 ? BigDecimal.ONE : BigDecimal.ZERO);
            step.setMailstat(BigDecimal.ZERO);
            step.setReqCancel(0);
            steps.add(step);
        }

        flow.setSteps(steps);
        doc.setFlowDoc(flow);
        doc.setDocumentStatus(DocumentStatus.DRAFT);
        return doc;
    }

    @Override
    @Transactional
    public Document submitFlow(Document doc) {
        doc.setDocumentStatus(DocumentStatus.WAITING);
        FlowDoc flowDoc = doc.getFlowDoc();
        if (flowDoc.getId() != null) {
            throw new ValidationException("Flow document already exists");
        }

        Document documentSaved = save(doc);
        Long docId = documentSaved.getId();
        String documentNo = documentSaved.getDocumentNo();

        flowDoc.setDocId(docId);
        flowDoc.setDocNo(documentNo);
        flowDoc.setCreatedDate(new Date());
        if (flowDoc.getSteps() != null) {
            FlowDocStep currentStep = flowDoc.getSteps().stream()
                    .filter(s -> s.getStepno() == 0)
                    .findFirst()
                    .orElse(null);

            if (currentStep != null) {
                currentStep.setIsActive(0);

                // 2. หา Next Step (Step ที่ 1)
                int nextStepNo = currentStep.getStepno() + 1;
                flowDoc.getSteps().stream()
                        .filter(s -> s.getStepno() == nextStepNo)
                        .findFirst()
                        .ifPresent(nextStep -> {
                            nextStep.setIsActive(1);
                            flowDoc.setActiveStep(nextStep.getStepno());

                            Long userId = userRepository.findUserIdByEmployeeId(nextStep.getEmman());
                            if (userId != null) {
                                // Send Notification via RabbitMQ into the SSE stream
                                NotificationDTO payload = NotificationDTO.builder()
                                        .title("New Task Awaiting Approval")
                                        .message("Document No. " + documentNo + " has been sent to you.")
                                        .type("INFO") // Defines UI color (e.g., INFO=Blue, SUCCESS=Green)
                                        .parentId(docId)
                                        .url("document/flow")
                                        .timestamp(new Date()) // Automatically converted to Long Timestamp per your JacksonConfig
                                        .build();

                                notificationService.sendToUser(userId, payload);
                                Notification entity = Notification.builder()
                                        .userId(userId)
                                        .title(payload.getTitle())
                                        .message(payload.getMessage())
                                        .type(payload.getType())
                                        .parentId(payload.getParentId())
                                        .url(payload.getUrl())
                                        .read(false)
                                        .build();
                                notificationRepository.save(entity);
                            } else {
                                log.warn("NotificationService.sendToUser Failed. Could not find user with id {}", nextStep.getEmman());
                            }
                        });
            }

            flowDoc.getSteps().forEach(step -> step.setFlowDoc(flowDoc));
        }

        doc.setFlowDoc(flowDocRepository.save(flowDoc));
        return doc;
    }

    private void generateDocumentNo(Document doc) {
        if (StringUtils.isNotBlank(doc.getDocumentNo())) {
            return;
        }

        String documentType = doc.getDocumentType();
        String format = java.time.YearMonth.now().toString().replace("-", "");
        List<Document> latestList = documentRepository.findTopByTypeAndMonthOrderByIdDesc(documentType, format);

        int running = 1;
        if (!latestList.isEmpty()) {
            String lastNo = latestList.get(0).getDocumentNo();
            String lastRunning = lastNo.substring(lastNo.lastIndexOf("-") + 1);
            running = Integer.parseInt(lastRunning) + 1;
        }

        String documentNo = String.format("%s-%s-%04d", documentType, format, running);
        doc.setDocumentNo(documentNo);
    }

}
