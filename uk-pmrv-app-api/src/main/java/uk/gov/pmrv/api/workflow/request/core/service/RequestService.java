package uk.gov.pmrv.api.workflow.request.core.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import uk.gov.pmrv.api.authorization.rules.services.resource.AccountRequestAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.validation.EnabledWorkflowValidator;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidator;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Validated
@Service
@RequiredArgsConstructor
public class RequestService {

    private static final Set<String> EXCLUDED_FROM_AVAILABLE_WORKFLOWS =
        Set.of(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name());

    private final RequestRepository requestRepository;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final AccountRequestAuthorizationResourceService accountRequestAuthorizationResourceService;
    private final List<RequestCreateValidator> requestCreateValidators;
    private final EnabledWorkflowValidator enabledWorkflowValidator;

    @Transactional
    public Request saveRequest(Request request) {
        return requestRepository.save(request);
    }

    /**
     * Returns request by request id.
     *
     * @param id Request id
     * @return {@link Request}
     */
    public Request findRequestById(String id) {
        return requestRepository.findById(id)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }


    @Transactional
    public void addActionToRequest(Request request, @Valid RequestActionPayload payload,
                                   RequestActionType actionType, String submittedBy) {

        final String fullName = submittedBy != null ? requestActionUserInfoResolver.getUserFullName(submittedBy) : null;

        request.addRequestAction(
                RequestAction.builder()
                        .payload(payload)
                        .type(actionType)
                        .submitterId(submittedBy)
                        .submitter(fullName)
                        .build());
    }

    @Transactional
    public void updateRequestStatus(String requestId, RequestStatus status) {
        Request request = findRequestById(requestId);

        request.setStatus(status);
        requestRepository.save(request);
    }

    @Transactional
    public void terminateRequest(String requestId, String processInstanceId) {
        Request request = findRequestById(requestId);

        if(processInstanceId.equals(request.getProcessInstanceId())){
            if (RequestType.SYSTEM_MESSAGE_NOTIFICATION.equals(request.getType())) {
                requestRepository.delete(request);
            } else {
                closeRequest(request);
            }
        }
    }

    @Transactional
    public Map<RequestCreateActionType, RequestCreateValidationResult> getAvailableWorkflows(final Long accountId,
                                                                                             final PmrvUser pmrvUser,
                                                                                             RequestCategory category) {
        final Set<RequestCreateActionType> requestCreateActionTypes = accountRequestAuthorizationResourceService
                .findRequestCreateActionsByAccountId(pmrvUser, accountId).stream()
                .filter(type -> !EXCLUDED_FROM_AVAILABLE_WORKFLOWS.contains(type))
                .filter(type -> enabledWorkflowValidator.isWorkflowEnabled(RequestCreateActionType.valueOf(type).getType()))
                .map(RequestCreateActionType::valueOf)
                .filter(type -> type.getType().getCategory().equals(category))
                .collect(Collectors.toSet());

        return requestCreateActionTypes.stream().collect(Collectors.toMap(
                requestType -> requestType,
                requestType -> requestCreateValidators.stream()
                    .filter(validator -> validator.getType().equals(requestType))
                    .findFirst()
                    .map(validator -> validator.validateAction(accountId))
                    .orElse(RequestCreateValidationResult.builder().valid(true).build())
            )
        );
    }

    private void closeRequest(Request request) {
        if(RequestStatus.IN_PROGRESS.equals(request.getStatus())){
            request.setStatus(RequestStatus.COMPLETED);
        }
        request.setPayload(null);
        request.setEndDate(LocalDateTime.now());

        requestRepository.save(request);
    }
}
