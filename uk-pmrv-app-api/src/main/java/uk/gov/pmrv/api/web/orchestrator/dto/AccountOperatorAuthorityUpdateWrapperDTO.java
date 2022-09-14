package uk.gov.pmrv.api.web.orchestrator.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.authorization.operator.domain.AccountOperatorAuthorityUpdateDTO;
import uk.gov.pmrv.api.web.orchestrator.validate.AccountOperatorAuthorityUpdate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

@AccountOperatorAuthorityUpdate
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AccountOperatorAuthorityUpdateWrapperDTO {

    @NotNull
    @Valid
    private List<AccountOperatorAuthorityUpdateDTO> accountOperatorAuthorityUpdateList;

    @NotNull
    private Map<AccountContactType, String> contactTypes;
    
}
