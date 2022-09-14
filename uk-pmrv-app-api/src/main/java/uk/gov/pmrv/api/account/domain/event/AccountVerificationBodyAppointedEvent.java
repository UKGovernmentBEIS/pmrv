package uk.gov.pmrv.api.account.domain.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class AccountVerificationBodyAppointedEvent {

    private Long accountId;
    private Long verificationBodyId;
}
