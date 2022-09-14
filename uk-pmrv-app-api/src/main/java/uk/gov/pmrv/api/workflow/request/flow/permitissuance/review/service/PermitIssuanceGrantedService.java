package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.service.AccountUpdateService;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.service.PermitService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.mapper.PermitMapper;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.mapper.PermitReviewMapper;

@Service
@RequiredArgsConstructor
public class PermitIssuanceGrantedService {

    private static final PermitReviewMapper PERMIT_REVIEW_MAPPER = Mappers.getMapper(PermitReviewMapper.class);
    private static final PermitMapper PERMIT_MAPPER = Mappers.getMapper(PermitMapper.class);

    private final AccountUpdateService accountUpdateService;
    private final RequestService requestService;
    private final PermitService permitService;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    public void grant(final String requestId) {

        final Request request = requestService.findRequestById(requestId);
        final PermitIssuanceRequestPayload requestPayload = (PermitIssuanceRequestPayload) request.getPayload();
        final Long accountId = request.getAccountId();

        final InstallationOperatorDetails installationOperatorDetails = installationOperatorDetailsQueryService.getInstallationOperatorDetails(
            request.getAccountId());

        final PermitIssuanceApplicationGrantedRequestActionPayload actionPayload =
            PERMIT_REVIEW_MAPPER.toPermitIssuanceApplicationGrantedRequestActionPayload(requestPayload);

        // get users' information
        final DecisionNotification notification =
            ((PermitIssuanceRequestPayload) request.getPayload()).getPermitDecisionNotification();
        final Map<String, RequestActionUserInfo> usersInfo =
            requestActionUserInfoResolver.getUsersInfo(notification.getOperators(), notification.getSignatory(), request);
        actionPayload.setUsersInfo(usersInfo);
        actionPayload.setInstallationOperatorDetails(installationOperatorDetails);
        
        final String reviewer = requestPayload.getRegulatorReviewer();

        requestService.addActionToRequest(request,
            actionPayload,
            RequestActionType.PERMIT_ISSUANCE_APPLICATION_GRANTED,
            reviewer);

        final PermitContainer permitContainer = PERMIT_MAPPER.toPermitContainer(requestPayload, installationOperatorDetails);
        permitService.submitPermit(permitContainer, accountId);

        accountUpdateService.updateAccountUponPermitGranted(
            accountId,
            permitContainer.getPermitType() == PermitType.HSE ? EmitterType.HSE : EmitterType.GHGE,
            permitContainer.getPermit().getEstimatedAnnualEmissions().getQuantity());
    }
}
