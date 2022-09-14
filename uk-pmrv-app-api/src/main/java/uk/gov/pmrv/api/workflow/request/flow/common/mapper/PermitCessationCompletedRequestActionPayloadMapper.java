package uk.gov.pmrv.api.workflow.request.flow.common.mapper;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;

@Service
@RequiredArgsConstructor
public class PermitCessationCompletedRequestActionPayloadMapper {

    private final RequestActionUserInfoResolver requestActionUserInfoResolver;

    public PermitCessationCompletedRequestActionPayload toCessationCompletedRequestActionPayload(
        RequestTask requestTask, 
        NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload,
        FileInfoDTO cessationOfficialNotice,
        RequestActionPayloadType requestActionPayloadType) {

        Request request = requestTask.getRequest();
        PermitCessationSubmitRequestTaskPayload requestTaskPayload =
            (PermitCessationSubmitRequestTaskPayload) requestTask.getPayload();
        DecisionNotification decisionNotification = taskActionPayload.getDecisionNotification();

        Map<String, RequestActionUserInfo> usersInfo = requestActionUserInfoResolver
            .getUsersInfo(decisionNotification.getOperators(), decisionNotification.getSignatory(), request);

        return PermitCessationCompletedRequestActionPayload.builder()
                .payloadType(requestActionPayloadType)
                .cessation(requestTaskPayload.getCessationContainer().getCessation())
                .cessationDecisionNotification(decisionNotification)
                .cessationDecisionNotificationUsersInfo(usersInfo)
                .cessationOfficialNotice(cessationOfficialNotice)
            .build();
    }
}
