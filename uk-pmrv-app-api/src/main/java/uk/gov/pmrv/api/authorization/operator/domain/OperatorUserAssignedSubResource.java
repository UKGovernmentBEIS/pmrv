package uk.gov.pmrv.api.authorization.operator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OperatorUserAssignedSubResource {

    private Long accountId;
    private String resourceSubType;
}
