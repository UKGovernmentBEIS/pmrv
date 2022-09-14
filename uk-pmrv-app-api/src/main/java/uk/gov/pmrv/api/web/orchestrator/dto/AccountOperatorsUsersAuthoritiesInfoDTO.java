package uk.gov.pmrv.api.web.orchestrator.dto;

import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class AccountOperatorsUsersAuthoritiesInfoDTO extends UsersAuthoritiesInfoDTO {

	private Map<AccountContactType, String> contactTypes;
}
