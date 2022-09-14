package uk.gov.pmrv.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountSearchCriteria {
    
    private String term;
    private AccountType type;
    
    private Long page;
    private Long pageSize;
}
