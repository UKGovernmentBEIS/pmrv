package uk.gov.pmrv.api.authorization.core.domain.dto;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserAuthorityDTO {
    private String userId;
    private String roleName;
    private String roleCode;
    private AuthorityStatus authorityStatus;
    private LocalDateTime authorityCreationDate;
}
