package uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitIssuanceRequestPayload extends RequestPayload
    implements RequestPayloadRfiable, RequestPayloadRdeable, RequestPayloadPayable {

    private PermitType permitType;

    private Permit permit;

    @Builder.Default
    private Map<UUID, String> permitAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, List<Boolean>> permitSectionsCompleted = new HashMap<>();
    
    private DecisionNotification permitDecisionNotification;
    
    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecisions = new EnumMap<>(PermitReviewGroup.class);

    @Builder.Default
    private Map<UUID, String> reviewAttachments = new HashMap<>();

    private PermitIssuanceDeterminateable determination;
    
    @JsonUnwrapped
    private RfiData rfiData;
    
    @JsonUnwrapped
    private RdeData rdeData;

    private RequestPaymentInfo requestPaymentInfo;

}
