package uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain;

import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SystemMessageNotificationPayload {

    @NotNull
    private String subject;

    @NotNull
    private String text;
}
