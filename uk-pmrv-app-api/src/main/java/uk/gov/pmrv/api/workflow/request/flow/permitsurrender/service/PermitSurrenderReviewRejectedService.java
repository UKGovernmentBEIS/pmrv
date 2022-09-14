package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.mapper.PermitSurrenderMapper;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.notification.PermitSurrenderOfficialNoticeService;

@Service
@RequiredArgsConstructor
public class PermitSurrenderReviewRejectedService {
    
    private final RequestService requestService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final PermitSurrenderOfficialNoticeService permitSurrenderOfficialNoticeService;

    private static final PermitSurrenderMapper permitSurrenderMapper = Mappers.getMapper(PermitSurrenderMapper.class);
    
    public void executeRejectedPostActions(String requestId) {
        Request request = requestService.findRequestById(requestId);
        PermitSurrenderRequestPayload requestPayload = (PermitSurrenderRequestPayload) request.getPayload();
        
        PermitSurrenderApplicationRejectedRequestActionPayload rejectedRequestActionPayload = 
                permitSurrenderMapper.toPermitSurrenderApplicationRejectedRequestActionPayload(requestPayload);

        DecisionNotification reviewDecisionNotification = requestPayload.getReviewDecisionNotification();

        rejectedRequestActionPayload.setUsersInfo(requestActionUserInfoResolver
            .getUsersInfo(reviewDecisionNotification.getOperators(), reviewDecisionNotification.getSignatory(), request)
        );
        
        // add action
        requestService.addActionToRequest(request,
                rejectedRequestActionPayload,
                RequestActionType.PERMIT_SURRENDER_APPLICATION_REJECTED,
                requestPayload.getRegulatorReviewer());
        
        //send official notice
        permitSurrenderOfficialNoticeService.sendReviewDeterminationOfficialNotice(request);
    }

}
