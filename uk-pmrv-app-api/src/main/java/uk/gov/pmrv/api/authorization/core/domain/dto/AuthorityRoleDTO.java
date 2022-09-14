package uk.gov.pmrv.api.authorization.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuthorityRoleDTO {

    private String userId;
    private AuthorityStatus authorityStatus;
    private String roleName;
    private String roleCode;
    private LocalDateTime creationDate;
}
