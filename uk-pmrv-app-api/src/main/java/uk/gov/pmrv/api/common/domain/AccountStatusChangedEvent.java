package uk.gov.pmrv.api.common.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AccountStatusChangedEvent {

    private Long accountId;
    private AccountStatus status;
}
