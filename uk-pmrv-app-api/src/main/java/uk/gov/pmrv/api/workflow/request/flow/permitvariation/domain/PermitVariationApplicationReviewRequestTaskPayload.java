package uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain;

import java.util.Collection;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.util.CollectionUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitPayloadDecidableAndDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestTaskPayloadRfiAttachable;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitVariationApplicationReviewRequestTaskPayload extends PermitVariationRequestTaskPayload
    implements RequestTaskPayloadRfiAttachable,
    PermitPayloadDecidableAndDeterminateable<PermitVariationReviewDecision, PermitVariationDeterminateable> {

    private PermitContainer originalPermitContainer;

    private PermitVariationReviewDecision permitVariationDetailsReviewDecision;
    
    private Boolean permitVariationDetailsReviewCompleted;

    @Builder.Default
    private Map<PermitReviewGroup, PermitVariationReviewDecision> reviewGroupDecisions = new EnumMap<>(PermitReviewGroup.class);

    private PermitVariationDeterminateable determination;

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> rfiAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return Stream.of(super.getAttachments(), getReviewAttachments(), getRfiAttachments())
            .flatMap(map -> map.entrySet().stream())
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        final Set<UUID> reviewAttachmentIds = getReviewGroupDecisions().values().stream()
            .flatMap(permitVariationReviewDecision -> permitVariationReviewDecision.getRequiredChanges().stream()
                .map(PermitReviewDecisionRequiredChange::getFiles))
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
        getRfiAttachments().keySet().removeIf(uuids::contains);
    }

}
