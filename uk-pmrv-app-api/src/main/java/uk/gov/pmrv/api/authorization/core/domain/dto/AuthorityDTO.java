package uk.gov.pmrv.api.authorization.core.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

import java.util.ArrayList;
import java.util.List;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthorityDTO implements GrantedAuthority {

    private String code;

    private AuthorityStatus status;

    private Long accountId;

    private CompetentAuthority competentAuthority;

    private Long verificationBodyId;

    @Builder.Default
    private List<Permission> authorityPermissions = new ArrayList<>();

    @Override
    public String getAuthority() {
        return getCode();
    }

}
