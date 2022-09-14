package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitNotificationApplicationSubmittedRequestActionPayload extends RequestActionPayload {

    @Valid
    @NotNull
    private PermitNotification permitNotification;

    @Builder.Default
    private Map<UUID, String> permitNotificationAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getPermitNotificationAttachments();
    }
}
