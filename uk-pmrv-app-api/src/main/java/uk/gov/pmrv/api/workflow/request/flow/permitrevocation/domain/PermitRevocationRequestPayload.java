package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationContainer;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PermitRevocationRequestPayload extends RequestPayload implements RequestPayloadPayable {

    private PermitRevocation permitRevocation;
    
    private DecisionNotification decisionNotification;
    
    private FileInfoDTO officialNotice;

    private String withdrawReason;

    @Builder.Default
    private Set<UUID> withdrawFiles = new HashSet<>();

    private DecisionNotification withdrawDecisionNotification;
    
    private LocalDate withdrawCompletedDate;
    
    private PermitCessationContainer permitCessationContainer;
    
    private LocalDate permitCessationCompletedDate;

    private RequestPaymentInfo requestPaymentInfo;
    
    @Builder.Default
    private Map<UUID, String> revocationAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> sectionsCompleted = new HashMap<>();
}
