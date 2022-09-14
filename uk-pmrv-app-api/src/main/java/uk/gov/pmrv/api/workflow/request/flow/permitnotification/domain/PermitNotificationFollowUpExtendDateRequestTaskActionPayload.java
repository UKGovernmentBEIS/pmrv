package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import java.time.LocalDate;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitNotificationFollowUpExtendDateRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    @Future
    private LocalDate dueDate;
}
