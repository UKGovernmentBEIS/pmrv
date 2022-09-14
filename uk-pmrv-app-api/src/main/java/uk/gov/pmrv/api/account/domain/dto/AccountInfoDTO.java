package uk.gov.pmrv.api.account.domain.dto;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;

@Getter
@EqualsAndHashCode
public class AccountInfoDTO {
    
	private Long id;
	private String name;
	private String emitterId;
	private AccountStatus status;
	private String legalEntityName;
	
    public AccountInfoDTO(Long id, String name, String emitterId, String status, String legalEntityName) {
        this.id = id;
        this.name = name;
        this.emitterId = emitterId;
        this.status = AccountStatus.valueOf(status);
        this.legalEntityName = legalEntityName;
    }
	
}
