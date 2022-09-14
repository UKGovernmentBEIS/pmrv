package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonUnwrapped;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessation;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RdeData;
import uk.gov.pmrv.api.workflow.request.flow.rde.domain.RequestPayloadRdeable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PermitSurrenderRequestPayload extends RequestPayload
    implements RequestPayloadRfiable, RequestPayloadRdeable, RequestPayloadPayable {
    
    private PermitSurrender permitSurrender;
    
    @Builder.Default
    private Map<UUID, String> permitSurrenderAttachments = new HashMap<>();

    private PermitSurrenderReviewDecision reviewDecision;

    private PermitSurrenderReviewDetermination reviewDetermination;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean reviewDeterminationCompleted;

    private DecisionNotification reviewDecisionNotification;
    
    private LocalDate reviewDeterminationCompletedDate;
    
    private FileInfoDTO officialNotice;
    
    private PermitCessation permitCessation;
    
    @JsonUnwrapped
    private RfiData rfiData;

    @JsonUnwrapped
    private RdeData rdeData;

    private LocalDate currentDueDate;

    private RequestPaymentInfo requestPaymentInfo;

}
