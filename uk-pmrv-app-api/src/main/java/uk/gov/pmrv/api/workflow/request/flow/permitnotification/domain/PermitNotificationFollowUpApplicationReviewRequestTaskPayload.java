package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PastOrPresent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitNotificationFollowUpApplicationReviewRequestTaskPayload extends PermitNotificationFollowUpRequestTaskPayload {

    @NotNull
    private PermitNotificationType permitNotificationType;
    
    @NotNull
    @PastOrPresent
    private LocalDate submissionDate;

    @NotNull
    private PermitNotificationFollowUpReviewDecision reviewDecision;

    @Builder.Default
    private Map<String, Boolean> reviewSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<String, Boolean> followUpSectionsCompleted = new HashMap<>();


    @Override
    public Set<UUID> getReferencedAttachmentIds() {

        final Set<UUID> reviewFiles = reviewDecision != null && reviewDecision.getFiles() != null ?
            reviewDecision.getFiles() : Set.of();

        return Stream.of(super.getReferencedAttachmentIds(), reviewFiles)
            .flatMap(Set::stream)
            .collect(Collectors.toSet());
    }
}
