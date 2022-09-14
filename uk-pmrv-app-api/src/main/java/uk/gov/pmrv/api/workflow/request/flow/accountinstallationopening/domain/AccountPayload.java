package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain;

import java.time.LocalDate;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountPayload {

    @NotNull(message = "{account.type.notEmpty}")
    private AccountType accountType;

    @NotBlank(message = "{account.name.notEmpty}")
    @Size(max = 255, message = "{account.name.typeMismatch}")
    private String name;

    @NotBlank(message = "{account.siteName.notEmpty}")
    @Size(max = 255, message = "{account.siteName.invalidSize}")
    private String siteName;
    
    @NotNull(message = "{account.emissionTradingScheme.notEmpty}")
    private EmissionTradingScheme emissionTradingScheme;

    @NotNull(message = "{account.competentAuthority.notEmpty}")
    private CompetentAuthority competentAuthority;

    @NotNull(message = "{account.commencementDate.notEmpty}")
    private LocalDate commencementDate;

    @NotNull(message = "{account.legalEntity.notEmpty}")
    @Valid
    private LegalEntityDTO legalEntity;

    @NotNull(message = "{account.location.notEmpty}")
    @Valid
    private LocationDTO location;
}
