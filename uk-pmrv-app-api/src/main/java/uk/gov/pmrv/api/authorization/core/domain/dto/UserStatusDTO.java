package uk.gov.pmrv.api.authorization.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserStatusDTO {

    private String userId;

    private RoleType roleType;

    private LoginStatus loginStatus;
}
