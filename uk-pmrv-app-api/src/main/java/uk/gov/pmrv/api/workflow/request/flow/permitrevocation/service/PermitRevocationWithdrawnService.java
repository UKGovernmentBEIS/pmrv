package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.notification.PermitRevocationOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class PermitRevocationWithdrawnService {

    private final RequestService requestService;
    private final PermitRevocationOfficialNoticeService permitRevocationOfficialNoticeService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;

    public void withdraw(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) request.getPayload();

        // get users' information
        final DecisionNotification withdrawDecisionNotification = requestPayload.getWithdrawDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
            .getUsersInfo(withdrawDecisionNotification.getOperators(), withdrawDecisionNotification.getSignatory(), request);
        
        // generate official notice
        FileInfoDTO officialNotice = permitRevocationOfficialNoticeService.generateRevocationWithdrawnOfficialNotice(request.getId());
        
        final PermitRevocationApplicationWithdrawnRequestActionPayload payload =
            PermitRevocationApplicationWithdrawnRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_REVOCATION_APPLICATION_WITHDRAWN_PAYLOAD)
                .reason(requestPayload.getWithdrawReason())
                .withdrawFiles(requestPayload.getWithdrawFiles())
                .revocationAttachments(requestPayload.getRevocationAttachments())
                .decisionNotification(withdrawDecisionNotification)
                .usersInfo(usersInfo)
                .withdrawnOfficialNotice(officialNotice)
                .build();

        requestService.addActionToRequest(request,
            payload,
            RequestActionType.PERMIT_REVOCATION_APPLICATION_WITHDRAWN,
            requestPayload.getRegulatorAssignee());
        
        // send official notice
        permitRevocationOfficialNoticeService.sendOfficialNotice(request, officialNotice, withdrawDecisionNotification);
    }
}
