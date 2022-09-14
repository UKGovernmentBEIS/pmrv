package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitNotificationFollowUpReturnedForAmendsRequestActionPayload extends RequestActionPayload {

    @NotBlank
    @Size(max = 10000)
    private String changesRequired;

    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Set<UUID> amendFiles = new HashSet<>();
    
    @Future
    private LocalDate dueDate;

    @Size(max = 10000)
    private String notes;

    @Builder.Default
    private Map<UUID, String> amendAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.amendAttachments;
    }
}
