package uk.gov.pmrv.api.migration.regulatoruser;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.RolePermissionsDTO;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.repository.RoleRepository;
import uk.gov.pmrv.api.authorization.core.transform.AuthorityMapper;
import uk.gov.pmrv.api.authorization.core.transform.RoleMapper;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorRolePermissionsDTO;
import uk.gov.pmrv.api.authorization.regulator.transform.RegulatorRoleMapper;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.common.transform.PmrvUserMapper;
import uk.gov.pmrv.api.migration.DryRunException;
import uk.gov.pmrv.api.migration.ExecutionMode;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationConstants;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.MigrationHelper;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorInvitedUserDetailsDTO;
import uk.gov.pmrv.api.user.regulator.service.RegulatorUserInvitationService;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Migration steps:
 * 1. Initially migrate the member state admin users. These first 5 users (one for each CA) will be the inviters of the rest users.
 * 2. Migrate the rest of the users.
 * Notes:
 * The initial member state admin users will be invited by a dummy 'migration_process' user.
 * The initial member state admin users should be accepted manually through keycloak.
 * The rest of the users will be accepted by the initial member state admin users.
 * <p>
 * Issues:
 * 1. ETSWAP_SUPPORT_CA@sfwltd.co.uk is linked to multiple users
 * 2. Missing firstName, surname, phones, jobTitles
 * 3. Exclude VB_MANAGE permission from CA super user
 * 4. Map from etswap roles -> pmrv roles
 * 5. Member State Admin users are invalid due to missing firstName, surname etc. hence migration will fail
 */
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationRegulatorUserService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final RoleRepository roleRepository;
    private final RegulatorUserInvitationService regulatorUserInvitationService;
    private final AuthorityRepository authorityRepository;
    private final Validator validator;

    private static final AuthorityMapper authorityMapper = Mappers.getMapper(AuthorityMapper.class);
    private static final PmrvUserMapper pmrvUserMapper = Mappers.getMapper(PmrvUserMapper.class);
    private static final RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);
    private static final RegulatorRoleMapper regulatorRoleMapper = Mappers.getMapper(RegulatorRoleMapper.class);

    private static final String QUERY_BASE =
        "select "
            + " u.fldUserID as userId, \r\n"
            + " cont.fldEmailAddress as email, \r\n"
            + " cont.fldName as firstName, \r\n"
            + " cont.fldSurname as lastName, \r\n"
            + " cont.fldJobTitle as jobTitle, \r\n"
            + " cont.fldTelephoneNumber as phoneNumber, \r\n"
            + " role.fldName as roleName, \r\n"
            + " ca.fldName as compAuth \r\n"
            + "from tblUser u \r\n"
            + "inner join tlkpCategory cat on cat.fldCategoryID = u.fldCategoryID \r\n"
            + "inner join tblCompetentAuthorityUser au on au.fldUserID = u.fldUserID \r\n"
            + "inner join tblCompetentAuthority ca on ca.fldCompetentAuthorityID = au.fldCompetentAuthorityID \r\n"
            + "inner join tblContact cont on cont.fldContactID = u.fldContactID \r\n"
            + "inner join tblRole role on role.fldRoleID = au.fldRoleID \r\n"
            + "where \r\n"
            + "cat.fldName = 'CA' \r\n "
            + "and u.fldIsDisabled = 0 \r\n";

    @Override
    public String getResource() {
        return "regulator-users";
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

        List<EtsRegulatorUser> etsRegulatorUsers =
            migrationJdbcTemplate.query(queryBuilder.toString(), new EtsRegulatorUserRowMapper(), emails.toArray());

        return migrateUsers(etsRegulatorUsers, mode, failedEntries);
    }

    private List<String> migrateAll(ExecutionMode mode) {
        List<String> failedEntries = new ArrayList<>();

        StringBuilder queryBuilder = new StringBuilder(QUERY_BASE);
        List<EtsRegulatorUser> etsRegulatorUsers =
            migrationJdbcTemplate.query(queryBuilder.toString(), new EtsRegulatorUserRowMapper());

        return migrateUsers(etsRegulatorUsers, mode, failedEntries);
    }

    private List<String> migrateUsers(List<EtsRegulatorUser> etsRegulatorUsers, ExecutionMode mode,
                                      List<String> failedEntries) {
        Map<CompetentAuthority, PmrvUser> invitersPerCA = new HashMap<>();

        List<EtsRegulatorUser> etsSuperAdminRegulators = etsRegulatorUsers.stream()
            .filter(u -> MigrationHelper.getEtsRoleNameByPmrvRole(AuthorityConstants.PMRV_SUPER_USER_ROLE_CODE)
                .equals(u.getRoleName()))
            .collect(Collectors.toList());

        etsSuperAdminRegulators.forEach(etsRegulatorUser -> migrateUser(etsRegulatorUser, invitersPerCA, mode, failedEntries));


        //migrate the rest of regulators
        etsRegulatorUsers.removeAll(etsSuperAdminRegulators);
        etsRegulatorUsers.forEach(etsRegulatorUser -> migrateUser(etsRegulatorUser, invitersPerCA, mode, failedEntries));

        return failedEntries;
    }

    private void migrateUser(EtsRegulatorUser etsRegulatorUser,
                             Map<CompetentAuthority, PmrvUser> invitersPerCA,
                             ExecutionMode mode,
                             List<String> failedEntries) {
        PmrvUser inviter = resolveInviter(etsRegulatorUser, invitersPerCA, failedEntries);
        if (inviter != null) {
            doMigrateUser(etsRegulatorUser, inviter, mode, failedEntries);
        }
    }

    private PmrvUser resolveInviter(EtsRegulatorUser etsRegulatorUser, Map<CompetentAuthority, PmrvUser> invitersPerCA,
                                    List<String> failedEntries) {
        CompetentAuthority compAuth = MigrationHelper.resolveCompAuth(etsRegulatorUser.getCompAuth());
        if (compAuth == null) {
            failedEntries.add(constructErrorMessage(etsRegulatorUser.getUserId(), etsRegulatorUser.getEmail(),
                "PMRV CA cannot be resolved for ETS CA: " + etsRegulatorUser.getCompAuth()));
            return null;
        }

        //firstly check the cached map
        PmrvUser inviter = invitersPerCA.get(compAuth);
        if (inviter != null) {
            return inviter;
        }

        // check the pmrv database if super admin exists
        List<Authority> superRegulatorAuthorities =
            authorityRepository.findByCompetentAuthorityAndPmrvSuperUserRolePermissions(compAuth);
        if (!superRegulatorAuthorities.isEmpty()) {
            PmrvUser pmrvCASuperUser = getPmrvSuperUser(superRegulatorAuthorities.get(0));
            invitersPerCA.put(compAuth, pmrvCASuperUser);
            return pmrvCASuperUser;
        }

        // neither cached inviter for CA exist, nor PMRV super user exists. 
        // if the current user to be migrated is a pmrv super user then return the default 'migration_process' as the inviter.
        // if no pmrv super user, cannot be migrated, hence no inviter is returned 
        final String etsPmrvSuperUserRole =
            MigrationHelper.getEtsRoleNameByPmrvRole(AuthorityConstants.PMRV_SUPER_USER_ROLE_CODE);
        if (!etsPmrvSuperUserRole.equals(etsRegulatorUser.getRoleName())) {
            failedEntries.add(constructErrorMessage(etsRegulatorUser.getUserId(), etsRegulatorUser.getEmail(),
                "Pmrv super admin regulator does not exist for CA: " + compAuth));
            return null;
        } else {
            return getMigrationProcessUser(compAuth);
        }
    }

    private void doMigrateUser(EtsRegulatorUser etsRegulatorUser, PmrvUser inviter, ExecutionMode mode,
                               List<String> failedEntries) {
        String pmrvRoleCode = MigrationHelper.resolveRoleCode(etsRegulatorUser.getRoleName());
        if (pmrvRoleCode == null) {
            failedEntries.add(constructErrorMessage(etsRegulatorUser.getUserId(), etsRegulatorUser.getEmail(),
                "PMRV role cannot be resolved for ETS role: " + etsRegulatorUser.getRoleName()));
            return;
        }

        //create invited user
        RegulatorInvitedUserDTO invitedUser =
            RegulatorInvitedUserDTO.builder()
                .userDetails(RegulatorInvitedUserDetailsDTO.builder()
                    .email(etsRegulatorUser.getEmail())
                    .firstName(etsRegulatorUser.getFirstName())
                    .lastName(etsRegulatorUser.getLastName())
                    .jobTitle(etsRegulatorUser.getJobTitle())
                    .phoneNumber(etsRegulatorUser.getPhoneNumber())
                    .build())
                .permissions(getRolePermissionsDTOByCode(pmrvRoleCode).getRolePermissions())
                .build();

        //validate invited user
        Set<ConstraintViolation<RegulatorInvitedUserDTO>> constraintViolations = validator.validate(invitedUser);
        if (!constraintViolations.isEmpty()) {
            constraintViolations.forEach(v -> failedEntries.add(constructErrorMessage(etsRegulatorUser.getUserId(), etsRegulatorUser.getEmail(),
                v.getPropertyPath().iterator().next().getName() + ": " + v.getMessage())));
            return;
        }

        //invite
        try {
            switch (mode) {
                case COMMIT:
                    regulatorUserInvitationService.inviteRegulatorUser(invitedUser, null, inviter);
                    break;
                case DRY:
                    // mock (async actions + keyclock request cannot be rollbacked)
                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            failedEntries.add(
                constructErrorMessage(etsRegulatorUser.getUserId(), etsRegulatorUser.getEmail(), e.getMessage()));
        }
    }

    private PmrvUser getMigrationProcessUser(CompetentAuthority compAuth) {
        PmrvUser authUser = new PmrvUser();
        authUser.setUserId(MigrationConstants.MIGRATION_PROCESS_USER);
        authUser.setRoleType(RoleType.REGULATOR);
        authUser.setAuthorities(List.of(PmrvAuthority.builder().competentAuthority(compAuth).build()));
        return authUser;
    }

    private PmrvUser getPmrvSuperUser(Authority superRegulatorAuthority) {
        AuthorityDTO superRegulatorAuthorityDTO = authorityMapper.toAuthorityDTO(superRegulatorAuthority);
        PmrvAuthority pmrvAuthority = pmrvUserMapper.toPmrvAuthority(superRegulatorAuthorityDTO);

        PmrvUser authUser = new PmrvUser();
        authUser.setUserId(superRegulatorAuthority.getUserId());
        authUser.setRoleType(RoleType.REGULATOR);
        authUser.setAuthorities(List.of(pmrvAuthority));
        return authUser;
    }

    public RegulatorRolePermissionsDTO getRolePermissionsDTOByCode(String roleCode) {
        Role role = roleRepository.findByCode(roleCode)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        RolePermissionsDTO rolePermissionsDTO = roleMapper.toRolePermissionsDTO(role);
        return regulatorRoleMapper.toRolePermissionsDTO(rolePermissionsDTO);
    }

    private String constructErrorMessage(String id, String email, String errorMessage) {
        return "Id: " + id + " | Email: " + email + " | Error: " + errorMessage;
    }
}
