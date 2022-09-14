package uk.gov.pmrv.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.domain.enumeration.InstallationCategory;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountHeaderInfoDTO {

    private String name;
    private AccountStatus status;
    private EmitterType emitterType;
    private InstallationCategory installationCategory;
    private String permitId;
}
