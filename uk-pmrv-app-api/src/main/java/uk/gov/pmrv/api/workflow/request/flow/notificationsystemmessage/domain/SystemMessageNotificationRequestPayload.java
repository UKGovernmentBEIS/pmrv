package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class SystemMessageNotificationRequestPayload extends RequestPayload {

    @JsonUnwrapped
    private SystemMessageNotificationPayload messagePayload;
}
