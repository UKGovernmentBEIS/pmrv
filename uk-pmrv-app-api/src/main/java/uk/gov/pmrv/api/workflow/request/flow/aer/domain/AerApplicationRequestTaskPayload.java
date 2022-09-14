package uk.gov.pmrv.api.workflow.request.flow.aer.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public abstract class AerApplicationRequestTaskPayload extends RequestTaskPayload {

    private Aer aer;

    private InstallationOperatorDetails installationOperatorDetails;

    @Builder.Default
    private Map<String, List<Boolean>> aerSectionsCompleted = new HashMap<>();

    @Builder.Default
    private Map<UUID, String> aerAttachments = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return getAerAttachments();
    }

    @Override
    public Set<UUID> getReferencedAttachmentIds() {
        return getAer() != null ?
                getAer().getAerSectionAttachmentIds() :
                Collections.emptySet();
    }
}
