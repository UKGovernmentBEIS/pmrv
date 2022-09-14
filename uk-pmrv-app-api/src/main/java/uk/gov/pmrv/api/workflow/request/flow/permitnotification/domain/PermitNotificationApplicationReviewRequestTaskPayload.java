package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import org.apache.commons.collections.CollectionUtils;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestTaskPayloadRfiAttachable;

import javax.validation.constraints.NotNull;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitNotificationApplicationReviewRequestTaskPayload extends RequestTaskPayload implements RequestTaskPayloadRfiAttachable {

    @NotNull
    private PermitNotification permitNotification;

    @Builder.Default
    private Map<UUID, String> permitNotificationAttachments = new HashMap<>();

    @NotNull
    private PermitNotificationReviewDecision reviewDecision;

    @Builder.Default
    private Map<UUID, String> rfiAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(this.getPermitNotificationAttachments(), getRfiAttachments())
                .flatMap(map -> map.entrySet().stream())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return this.getPermitNotification() != null ?
                this.getPermitNotification().getAttachmentIds() :
                Collections.emptySet();
    }

    @Override
    public void removeAttachments(final Collection<UUID> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        super.removeAttachments(uuids);
        getRfiAttachments().keySet().removeIf(uuids::contains);
    }
}
