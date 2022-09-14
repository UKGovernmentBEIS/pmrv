package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitNotificationWaitForFollowUpRequestTaskPayload extends RequestTaskPayload {

    private String followUpRequest;
    
    private LocalDate followUpResponseExpirationDate;
}
