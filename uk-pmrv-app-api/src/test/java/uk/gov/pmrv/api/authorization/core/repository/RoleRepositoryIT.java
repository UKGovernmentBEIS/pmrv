package uk.gov.pmrv.api.authorization.core.repository;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.authorization.core.domain.RolePermission;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class RoleRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    RoleRepository roleRepository;

    @Autowired
    EntityManager entityManager;

    @Test
    void findByType_no_roles() {
        createRole("Code 1", "code1", OPERATOR, PERM_INSTALLATION_ACCOUNT_OPENING_ARCHIVE_VIEW_TASK);
        createRole("Code 2", "code2", OPERATOR,
            PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK);
        List<Role> roles = roleRepository.findByType(REGULATOR);
        assertThat(roles).isEmpty();
    }

    @Test
    void findByType() {
        Role role1 = createRole("Code 1", "code1", REGULATOR,
            PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_VIEW_TASK);
        Role role2 = createRole("Code 2", "code2", REGULATOR,
            PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK);
        List<Role> roles = roleRepository.findByType(REGULATOR);
        assertThat(List.of(role1, role2)).hasSameElementsAs(roles);
    }

    private Role createRole(String name, String code, RoleType roleType, Permission... permissions) {
        Role role = Role.builder()
            .name(name)
            .code(code)
            .type(roleType)
            .build();

        for (Permission permission : permissions) {
            role.addPermission(
                RolePermission.builder()
                    .permission(permission).build());
        }

        entityManager.persist(role);

        return role;
    }

}
