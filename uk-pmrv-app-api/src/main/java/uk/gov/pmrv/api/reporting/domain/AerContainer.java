package uk.gov.pmrv.api.reporting.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AerContainer {

    @Valid
    @NotNull
    private Aer aer;

    @Valid
    @NotNull
    private InstallationOperatorDetails installationOperatorDetails;

    @Builder.Default
    private Map<UUID, String> aerAttachments = new HashMap<>();
}
