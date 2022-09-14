package uk.gov.pmrv.api.common.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * The authenticated User.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PmrvUser {

    /** The keycloak user Id. */
    private String userId;

    /** The keycloak user email. */
    private String email;

    /** The keycloak user first name. */
    private String firstName;

    /** The keycloak user last name. */
    private String lastName;

    /** The List of {@link PmrvAuthority} applicable user authorities. */
    @Builder.Default
    private List<PmrvAuthority> authorities = new ArrayList<>();

    /** The {@link RoleType}. */
    private RoleType roleType;

    public Set<Long> getAccounts() {
        return this.authorities.stream()
            .map(PmrvAuthority::getAccountId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());
    }

    /**
     * Returns the current user's {@link PmrvUser} verificationBodyId.
     * @return null if {@link PmrvUser} is {@link RoleType#VERIFIER}
     */
    public Long getVerificationBodyId() {
        return this.authorities.isEmpty() ? null : this.authorities.get(0).getVerificationBodyId();
    }

    /**
     * Returns the current user's {@link PmrvUser} {@link CompetentAuthority}.
     * @return null if {@link PmrvUser} is {@link RoleType#REGULATOR}
     */
    public CompetentAuthority getCompetentAuthority() {
        return this.authorities.isEmpty() ? null : this.authorities.get(0).getCompetentAuthority();
    }
}