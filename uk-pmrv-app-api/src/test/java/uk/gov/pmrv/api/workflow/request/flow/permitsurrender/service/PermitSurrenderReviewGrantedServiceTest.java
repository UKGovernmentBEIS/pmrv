package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import static java.time.temporal.ChronoUnit.DAYS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Date;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountStatusService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.notification.PermitSurrenderOfficialNoticeService;
import uk.gov.pmrv.api.workflow.utils.DateUtils;

@ExtendWith(MockitoExtension.class)
class PermitSurrenderReviewGrantedServiceTest {

    @InjectMocks
    private PermitSurrenderReviewGrantedService service;

    @Mock
    private RequestService requestService;
    
    @Mock
    private AccountStatusService accountStatusService;
    
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
                .reviewDetermination(PermitSurrenderReviewDeterminationGrant.builder()
                        .type(PermitSurrenderReviewDeterminationType.GRANTED)
                        .stopDate(LocalDate.now().minusDays(1))
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
        
        service.executeGrantedPostActions(requestId);
        
        verify(accountStatusService, times(1)).handlePermitSurrenderGranted(accountId);
        verify(requestActionUserInfoResolver, times(1))
            .getUsersInfo(reviewDecisionNotification.getOperators(), reviewDecisionNotification.getSignatory(), request);
        verify(permitSurrenderOfficialNoticeService, times(1)).sendReviewDeterminationOfficialNotice(request);
        
        ArgumentCaptor<PermitSurrenderApplicationGrantedRequestActionPayload> requestActionPayloadCaptor = ArgumentCaptor.forClass(PermitSurrenderApplicationGrantedRequestActionPayload.class);
        verify(requestService, times(1)).addActionToRequest(Mockito.eq(request), requestActionPayloadCaptor.capture(), Mockito.eq(RequestActionType.PERMIT_SURRENDER_APPLICATION_GRANTED), Mockito.eq(requestPayload.getRegulatorReviewer()));
        PermitSurrenderApplicationGrantedRequestActionPayload actionPayloadCaptured = requestActionPayloadCaptor.getValue();
        assertThat(actionPayloadCaptured.getPayloadType()).isEqualTo(RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_GRANTED_PAYLOAD);
        assertThat(actionPayloadCaptured.getUsersInfo()).isEqualTo(Map.of(
                "operator1", RequestActionUserInfo.builder().name("operator1").roleCode("operator").build(),
                "regulator1", RequestActionUserInfo.builder().name("regulator1").roleCode("regulator").build()
                ));
        assertThat(actionPayloadCaptured.getReviewDecisionNotification()).isEqualTo(requestPayload.getReviewDecisionNotification());
        assertThat(actionPayloadCaptured.getReviewDetermination()).isEqualTo(requestPayload.getReviewDetermination());
    }
    
    @Test
    void resolveNoticeReminderDate() {
    	final String requestId = "1";
    	Long accountId= 1L;
    	LocalDate noticeDate = LocalDate.now();
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
                .reviewDetermination(PermitSurrenderReviewDeterminationGrant.builder()
                        .type(PermitSurrenderReviewDeterminationType.GRANTED)
                        .noticeDate(noticeDate)
                        .build())
                .build();
        Request request = Request.builder()
                .id(requestId)
                .payload(requestPayload)
                .accountId(accountId)
                .build();
        
    	when(requestService.findRequestById(requestId)).thenReturn(request);
    	
    	Date result = service.resolveNoticeReminderDate(requestId);
    	
    	assertThat(result).isEqualTo(DateUtils.convertLocalDateToDate(noticeDate.minus(28, DAYS)));
    	verify(requestService).findRequestById(requestId);
    }
}
