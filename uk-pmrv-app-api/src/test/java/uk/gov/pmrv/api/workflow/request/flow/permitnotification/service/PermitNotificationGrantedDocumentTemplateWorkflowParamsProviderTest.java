package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationReviewDecision;

@ExtendWith(MockitoExtension.class)
class PermitNotificationGrantedDocumentTemplateWorkflowParamsProviderTest {

    @InjectMocks
    private PermitNotificationGrantedDocumentTemplateWorkflowParamsProvider provider;

    @Test
    void getRequestTaskActionType() {
        assertThat(provider.getContextActionType()).isEqualTo(DocumentTemplateGenerationContextActionType.PERMIT_NOTIFICATION_GRANTED);
    }

    @Test
    void constructParams() {

        final PermitNotificationRequestPayload payload = PermitNotificationRequestPayload.builder()
            .reviewDecision(PermitNotificationReviewDecision.builder().officialNotice("the official notice").build())
            .build();
        final Map<String, Object> result = provider.constructParams(payload);
        assertThat(result).containsExactlyEntriesOf(Map.of("officialNotice", "the official notice"));
    }

}
