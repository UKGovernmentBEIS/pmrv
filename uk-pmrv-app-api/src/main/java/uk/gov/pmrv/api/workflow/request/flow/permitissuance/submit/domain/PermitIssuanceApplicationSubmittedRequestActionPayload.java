package uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestActionPayload;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitIssuanceApplicationSubmittedRequestActionPayload extends RequestActionPayload {

    @NotNull
    private PermitType permitType;

    @Valid
    @NotNull
    private Permit permit;

    @Valid
    @NotNull
    private InstallationOperatorDetails installationOperatorDetails;

    @Builder.Default
    private Map<UUID, String> permitAttachments = new HashMap<>();

    @Builder.Default
    private Map<String, List<Boolean>> permitSectionsCompleted = new HashMap<>();

    @Override
    public Map<UUID, String> getAttachments() {
        return this.getPermitAttachments();
    }
}
