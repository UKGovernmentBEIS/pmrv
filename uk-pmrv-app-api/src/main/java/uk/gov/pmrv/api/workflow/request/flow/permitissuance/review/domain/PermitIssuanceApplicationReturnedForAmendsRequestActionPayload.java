package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitIssuanceApplicationReturnedForAmendsRequestActionPayload extends RequestActionPayload {

    @Builder.Default
    private Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecisions = new EnumMap<>(PermitReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getReviewAttachments();
    }
}
