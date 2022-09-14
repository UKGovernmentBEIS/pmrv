package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.service.AccountUpdateService;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.permit.service.PermitService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceGrantDetermination;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceGrantedServiceTest {

    @InjectMocks
    private PermitIssuanceGrantedService service;

    @Mock
    private AccountUpdateService accountUpdateService;

    @Mock
    private RequestService requestService;

    @Mock
    private PermitService permitService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    void execute() {
        final String requestId = "1";
        final String operator = "operator";
        final String signatory = "signatory";
        final Long accountId = 2L;
        final BigDecimal estimatedAnnualEmissions = BigDecimal.valueOf(50000);
        final Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder().quantity(estimatedAnnualEmissions).build())
            .build();
        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().installationName("sample").build();
        final PermitIssuanceGrantDetermination determination = PermitIssuanceGrantDetermination.builder().type(DeterminationType.GRANTED).build();
        final DecisionNotification decisionNotification = DecisionNotification.builder().operators(Set.of(operator)).signatory(signatory).build();
        final PermitIssuanceRequestPayload permitIssuanceRequestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .permitType(PermitType.HSE)
            .permit(permit)
            .permitDecisionNotification(decisionNotification)
            .determination(determination)
            .regulatorReviewer("reviewer")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(permitIssuanceRequestPayload)
            .accountId(accountId)
            .build();
        final PermitContainer permitContainer = PermitContainer.builder()
            .permitType(PermitType.HSE)
            .installationOperatorDetails(installationOperatorDetails)
            .permit(permit)
            .build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId())).thenReturn(installationOperatorDetails);
        when(requestActionUserInfoResolver.getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request))
            .thenReturn(Map.of());

        service.grant(requestId);

        verify(requestService, times(1)).findRequestById(requestId);
        verify(requestActionUserInfoResolver, times(1))
            .getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request);
        verify(permitService, times(1)).submitPermit(permitContainer, request.getAccountId());
        verify(accountUpdateService, times(1))
            .updateAccountUponPermitGranted(request.getAccountId(), EmitterType.HSE, estimatedAnnualEmissions);
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
    }
}
