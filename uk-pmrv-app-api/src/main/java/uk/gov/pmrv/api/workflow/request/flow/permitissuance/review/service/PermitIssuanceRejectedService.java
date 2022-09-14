package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service;

import java.util.Map;

import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.service.AccountStatusService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.mapper.PermitReviewMapper;

@Service
@RequiredArgsConstructor
public class PermitIssuanceRejectedService {

    private static final PermitReviewMapper PERMIT_REVIEW_MAPPER = Mappers.getMapper(PermitReviewMapper.class);

    private final RequestService requestService;
    private final AccountStatusService accountStatusService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;

    public void reject(final String requestId) {

        final Request request = requestService.findRequestById(requestId);

        final PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();
        final Long accountId = request.getAccountId();

        accountStatusService.handlePermitRejected(accountId);

        final PermitIssuanceApplicationRejectedRequestActionPayload actionPayload =
            PERMIT_REVIEW_MAPPER.toPermitIssuanceApplicationRejectedRequestActionPayload(requestPayload);

        // get users' information
        final DecisionNotification notification =
            ((PermitIssuanceRequestPayload) request.getPayload()).getPermitDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
            .getUsersInfo(notification.getOperators(), notification.getSignatory(), request);
        actionPayload.setUsersInfo(usersInfo);

        final String reviewer = requestPayload.getRegulatorReviewer();

        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.PERMIT_ISSUANCE_APPLICATION_REJECTED,
            reviewer);
    }

}
