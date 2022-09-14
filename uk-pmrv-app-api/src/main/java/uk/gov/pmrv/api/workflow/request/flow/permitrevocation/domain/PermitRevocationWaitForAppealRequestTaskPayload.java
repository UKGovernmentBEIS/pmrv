package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain;

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
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
public class PermitRevocationWaitForAppealRequestTaskPayload extends RequestTaskPayload {
    
    @NotBlank
    @Size(max = 10000)
    private String reason;

    @Builder.Default
    private Set<UUID> withdrawFiles = new HashSet<>();

    @Builder.Default
    private Map<UUID, String> revocationAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.revocationAttachments;
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return this.withdrawFiles;
    }
}
