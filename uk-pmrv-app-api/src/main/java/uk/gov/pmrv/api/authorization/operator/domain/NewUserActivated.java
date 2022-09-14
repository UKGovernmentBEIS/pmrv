package uk.gov.pmrv.api.authorization.operator.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NewUserActivated {

    private String userId;

    private Long accountId;

    private String roleCode;
}
