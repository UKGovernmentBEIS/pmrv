package uk.gov.pmrv.api.authorization.operator.event;

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
public class OperatorAuthorityDeletionEvent {

    private String userId;
    private Long accountId;
}
