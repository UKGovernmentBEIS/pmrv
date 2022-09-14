package uk.gov.pmrv.api.migration.operatoruser;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.transform.AuthorityMapper;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.transform.PmrvUserMapper;
import uk.gov.pmrv.api.migration.DryRunException;
import uk.gov.pmrv.api.migration.ExecutionMode;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.MigrationHelper;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserInvitationService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Assumptions:
 * - Accounts have already been migrated
 * - Regulator users have already been migrated
 * <p>
 * <p>
 * Migration steps:
 * 1. migrate the operator admins first, as follows:
 * a) if no operator admin exists for the account, the current user will be invited by the CA regulator super user.
 * b) if operator admin found for the account, the current user will be invited by that admin user.
 * 2. migrate the rest operator users
 * <p>
 * <p>
 * ISSUES:
 * - Operator editor role: how to map to pmrv role?
 */
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationOperatorUserService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final AccountRepository accountRepository;
    private final OperatorAuthorityService operatorAuthorityService;
    private final OperatorUserInvitationService operatorUserInvitationService;
    private final AuthorityRepository authorityRepository;

    private static final AuthorityMapper authorityMapper = Mappers.getMapper(AuthorityMapper.class);
    private static final PmrvUserMapper pmrvUserMapper = Mappers.getMapper(PmrvUserMapper.class);

    private final Validator validator;

    private static final String QUERY_BASE =
        "select cont.fldEmailAddress as email, \r\n"
            + "cont.fldName as firstName, \r\n"
            + "cont.fldSurname as lastName, \r\n"
            + "role.fldName as roleName,\r\n"
            + "ca.fldName as compAuth,\r\n"
            + "em.fldName as emitterName, \r\n"
            + "em.fldEmitterID as emitterId\r\n"
            + "from tblEmitterUser emu\r\n"
            + "inner join tblEmitter em on emu.fldEmitterID = em.fldEmitterID\r\n"
            + "inner join tblUser u on emu.fldUserID = u.fldUserID\r\n"
            + "inner join tblRole role on role.fldRoleID = emu.fldRoleID\r\n"
            + "inner join tblContact cont on cont.fldContactID = u.fldContactID\r\n"
            + "inner join tblCompetentAuthority ca on ca.fldCompetentAuthorityID = em.fldCompetentAuthorityID\r\n"
            + "where \r\n"
            + "emu.fldIsDisabled = 0 \r\n";

    @Override
    public String getResource() {
        return "operator-users";
    }

    @Override
    public void migrateDryRun(String emails) {
        List<String> failedEntries;
        if (!StringUtils.isBlank(emails)) {
            failedEntries = migrateEmails(Arrays.asList(emails.split(",")), ExecutionMode.DRY);
        } else {
            failedEntries = migrateAll(ExecutionMode.DRY);
        }

        // rollback
        throw new DryRunException(failedEntries);
    }

    @Override
    public List<String> migrate(String emails) {
        if (!StringUtils.isBlank(emails)) {
            return migrateEmails(Arrays.asList(emails.split(",")), ExecutionMode.COMMIT);
        } else {
            return migrateAll(ExecutionMode.COMMIT);
        }
    }

    private List<String> migrateEmails(List<String> emails, ExecutionMode mode) {
        List<String> failedEntries = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        String inEmailsSql = String.join(",", Collections.nCopies(emails.size(), "?"));
        queryBuilder.append(String.format("and cont.fldEmailAddress in (%s)", inEmailsSql));

        List<EtsAccountOperatorUser> etsAccountOperatorUsers =
            migrationJdbcTemplate.query(queryBuilder.toString(), new EtsAccountOperatorUserRowMapper(),
                emails.toArray());

        return migrateUsers(etsAccountOperatorUsers, mode, failedEntries);
    }

    private List<String> migrateAll(ExecutionMode mode) {
        List<String> failedEntries = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        List<EtsAccountOperatorUser> etsAccountOperatorUsers =
            migrationJdbcTemplate.query(queryBuilder.toString(), new EtsAccountOperatorUserRowMapper());

        return migrateUsers(etsAccountOperatorUsers, mode, failedEntries);
    }

    private List<String> migrateUsers(List<EtsAccountOperatorUser> etsAccountOperatorUsers, ExecutionMode mode,
                                      List<String> failedEntries) {
        //migrate the operator admin first
        List<EtsAccountOperatorUser> etsOperatorAdmins = etsAccountOperatorUsers.stream()
            .filter(u -> AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE.equals(
                MigrationHelper.resolveRoleCode(u.getRoleName())))
            .collect(Collectors.toList());
        for (EtsAccountOperatorUser etsOperatorAdmin : etsOperatorAdmins) {
            migrateUser(etsOperatorAdmin, mode, failedEntries);
        }

        //migrate the rest of operators
        etsAccountOperatorUsers.removeAll(etsOperatorAdmins);
        for (EtsAccountOperatorUser etsAccountOperatorUser : etsAccountOperatorUsers) {
            migrateUser(etsAccountOperatorUser, mode, failedEntries);
        }

        return failedEntries;
    }

    private void migrateUser(EtsAccountOperatorUser etsAccountOperatorUser, ExecutionMode mode,
                             List<String> failedEntries) {
        String accountEtsId = etsAccountOperatorUser.getEmitterId();

        //get pmrv account by ets id 
        Account account = accountRepository.findByMigratedAccountId(accountEtsId).orElse(null);
        if (account == null) {
            failedEntries.add("No pmrv account found for emitter id: " + accountEtsId);
            return;
        }

        //get account operator admin
        Optional<String> accountOperatorAdmin = findAccountOperatorAdminUser(account);

        if (accountOperatorAdmin.isEmpty()) {
            String pmrvRoleCode = MigrationHelper.resolveRoleCode(etsAccountOperatorUser.getRoleName());
            if (!pmrvRoleCode.equals(AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE)) {
                failedEntries.add(String.format(
                    "No operator admin user exists for account %s and the current user is not an operator admin",
                    account.getName()));
            } else {
                migrateOperatorAdminInvitedByCompAuthRegulatorSuperUser(etsAccountOperatorUser, account, mode,
                    failedEntries);
            }
        } else {
            // invite the current user by the account operator admin
            PmrvUser accountOperatorAdminPmrvUser =
                constructAccountOperatorAdminUser(accountOperatorAdmin.get(), account.getId());
            doMigrate(etsAccountOperatorUser, accountOperatorAdminPmrvUser, account, mode, failedEntries);
        }
    }

    private void migrateOperatorAdminInvitedByCompAuthRegulatorSuperUser(EtsAccountOperatorUser operatorAdminEtsUser,
                                                                         Account account, ExecutionMode mode,
                                                                         List<String> failedEntries) {
        // invite the current user on invited by the regulator admin
        String etsCompAuth = operatorAdminEtsUser.getCompAuth();
        CompetentAuthority pmrvCompAuth = MigrationHelper.resolveCompAuth(etsCompAuth);
        if (pmrvCompAuth == null) {
            failedEntries.add("PMRV CA cannot be resolved for ETS CA: " + etsCompAuth);
            return;
        }
        List<Authority> regulatorAdmins =
            authorityRepository.findByCompetentAuthorityAndPmrvSuperUserRolePermissions(pmrvCompAuth);
        if (regulatorAdmins.isEmpty()) {
            failedEntries.add("No PMRV regulator super user found CA: " + pmrvCompAuth);
            return;
        }
        PmrvUser regulatorAdmin = constructRegulatorAdminUser(regulatorAdmins.get(0));
        doMigrate(operatorAdminEtsUser, regulatorAdmin, account, mode, failedEntries);
    }

    private void doMigrate(EtsAccountOperatorUser etsAccountOperatorUser, PmrvUser inviter, Account account,
                           ExecutionMode mode, List<String> failedEntries) {
        OperatorUserInvitationDTO operatorUserInvited =
            OperatorUserInvitationDTO.builder()
                .email(etsAccountOperatorUser.getEmail())
                .firstName(etsAccountOperatorUser.getFirstName())
                .lastName(etsAccountOperatorUser.getLastName())
                .roleCode(MigrationHelper.resolveRoleCode(etsAccountOperatorUser.getRoleName()))
                .build();

        //validate invited user
        Set<ConstraintViolation<OperatorUserInvitationDTO>> constraintViolations =
            validator.validate(operatorUserInvited);
        if (!constraintViolations.isEmpty()) {
            constraintViolations.forEach(v -> failedEntries.add(constructErrorMessage(etsAccountOperatorUser.getEmail(),
                v.getPropertyPath().iterator().next().getName() + ": " + v.getMessage())));
            return;
        }

        //invite
        try {
            switch (mode) {
                case COMMIT:
                    operatorUserInvitationService.inviteUserToAccount(account.getId(), operatorUserInvited, inviter);
                    break;
                case DRY:
                    // mock (async actions + keyclock request cannot be rollbacked)
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            failedEntries.add(
                String.format("Error during invite for email: %s. Error: %s", operatorUserInvited.getEmail(),
                    e.getMessage()));
        }
    }

    private PmrvUser constructRegulatorAdminUser(Authority regulatorAdminAuthority) {
        AuthorityDTO regulatorAdminAuthorityDTO = authorityMapper.toAuthorityDTO(regulatorAdminAuthority);
        PmrvAuthority pmrvAuthority = pmrvUserMapper.toPmrvAuthority(regulatorAdminAuthorityDTO);
        PmrvUser authUser = new PmrvUser();
        authUser.setUserId(regulatorAdminAuthority.getUserId());
        authUser.setRoleType(RoleType.REGULATOR);
        authUser.setAuthorities(List.of(pmrvAuthority));
        return authUser;
    }

    private PmrvUser constructAccountOperatorAdminUser(String operatorAdminUser, Long accountId) {
        Authority authority = authorityRepository.findByUserIdAndAccountId(operatorAdminUser, accountId).get();
        AuthorityDTO authorityDTO = authorityMapper.toAuthorityDTO(authority);
        PmrvAuthority pmrvAuthority = pmrvUserMapper.toPmrvAuthority(authorityDTO);
        PmrvUser authUser = new PmrvUser();
        authUser.setUserId(authority.getUserId());
        authUser.setRoleType(RoleType.OPERATOR);
        authUser.setAuthorities(List.of(pmrvAuthority));
        return authUser;
    }

    private Optional<String> findAccountOperatorAdminUser(Account account) {
        return operatorAuthorityService
            .findActiveOperatorAdminUsersByAccount(account.getId())
            .stream()
            .sorted()
            .findFirst();
    }

    private String constructErrorMessage(String email, String errorMessage) {
        return "User Email: " + email + " | Error: " + errorMessage;
    }

}
