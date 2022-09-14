package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.util.CollectionUtils;

import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitPayloadGroupDecidable;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceApplicationRequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class PermitIssuanceReviewRequestTaskPayload extends PermitIssuanceApplicationRequestTaskPayload
    implements PermitPayloadGroupDecidable<PermitIssuanceReviewDecision> {

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecisions = new EnumMap<>(PermitReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), getReviewAttachments())
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        final Set<UUID> reviewAttachmentIds = getReviewGroupDecisions().values().stream()
            .map(permitIssuanceReviewDecision -> permitIssuanceReviewDecision.getRequiredChange().getFiles())
            .flatMap(Set::stream)
            .collect(Collectors.toSet());

        return Stream.of(super.getReferencedAttachmentIds(), reviewAttachmentIds)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }

    @Override
    public void removeAttachments(final Collection<UUID> uuids) {
        if (CollectionUtils.isEmpty(uuids)) {
            return;
        }
        getPermitAttachments().keySet().removeIf(uuids::contains);
        getReviewAttachments().keySet().removeIf(uuids::contains);
    }

}
