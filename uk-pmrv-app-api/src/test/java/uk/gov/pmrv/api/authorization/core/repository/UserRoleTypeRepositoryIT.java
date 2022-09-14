package uk.gov.pmrv.api.authorization.core.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.authorization.core.domain.UserRoleType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class UserRoleTypeRepositoryIT extends AbstractContainerBaseTest {
    private static final String USER_OPERATOR_ID = "user_operator_id";
    private static final String USER_REGULATOR_ID_A = "user_regulator_id_a";
    private static final String USER_REGULATOR_ID_B = "user_regulator_id_b";

    @Autowired
    private UserRoleTypeRepository userRoleTypeRepository;

    @Sql(statements = {
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (1, 'user_operator_id', 'operator_admin', 'ACTIVE', 1, null, NOW(), 'user_operator_id')",
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (2, 'user_operator_id', 'operator_user', 'ACTIVE', 2, null, NOW(), 'user_operator_id')"
    })
    @Test
    void findById_active_user1() {
        Optional<UserRoleType> optionalUserRole = userRoleTypeRepository.findById(USER_OPERATOR_ID);

        assertTrue(optionalUserRole.isPresent());
        assertEquals(RoleType.OPERATOR, optionalUserRole.get().getRoleType());
        assertEquals(USER_OPERATOR_ID, optionalUserRole.get().getUserId());
    }

    @Sql(statements = {
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (3, 'user_regulator_id_a', 'regulator_user', 'ACTIVE', null, 'ENGLAND', NOW(), 'user_regulator_id_a')",
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (4, 'user_regulator_id_b', 'regulator_user', 'DISABLED', null, 'WALES', NOW(), 'user_regulator_id_b')"
    })
    @Test
    void findById_active_user2() {
        Optional<UserRoleType> optionalUserRole = userRoleTypeRepository.findById(USER_REGULATOR_ID_A);

        assertTrue(optionalUserRole.isPresent());
        assertEquals(RoleType.REGULATOR, optionalUserRole.get().getRoleType());
        assertEquals(USER_REGULATOR_ID_A, optionalUserRole.get().getUserId());
    }

    @Sql(statements = {
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (3, 'user_regulator_id_a', 'regulator_user', 'ACTIVE', null, 'ENGLAND', NOW(), 'user_regulator_id_a')",
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (4, 'user_regulator_id_b', 'regulator_user', 'DISABLED', null, 'WALES', NOW(), 'user_regulator_id_b')"
    })
    @Test
    void findById_disabled_user() {
        Optional<UserRoleType> optionalUserRole = userRoleTypeRepository.findById(USER_REGULATOR_ID_B);

        assertTrue(optionalUserRole.isPresent());
        assertEquals(RoleType.REGULATOR, optionalUserRole.get().getRoleType());
        assertEquals(USER_REGULATOR_ID_B, optionalUserRole.get().getUserId());
    }

    @Sql(statements = {
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (3, 'user_regulator_id_a', 'regulator_user', 'PENDING', null, 'ENGLAND', NOW(), 'user_regulator_id_a')",
        "INSERT INTO au_authority (id, user_id, code, status, account_id, competent_authority, creation_date, created_by) VALUES (4, 'user_regulator_id_b', 'regulator_user', 'DISABLED', null, 'WALES', NOW(), 'user_regulator_id_b')"
    })
    @Test
    void findById_pending_user() {
        Optional<UserRoleType> optionalUserRole = userRoleTypeRepository.findById(USER_REGULATOR_ID_A);

        assertTrue(optionalUserRole.isEmpty());
    }

    @Sql(statements = {
        "INSERT INTO au_authority (id, user_id, code, status, creation_date, created_by, verification_body_id) VALUES (1, 'verifier_user_id', 'verifier_user', 'ACTIVE', NOW(), 'verifier_user_id', 1)"
    })
    @Test
    void findById_active_user_with_verifier_role() {
        final String verifierUserId= "verifier_user_id";
        Optional<UserRoleType> optionalUserRole = userRoleTypeRepository.findById(verifierUserId);

        assertTrue(optionalUserRole.isPresent());
        assertEquals(RoleType.VERIFIER, optionalUserRole.get().getRoleType());
        assertEquals(verifierUserId, optionalUserRole.get().getUserId());
    }

}