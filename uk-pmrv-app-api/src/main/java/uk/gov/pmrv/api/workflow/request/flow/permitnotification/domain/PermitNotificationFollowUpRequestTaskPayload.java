package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitNotificationFollowUpRequestTaskPayload extends RequestTaskPayload {

    private String followUpRequest;

    private LocalDate followUpResponseExpirationDate;

    @NotBlank
    @Size(max = 10000)
    private String followUpResponse;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> followUpFiles = new HashSet<>();

    @Builder.Default
    private Map<UUID, String> followUpAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.followUpAttachments;
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return this.followUpFiles;
    }
}
