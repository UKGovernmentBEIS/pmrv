package uk.gov.pmrv.api.workflow.request.flow.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessation;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationContainer;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationDeterminationOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@ExtendWith(MockitoExtension.class)
class PermitCessationCompletedRequestActionPayloadMapperTest {

    @InjectMocks
    private PermitCessationCompletedRequestActionPayloadMapper cessationCompletedRequestActionPayloadMapper;

    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;

    @Test
    void toCessationCompletedRequestActionPayload() {
        Long accountId = 1L;
        String operatorUser = "operatorUser";
        String operatorAdmin = "operatorAdmin";
        String signatoryUser = "signatoryUser";
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of(operatorAdmin, operatorUser))
            .signatory(signatoryUser)
            .build();
        NotifyOperatorForDecisionRequestTaskActionPayload notifyOperatorForDecisionRequestTaskActionPayload =
            NotifyOperatorForDecisionRequestTaskActionPayload.builder().decisionNotification(decisionNotification).build();

        PermitCessation cessation = PermitCessation.builder()
            .determinationOutcome(PermitCessationDeterminationOutcome.APPROVED)
            .notes("notes")
            .build();
        PermitCessationSubmitRequestTaskPayload requestTaskPayload = PermitCessationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_CESSATION_SUBMIT_PAYLOAD)
            .cessationContainer(PermitCessationContainer.builder().cessation(cessation).build())
            .build();
        Request request = Request.builder().accountId(accountId).build();
        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_SURRENDER_CESSATION_SUBMIT)
            .payload(requestTaskPayload)
            .request(request)
            .build();

        Map<String, RequestActionUserInfo> userInfo = Map.of(
            operatorUser, RequestActionUserInfo.builder().name(operatorUser).build(),
            operatorAdmin, RequestActionUserInfo.builder().name(operatorAdmin).build(),
            signatoryUser, RequestActionUserInfo.builder().name(signatoryUser).build()
        );
        
        FileInfoDTO cessationOfficialNotice = FileInfoDTO.builder()
                .name("off notice").uuid(UUID.randomUUID().toString())
                .build();

        when(requestActionUserInfoResolver.getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request))
            .thenReturn(userInfo);

        PermitCessationCompletedRequestActionPayload result =
            cessationCompletedRequestActionPayloadMapper.toCessationCompletedRequestActionPayload(requestTask,
                notifyOperatorForDecisionRequestTaskActionPayload,
                cessationOfficialNotice,
                RequestActionPayloadType.PERMIT_SURRENDER_CESSATION_COMPLETED_PAYLOAD);

        assertEquals(RequestActionPayloadType.PERMIT_SURRENDER_CESSATION_COMPLETED_PAYLOAD,
                result.getPayloadType());
        assertEquals(cessation, result.getCessation());
        assertEquals(decisionNotification, result.getCessationDecisionNotification());
        assertEquals(userInfo, result.getCessationDecisionNotificationUsersInfo());
        assertThat(result.getCessationOfficialNotice()).isEqualTo(cessationOfficialNotice);
    }
}