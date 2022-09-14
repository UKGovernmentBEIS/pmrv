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
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.notification.PermitRevocationOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class PermitRevocationSubmittedService {

    private final RequestService requestService;
    private final PermitRevocationOfficialNoticeService permitRevocationOfficialNoticeService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;

    public void submit(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) request.getPayload();

        // get users' information
        final DecisionNotification decisionNotification = requestPayload.getDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo =
            requestActionUserInfoResolver.getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request);
        
        // generate official notice
        FileInfoDTO officialNotice = permitRevocationOfficialNoticeService.generateRevocationOfficialNotice(request.getId());
        
        // set official notice to request payload
        requestPayload.setOfficialNotice(officialNotice);

        // create request action
        final PermitRevocationApplicationSubmittedRequestActionPayload actionPayload =
            PermitRevocationApplicationSubmittedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_REVOCATION_APPLICATION_SUBMITTED_PAYLOAD)
                .permitRevocation(requestPayload.getPermitRevocation())
                .decisionNotification(decisionNotification)
                .usersInfo(usersInfo)
                .officialNotice(officialNotice)
                .build();
        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.PERMIT_REVOCATION_APPLICATION_SUBMITTED,
            request.getPayload().getRegulatorAssignee());
        
        // send official notice
        permitRevocationOfficialNoticeService.sendOfficialNotice(request, officialNotice, decisionNotification);
    }
}
