package uk.gov.pmrv.api.web.orchestrator.dto;

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
public class UserAuthorityInfoDTO {
    private String userId;
    private String firstName;
    private String lastName;
    private String roleName;
    private String roleCode;
    private LocalDateTime authorityCreationDate;
    private AuthorityStatus authorityStatus;
    private Boolean locked;
}
