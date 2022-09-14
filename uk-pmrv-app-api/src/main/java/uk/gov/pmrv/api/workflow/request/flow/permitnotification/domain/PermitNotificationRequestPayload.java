package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

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
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;

@Data
@EqualsAndHashCode(callSuper = true)
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class PermitNotificationRequestPayload extends RequestPayload implements RequestPayloadRfiable {

    private PermitNotification permitNotification;

    @Builder.Default
    private Map<UUID, String> permitNotificationAttachments = new HashMap<>();

    private PermitNotificationReviewDecision reviewDecision;

    private DecisionNotification reviewDecisionNotification;

    private String followUpResponse;

    @Builder.Default
    private Set<UUID> followUpResponseFiles = new HashSet<>();

    @Builder.Default
    private Map<UUID, String> followUpResponseAttachments = new HashMap<>();
    
    private LocalDate followUpResponseSubmissionDate;
    
    private PermitNotificationFollowUpReviewDecision followUpReviewDecision;
    
    private DecisionNotification followUpReviewDecisionNotification;

    @Builder.Default
    private Map<String, Boolean> followUpReviewSectionsCompleted = new HashMap<>();


    @Builder.Default
    private Map<String, Boolean> followUpSectionsCompleted = new HashMap<>();

    private FileInfoDTO officialNotice;
    
    @JsonUnwrapped
    private RfiData rfiData;

}
