package uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.dto.RequestTaskAssignmentDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

import java.util.List;
import java.util.Optional;

/**
 * Service responsible for performing assignments on {@link RequestTask} objects.
 */
@Service
@RequiredArgsConstructor
public class UserRequestTaskAssignmentService {

    private final RequestTaskService requestTaskService;
    private final RequestTaskAssignmentValidationService requestTaskAssignmentValidationService;
    private final AuthorizationRulesQueryService authorizationRulesQueryService;
    private final List<UserRoleRequestTaskAssignmentService> userRoleRequestTaskAssignmentServices;

    /**
     * Assigns the {@link RequestTaskAssignmentDTO#getTaskId()} to the provided {@link RequestTaskAssignmentDTO#getUserId()}.
     * @param requestTaskAssignmentDTO the {@link RequestTaskAssignmentDTO}
     */
    @Transactional
    public void assignTask(RequestTaskAssignmentDTO requestTaskAssignmentDTO) {
        RequestTask requestTask = requestTaskService.findTaskById(requestTaskAssignmentDTO.getTaskId());

        requestTaskAssignmentValidationService.validateTaskAssignmentCapability(requestTask);

        getUserService(requestTask)
            .ifPresent(service -> service.assignTask(requestTask, requestTaskAssignmentDTO.getUserId()));
    }

    private Optional<UserRoleRequestTaskAssignmentService> getUserService(RequestTask requestTask) {
        RoleType requestTaskRoleType = authorizationRulesQueryService
            .findRoleTypeByResourceTypeAndSubType(ResourceType.REQUEST_TASK, requestTask.getType().name())
            .orElse(null);

        return userRoleRequestTaskAssignmentServices.stream()
            .filter(service -> service.getRoleType().equals(requestTaskRoleType))
            .findAny();
    }
}
