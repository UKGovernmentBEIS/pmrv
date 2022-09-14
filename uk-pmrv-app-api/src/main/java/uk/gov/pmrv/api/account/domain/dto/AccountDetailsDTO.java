package uk.gov.pmrv.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.ApplicationType;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.SubsistenceCategory;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDetailsDTO {

    private Long id;
    private String name;
    private AccountType accountType;
    private AccountStatus status;
    private String siteName;
    private LocationDTO location;
    private Long sopId;
    private Integer registryId;

    private String permitId;
    private EmitterType emitterType;
    private InstallationCategory installationCategory;
    private SubsistenceCategory subsistenceCategory;
    private ApplicationType applicationType;

    // LE info
    private String legalEntityName;
    private LegalEntityType legalEntityType;
    private String companyReferenceNumber;
    private AddressDTO legalEntityAddress;
}
