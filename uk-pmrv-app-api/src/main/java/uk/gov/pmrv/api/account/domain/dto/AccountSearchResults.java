package uk.gov.pmrv.api.account.domain.dto;

import java.util.List;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AccountSearchResults {

    private List<AccountInfoDTO> accounts;
    private Long total;
    
}
