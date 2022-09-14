package uk.gov.pmrv.api.permit.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermitContainer {

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

    private LocalDate activationDate;

    /**
     * A sortedMap that holds the holds the annual emission targets of the installation that the permit is issued about.
     * Map keys represent the years (e.g 2022) for which the emission targets are set, whereas value are the targets itself.
     */
    @Builder.Default
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private SortedMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>();
}
