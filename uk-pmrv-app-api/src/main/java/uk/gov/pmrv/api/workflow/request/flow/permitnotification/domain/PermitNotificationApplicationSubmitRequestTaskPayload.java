package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PermitNotificationApplicationSubmitRequestTaskPayload extends RequestTaskPayload {

    @Valid
    @NotNull
    private PermitNotification permitNotification;

    @Builder.Default
    private Map<UUID, String> permitNotificationAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> sectionsCompleted = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getPermitNotificationAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return this.getPermitNotification() != null ?
                this.getPermitNotification().getAttachmentIds() :
                Collections.emptySet();
    }
}
