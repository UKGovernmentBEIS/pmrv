package uk.gov.pmrv.api.authorization.core.domain;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Immutable;
import org.hibernate.annotations.Subselect;
import org.hibernate.annotations.Synchronize;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

/**
 * Read-only entity that represents the role type of a user.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
@Entity
@Subselect("select distinct user_id,  "
    + "case "
    + "when account_id is not null then 'OPERATOR' "
    + "when competent_authority is not null then 'REGULATOR' "
    + "when verification_body_id is not null then 'VERIFIER' "
    + "end as role_type "
    + "from au_authority "
    + "where status in ('TEMP_DISABLED','DISABLED','ACTIVE')")
@Immutable
@Synchronize({"au_authority"})
public class UserRoleType {

    @Id
    private String userId;

    @NotNull
    @Enumerated(EnumType.STRING)
    private RoleType roleType;
}
