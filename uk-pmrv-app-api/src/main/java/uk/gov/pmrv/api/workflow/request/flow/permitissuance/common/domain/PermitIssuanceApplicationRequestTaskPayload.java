package uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class PermitIssuanceApplicationRequestTaskPayload extends RequestTaskPayload {

    private PermitType permitType;

    private Permit permit;

    private InstallationOperatorDetails installationOperatorDetails;

    @Builder.Default
    private Map<String, List<Boolean>> permitSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> permitAttachments = new HashMap<>();
    
    @Override
    public Map<UUID, String> getAttachments() {
        return getPermitAttachments();
    }
    
    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return getPermit() != null ?
            getPermit().getPermitSectionAttachmentIds() :
            Collections.emptySet();
    }
}
