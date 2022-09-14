package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.notification.PermitRevocationOfficialNoticeService;

@ExtendWith(MockitoExtension.class)
class PermitRevocationSubmittedServiceTest {

    @InjectMocks
    private PermitRevocationSubmittedService service;

    @Mock
    private RequestService requestService;
    
    @Mock 
    private PermitRevocationOfficialNoticeService permitRevocationOfficialNoticeService;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void submit() {

        final String requestId = "1";
        final Long accountId = 1L;
        final DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator1"))
            .signatory("regulator1")
            .build();
        final PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_REVOCATION_REQUEST_PAYLOAD)
            .permitRevocation(PermitRevocation.builder()
                .reason("reason")
                .activitiesStopped(false)
                .effectiveDate(LocalDate.of(2025, 1, 1))
                .feeCharged(true)
                .feeDate(LocalDate.of(2025, 2, 1))
                .feeDetails("details of the fee")
                .surrenderRequired(false)
                .build())
            .decisionNotification(decisionNotification)
            .regulatorAssignee("regulatorAssignee")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .accountId(accountId)
            .build();
        final FileInfoDTO officialNotice = FileInfoDTO.builder()
        		.name("official notice.pdf")
        		.uuid(UUID.randomUUID().toString())
        		.build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver
            .getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request))
            .thenReturn(Map.of(
                "operator1", RequestActionUserInfo.builder().name("operator1").roleCode("operator").build(),
                "regulator1", RequestActionUserInfo.builder().name("regulator1").roleCode("regulator").build()
            ));
        when(permitRevocationOfficialNoticeService.generateRevocationOfficialNotice(request.getId()))
    		.thenReturn(officialNotice);

        service.submit(requestId);

        verify(requestActionUserInfoResolver, times(1))
            .getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request);
        verify(permitRevocationOfficialNoticeService, times(1)).generateRevocationOfficialNotice(request.getId());
        verify(permitRevocationOfficialNoticeService, times(1))
            .sendOfficialNotice(request, officialNotice, decisionNotification);

        final ArgumentCaptor<PermitRevocationApplicationSubmittedRequestActionPayload> requestActionPayloadCaptor =
            ArgumentCaptor.forClass(PermitRevocationApplicationSubmittedRequestActionPayload.class);
        
        verify(requestService, times(1)).addActionToRequest(
            Mockito.eq(request), 
            requestActionPayloadCaptor.capture(),
            Mockito.eq(RequestActionType.PERMIT_REVOCATION_APPLICATION_SUBMITTED),
            Mockito.eq(requestPayload.getRegulatorAssignee()));
        
        final PermitRevocationApplicationSubmittedRequestActionPayload actionPayloadCaptured = requestActionPayloadCaptor.getValue();
        
        assertThat(actionPayloadCaptured.getPayloadType()).isEqualTo(
            RequestActionPayloadType.PERMIT_REVOCATION_APPLICATION_SUBMITTED_PAYLOAD);
        assertThat(actionPayloadCaptured.getUsersInfo()).isEqualTo(Map.of(
            "operator1", RequestActionUserInfo.builder().name("operator1").roleCode("operator").build(),
            "regulator1", RequestActionUserInfo.builder().name("regulator1").roleCode("regulator").build()
        ));
        assertThat(actionPayloadCaptured.getDecisionNotification()).isEqualTo(
            requestPayload.getDecisionNotification());
        assertThat(actionPayloadCaptured.getPermitRevocation()).isEqualTo(requestPayload.getPermitRevocation());
        assertThat(actionPayloadCaptured.getOfficialNotice()).isEqualTo(officialNotice);
        assertThat(actionPayloadCaptured.getOfficialNotice()).isEqualTo(officialNotice);
        assertThat(requestPayload.getOfficialNotice()).isEqualTo(officialNotice);
    }
}
