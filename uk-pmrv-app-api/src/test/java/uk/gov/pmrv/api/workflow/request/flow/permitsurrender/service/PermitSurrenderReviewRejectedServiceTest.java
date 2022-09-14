package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationReject;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.notification.PermitSurrenderOfficialNoticeService;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderReviewRejectedServiceTest {

    @InjectMocks
    private PermitSurrenderReviewRejectedService service;

    @Mock
    private RequestService requestService;
    
    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;
    
    @Mock
    private PermitSurrenderOfficialNoticeService permitSurrenderOfficialNoticeService;
    
    @Test
    void executeDeemedWithdrawnPostActions() {
        final String requestId = "1";
        Long accountId= 1L;
        final DecisionNotification reviewDecisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator1"))
            .signatory("regulator1")
            .build();
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
                .reviewDetermination(PermitSurrenderReviewDeterminationReject.builder()
                        .type(PermitSurrenderReviewDeterminationType.REJECTED)
                        .reason("reason")
                        .build())
                .reviewDecisionNotification(reviewDecisionNotification)
                .regulatorReviewer("regulatorReviewer")
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .accountId(accountId)
                .build();
        
        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver
            .getUsersInfo(reviewDecisionNotification.getOperators(), reviewDecisionNotification.getSignatory(), request))
            .thenReturn(Map.of(
                    "operator1", RequestActionUserInfo.builder().name("operator1").roleCode("operator").build(),
                    "regulator1", RequestActionUserInfo.builder().name("regulator1").roleCode("regulator").build()
                    ));
        
        service.executeRejectedPostActions(requestId);
        
        verify(requestActionUserInfoResolver, times(1))
            .getUsersInfo(reviewDecisionNotification.getOperators(), reviewDecisionNotification.getSignatory(), request);
        verify(permitSurrenderOfficialNoticeService, times(1)).sendReviewDeterminationOfficialNotice(request);
        
        ArgumentCaptor<PermitSurrenderApplicationRejectedRequestActionPayload> requestActionPayloadCaptor = ArgumentCaptor.forClass(PermitSurrenderApplicationRejectedRequestActionPayload.class);
        verify(requestService, times(1)).addActionToRequest(Mockito.eq(request), requestActionPayloadCaptor.capture(), Mockito.eq(RequestActionType.PERMIT_SURRENDER_APPLICATION_REJECTED), Mockito.eq(requestPayload.getRegulatorReviewer()));
        PermitSurrenderApplicationRejectedRequestActionPayload actionPayloadCaptured = requestActionPayloadCaptor.getValue();
        assertThat(actionPayloadCaptured.getPayloadType()).isEqualTo(RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_REJECTED_PAYLOAD);
        assertThat(actionPayloadCaptured.getUsersInfo()).isEqualTo(Map.of(
                "operator1", RequestActionUserInfo.builder().name("operator1").roleCode("operator").build(),
                "regulator1", RequestActionUserInfo.builder().name("regulator1").roleCode("regulator").build()
                ));
        assertThat(actionPayloadCaptured.getReviewDecisionNotification()).isEqualTo(requestPayload.getReviewDecisionNotification());
        assertThat(actionPayloadCaptured.getReviewDetermination()).isEqualTo(requestPayload.getReviewDetermination());
    }
}
