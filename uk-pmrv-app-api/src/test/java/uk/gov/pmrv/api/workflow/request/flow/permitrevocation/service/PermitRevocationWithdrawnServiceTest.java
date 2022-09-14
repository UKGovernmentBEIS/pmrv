package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.notification.PermitRevocationOfficialNoticeService;

@ExtendWith(MockitoExtension.class)
class PermitRevocationWithdrawnServiceTest {

    @InjectMocks
    private PermitRevocationWithdrawnService service;

    @Mock
    private RequestService requestService;
    
    @Mock 
    private PermitRevocationOfficialNoticeService permitRevocationOfficialNoticeService;
    
    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void withdraw() {

        final String requestId = "1";
        final Long accountId = 1L;
        final UUID file = UUID.randomUUID();
        final DecisionNotification withdrawDecisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator1"))
            .signatory("regulator1")
            .build();
        final PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_REVOCATION_REQUEST_PAYLOAD)
            .withdrawDecisionNotification(withdrawDecisionNotification)
            .withdrawReason("the reason")
            .withdrawFiles(Set.of(file))
            .revocationAttachments(Map.of(file, "file"))
            .regulatorAssignee("regulatorAssignee")
            .build();
        final Request request = Request.builder()
            .id(requestId)
            .payload(requestPayload)
            .accountId(accountId)
            .build();
        final FileInfoDTO withdrawnOfficialNotice = FileInfoDTO.builder()
        		.name("official notice.pdf")
        		.uuid(UUID.randomUUID().toString())
        		.build();

        when(requestService.findRequestById(requestId)).thenReturn(request);
        when(requestActionUserInfoResolver
            .getUsersInfo(withdrawDecisionNotification.getOperators(), withdrawDecisionNotification.getSignatory(), request))
            .thenReturn(Map.of(
                "operator1", RequestActionUserInfo.builder().name("operator1").roleCode("operator").build(),
                "regulator1", RequestActionUserInfo.builder().name("regulator1").roleCode("regulator").build()
            ));
        when(permitRevocationOfficialNoticeService.generateRevocationWithdrawnOfficialNotice(request.getId()))
        	.thenReturn(withdrawnOfficialNotice);

        service.withdraw(requestId);

        verify(requestActionUserInfoResolver, times(1))
            .getUsersInfo(withdrawDecisionNotification.getOperators(), withdrawDecisionNotification.getSignatory(), request);

        final ArgumentCaptor<PermitRevocationApplicationWithdrawnRequestActionPayload> requestActionPayloadCaptor =
            ArgumentCaptor.forClass(PermitRevocationApplicationWithdrawnRequestActionPayload.class);

        verify(requestService, times(1)).addActionToRequest(
            Mockito.eq(request),
            requestActionPayloadCaptor.capture(),
            Mockito.eq(RequestActionType.PERMIT_REVOCATION_APPLICATION_WITHDRAWN),
            Mockito.eq(requestPayload.getRegulatorAssignee()));
        verify(permitRevocationOfficialNoticeService, times(1)).generateRevocationWithdrawnOfficialNotice(request.getId());
        verify(permitRevocationOfficialNoticeService, times(1))
            .sendOfficialNotice(request, withdrawnOfficialNotice, withdrawDecisionNotification);

        final PermitRevocationApplicationWithdrawnRequestActionPayload actionPayloadCaptured =
            requestActionPayloadCaptor.getValue();

        assertThat(actionPayloadCaptured.getPayloadType()).isEqualTo(
            RequestActionPayloadType.PERMIT_REVOCATION_APPLICATION_WITHDRAWN_PAYLOAD);
        assertThat(actionPayloadCaptured.getUsersInfo()).isEqualTo(Map.of(
            "operator1", RequestActionUserInfo.builder().name("operator1").roleCode("operator").build(),
            "regulator1", RequestActionUserInfo.builder().name("regulator1").roleCode("regulator").build()
        ));
        assertThat(actionPayloadCaptured.getDecisionNotification()).isEqualTo(requestPayload.getWithdrawDecisionNotification());
        assertThat(actionPayloadCaptured.getReason()).isEqualTo("the reason");
        assertThat(actionPayloadCaptured.getWithdrawFiles()).isEqualTo(Set.of(file));
        assertThat(actionPayloadCaptured.getRevocationAttachments()).isEqualTo(Map.of(file, "file"));
        assertThat(actionPayloadCaptured.getWithdrawnOfficialNotice()).isEqualTo(withdrawnOfficialNotice);
    }
}