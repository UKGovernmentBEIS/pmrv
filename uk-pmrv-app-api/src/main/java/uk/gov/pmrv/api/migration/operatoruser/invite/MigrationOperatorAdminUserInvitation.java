package uk.gov.pmrv.api.migration.operatoruser.invite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityPermission;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.transform.AuthorityMapper;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.transform.PmrvUserMapper;
import uk.gov.pmrv.api.migration.DryRunException;
import uk.gov.pmrv.api.migration.ExecutionMode;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserInvitationService;

/**
 * Service is used to send invitations to users in order to participate with role operator_admin
 * to installation accounts.
 *
 * The expected input to the service consists of semicolon (;) separated rows, where each row consists of the appropriate information
 * that are needed in order to create and send the invitation. Row information should be separated with the pipe (|) delimiter.
 *
 * Example input:
 * {
 *     ids="1|Andy|Murray|Andy.Murray@niea.com|13881|regulator@pmrv.uk; 2|Nick|Cave|Nick.Cave@niea.com|13882|regulator@pmrv.uk;"
 * }
 *
 * Each row should consist of the following 6 information in the specified order :
 * 1)The row id, that is used for logging purposes,
 * 2)The invited user first name,
 * 3)The invited user last name,
 * 4)The invited user email,
 * 5)The account id to which the user will be invited to participate,
 * 6)The email of the user that sends the invitation.
 *
 */
@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationOperatorAdminUserInvitation extends MigrationBaseService {

    private final AccountRepository accountRepository;
    private final UserAuthService authUserService;
    private final AuthorityRepository authorityRepository;
    private final OperatorUserInvitationService operatorUserInvitationService;

    private static final AuthorityMapper authorityMapper = Mappers.getMapper(AuthorityMapper.class);
    private static final PmrvUserMapper pmrvUserMapper = Mappers.getMapper(PmrvUserMapper.class);

    private final Validator validator;

    @Override
    public void migrateDryRun(String input) {
        List<String> failedEntries = sendUserInvitations(input, ExecutionMode.DRY);
        failedEntries.add("CAUTION: Execution in DRY mode leads to no invitations being send.");
        // rollback
        throw new DryRunException(failedEntries);
    }

    @Override
    public List<String> migrate(String input) {
        return sendUserInvitations(input, ExecutionMode.COMMIT);
    }

    public List<String> sendUserInvitations(String input, ExecutionMode executionMode) {
        if(StringUtils.isEmpty(input)) {
           return List.of("Please insert details for at least one user invitation");
        }

        List<String> failedEntries = new ArrayList<>();
        AtomicInteger failedCounter = new AtomicInteger(0);

        List<AccountOperatorAdminUserInvitation> operatorAdminUserInvitations = Arrays.stream(input.split(";"))
            .map(val -> val.split("\\|"))
            .map(parts -> toAccountOperatorAdminUserInvitation(parts, failedEntries))
            .collect(Collectors.toList());

        for (AccountOperatorAdminUserInvitation operatorAdminUserInvitation: operatorAdminUserInvitations) {
            if(operatorAdminUserInvitation != null) {
                inviteOperatorAdminUserToAccount(operatorAdminUserInvitation, failedEntries, failedCounter, executionMode);
            } else {
                failedCounter.incrementAndGet();
            }
        }

        if(executionMode == ExecutionMode.COMMIT) {
            failedEntries.add(failedCounter.get() + "/" + operatorAdminUserInvitations.size() + " invitations sending failed.");
        }
        return failedEntries;
    }

    private AccountOperatorAdminUserInvitation toAccountOperatorAdminUserInvitation(String[] values, List<String> failedEntries) {
        Long rowId = Long.valueOf(values[0].trim());
        if(values.length != 6) {
            failedEntries.add(constructErrorMessage(rowId, "Input data not in expected format"));
            return null;
        }

        AccountOperatorAdminUserInvitation accountOperatorAdminUserInvitation = null;
        try {
            accountOperatorAdminUserInvitation = AccountOperatorAdminUserInvitation.builder()
                .rowId(rowId)
                .firstName(values[1].trim())
                .lastName(values[2].trim())
                .email(values[3].trim())
                .accountId(Long.valueOf(values[4].trim()))
                .inviterEmail(values[5].trim())
                .build();
        } catch (NumberFormatException ex) {
            failedEntries.add(constructErrorMessage(rowId, "Input data not in expected format"));
        }

        return accountOperatorAdminUserInvitation;
    }

    private void inviteOperatorAdminUserToAccount(AccountOperatorAdminUserInvitation userInvitation, List<String> failedEntries,
                                                  AtomicInteger failedCounter, ExecutionMode executionMode) {
        Long rowId = userInvitation.getRowId();
        Long accountId = userInvitation.getAccountId();

        Account account = accountRepository.findById(accountId).orElse(null);
        if (account == null) {
            failedEntries.add(constructErrorMessage(rowId, String.format("No pmrv account found with id %s ", accountId)));
            failedCounter.incrementAndGet();
            return;
        }

        Optional<UserInfoDTO> inviterUserInfoOptional = authUserService.getUserByEmail(userInvitation.getInviterEmail());
        if(inviterUserInfoOptional.isEmpty()) {
            failedEntries.add(constructErrorMessage(rowId, String.format("No pmrv user found with email %s ", userInvitation.getInviterEmail())));
            failedCounter.incrementAndGet();
            return;
        }

        PmrvUser regulatorAdminUser = constructRegulatorAdminUser(inviterUserInfoOptional.get(), account.getCompetentAuthority(), failedEntries, rowId);
        OperatorUserInvitationDTO operatorUserInvitationDTO = constructInvitedOperatorAdminUser(userInvitation, failedEntries);

        if(regulatorAdminUser != null && operatorUserInvitationDTO != null) {
            doInviteUserToAccount(accountId, operatorUserInvitationDTO, regulatorAdminUser, rowId, failedEntries, failedCounter, executionMode);
        } else {
            failedCounter.incrementAndGet();
        }
    }

    private PmrvUser constructRegulatorAdminUser(UserInfoDTO userInfo, CompetentAuthority competentAuthority, List<String> failedEntries, Long rowId) {
        Optional<Authority> regulatorUserAuthorityOptional =
            authorityRepository.findByUserIdAndCompetentAuthority(userInfo.getUserId(), competentAuthority);
        if(regulatorUserAuthorityOptional.isEmpty()) {
            failedEntries.add(constructErrorMessage(rowId, String.format("Provided user %s has not the appropriate authorities", userInfo.getEmail())));
            return null;
        }

        Authority regulatorUserAuthority = regulatorUserAuthorityOptional.get();
        if(!hasPermissionsToEditAccountUsers(regulatorUserAuthority)) {
            failedEntries.add(constructErrorMessage(rowId, String.format("Provided user %s has not the appropriate permissions", userInfo.getEmail())));
            return null;
        }

        AuthorityDTO regulatorAdminAuthorityDTO = authorityMapper.toAuthorityDTO(regulatorUserAuthority);
        PmrvAuthority pmrvAuthority = pmrvUserMapper.toPmrvAuthority(regulatorAdminAuthorityDTO);
        return  PmrvUser.builder()
            .userId(regulatorUserAuthority.getUserId())
            .roleType(RoleType.REGULATOR)
            .authorities(List.of(pmrvAuthority))
            .build();
    }

    private boolean hasPermissionsToEditAccountUsers(Authority authority) {
        return authority.getAuthorityPermissions().stream()
            .map(AuthorityPermission::getPermission)
            .collect(Collectors.toList()).contains(Permission.PERM_ACCOUNT_USERS_EDIT);
    }

    private OperatorUserInvitationDTO constructInvitedOperatorAdminUser(AccountOperatorAdminUserInvitation userInvitation, List<String> failedEntries) {
        OperatorUserInvitationDTO operatorUserInvited =
            OperatorUserInvitationDTO.builder()
                .email(userInvitation.getEmail())
                .firstName(userInvitation.getFirstName())
                .lastName(userInvitation.getLastName())
                .roleCode(AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE)
                .build();

        //validate invited user
        Set<ConstraintViolation<OperatorUserInvitationDTO>> constraintViolations =
            validator.validate(operatorUserInvited);
        if (!constraintViolations.isEmpty()) {
            constraintViolations.forEach(v -> failedEntries.add(constructErrorMessage(userInvitation.getRowId(),
                v.getPropertyPath().iterator().next().getName() + " - " + v.getMessage())));
            return null;
        }

        return operatorUserInvited;
    }

    private void doInviteUserToAccount(Long accountId, OperatorUserInvitationDTO userInvitationDTO, PmrvUser regulatorAdminUser,
                                       Long rowId, List<String> failedEntries, AtomicInteger failedCounter, ExecutionMode executionMode) {
        try {
            switch (executionMode) {
                case COMMIT:
                    operatorUserInvitationService.inviteUserToAccount(accountId, userInvitationDTO, regulatorAdminUser);
                    break;
                case DRY:
                    // mock (async actions + keyclock request cannot be rollbacked)
                    break;
                default:
                    break;
            }
        } catch (Exception ex) {
            failedEntries.add(constructErrorMessage(rowId, String.format("Error during user invitation - %s",  ex.getMessage())));
            failedCounter.incrementAndGet();
        }
    }

    private String constructErrorMessage(Long rowId, String errorMessage) {
        return "Row: " + rowId + " | Error: " + errorMessage;
    }

    @Override
    public String getResource() {
        return "operator-admin-users-invite";
    }
}
