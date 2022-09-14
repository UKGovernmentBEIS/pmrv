package uk.gov.pmrv.api.workflow.request.application.taskview;

import java.util.Collections;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.authorization.rules.services.resource.RequestTaskAuthorizationResourceService;
import uk.gov.pmrv.api.authorization.rules.services.resource.ResourceCriteria;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.application.UserService;
import uk.gov.pmrv.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

@RequiredArgsConstructor
@Service
public class RequestTaskViewService {

    private final RequestTaskService requestTaskService;
    private final UserService userService;
    private final RequestTaskAuthorizationResourceService requestTaskAuthorizationResourceService;

    private static final RequestTaskMapper requestTaskMapper = Mappers.getMapper(RequestTaskMapper.class);
    private static final RequestInfoMapper requestInfoMapper = Mappers.getMapper(RequestInfoMapper.class);

    @Transactional
    public RequestTaskItemDTO getTaskItemInfo(Long taskId, PmrvUser currentUser) {
        RequestTask requestTask = requestTaskService.findTaskById(taskId);

        return RequestTaskItemDTO.builder()
            .requestTask(buildTaskDTO(requestTask))
            .allowedRequestTaskActions(buildAllowedRequestTaskActions(currentUser, requestTask))
            .userAssignCapable(isUserCapableToAssignRequestTask(currentUser, requestTask))
            .requestInfo(requestInfoMapper.toRequestInfoDTO(requestTask.getRequest()))
            .build();
    }
    
    private RequestTaskDTO buildTaskDTO(RequestTask requestTask) {
        ApplicationUserDTO assigneeUser = !ObjectUtils.isEmpty(requestTask.getAssignee())
            ? userService.getUserById(requestTask.getAssignee())
            : null;

        return requestTaskMapper.toTaskDTO(requestTask, assigneeUser);
    }
    
    private List<RequestTaskActionType> buildAllowedRequestTaskActions(PmrvUser currentUser, RequestTask requestTask) {
        if (isUserCapableToExecuteRequestTask(currentUser, requestTask)) {
            return requestTask.getType().getAllowedRequestTaskActionTypes();
        } else {
            return Collections.emptyList();
        }
    }

    private boolean isUserCapableToExecuteRequestTask(PmrvUser user, RequestTask requestTask) {
        return user.getUserId().equals(requestTask.getAssignee()) && hasUserExecuteScopeOnRequestTask(user, requestTask);
    }
    
    private boolean isUserCapableToAssignRequestTask(PmrvUser user, RequestTask requestTask) {
        return requestTaskAuthorizationResourceService.hasUserAssignScopeOnRequestTasks(user, buildResourceCriteria(requestTask));
    }

    private boolean hasUserExecuteScopeOnRequestTask(PmrvUser user, RequestTask requestTask) {
        return requestTaskAuthorizationResourceService
            .hasUserExecuteScopeOnRequestTaskType(user, requestTask.getType().name(), buildResourceCriteria(requestTask));
    }

    private ResourceCriteria buildResourceCriteria(RequestTask requestTask) {
        Request request = requestTask.getRequest();
        return ResourceCriteria.builder()
            .accountId(request.getAccountId())
            .competentAuthority(request.getCompetentAuthority())
            .verificationBodyId(request.getVerificationBodyId())
            .build();
    }
}
