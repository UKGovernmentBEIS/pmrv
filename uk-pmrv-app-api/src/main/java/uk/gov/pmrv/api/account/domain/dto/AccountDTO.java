package uk.gov.pmrv.api.account.domain.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountDTO {
	
	private Long id;
	
	@NotNull
	private AccountType accountType;

	private AccountStatus status;

	@NotBlank
	@Size(max = 255)
	private String name;

	@Size(max = 255)
	@NotBlank
	private String siteName;
	
	@NotNull
    private EmissionTradingScheme emissionTradingScheme;

	@NotNull
	private CompetentAuthority competentAuthority;

	@NotNull
	private LocalDate commencementDate;
	
	@NotNull
    @Valid
	private LegalEntityDTO legalEntity;

	@NotNull
    @Valid
	private LocationDTO location;
	
	private LocalDateTime acceptedDate;
	
	private EmitterType emitterType;
}
