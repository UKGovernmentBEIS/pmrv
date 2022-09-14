package uk.gov.pmrv.api.migration.verifier;

import lombok.RequiredArgsConstructor;

import org.mapstruct.factory.Mappers;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.migration.DryRunException;
import uk.gov.pmrv.api.migration.ExecutionMode;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.verifier.domain.VerifierUserVO;
import uk.gov.pmrv.api.migration.verifier.domain.VerifierUserVOMapper;
import uk.gov.pmrv.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInvitationService;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;

import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@RequiredArgsConstructor
public class MigrationVerifierUserService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final VerificationBodyRepository verificationBodyRepository;
    private final AuthorityRepository authorityRepository;
    private final VerifierUserInvitationService verifierUserInvitationService;
    private final Validator validator;
    private final MigrationVerifierMapper migrationVerifierMapper = Mappers.getMapper(MigrationVerifierMapper.class);

    private static final String SQL = "SELECT r.fldName AS code, c.fldEmailAddress AS email, u.fldUserID AS user_id, " +
            "c.fldName AS firstName, c.fldSurname AS lastName, c.fldTelephoneNumber AS phoneNumber, " +
            "vu.fldVerifierID AS verification_body_id, v.fldName AS verification_body_name, u.fldIsDisabled AS disabled " +
            "FROM tblUser u " +
            "JOIN tblVerifierUser vu ON vu.fldUserID=u.fldUserID " +
            "JOIN tblRole r ON r.fldRoleID=vu.fldRoleID " +
            "JOIN tblContact c ON c.fldContactID=u.fldContactID " +
            "JOIN tblVerifier v ON v.fldVerifierID=vu.fldVerifierID " +
            "WHERE u.fldCategoryID=1 AND u.fldIsDisabled=0 AND v.fldIsDisabled=0";
    private static final String VERIFIER_ADMIN = "Verifier Admin";

    @Override
    public void migrateDryRun(String emails) {
        List<String> errors = doMigrateInit(emails, ExecutionMode.DRY);

        // rollback
        throw new DryRunException(errors);
    }

    @Override
    public List<String> migrate(String emails) {
        return doMigrateInit(emails, ExecutionMode.COMMIT);
    }

    @Override
    public String getResource() {
        return "verifier-users";
    }

    private List<String> doMigrateInit(String emails, ExecutionMode mode) {
        // TODO Regulator to be used
        Authority regulator = authorityRepository.findByCompetentAuthority(CompetentAuthority.ENGLAND)
                .stream().filter(Objects::nonNull).findFirst().orElseThrow(() -> new RuntimeException("No Regulator User exist" ));
        PmrvUser regulatorUser = PmrvUser.builder().userId(regulator.getUserId()).roleType(RoleType.REGULATOR).build();

        // Get VBs
        List<VerificationBody> verificationBodies = verificationBodyRepository.findAll();

        // Create sql statement
        String sql = ObjectUtils.isEmpty(emails) ? SQL :
                String.format(SQL + " AND c.fldEmailAddress IN (%s)", Arrays.stream(emails.split(","))
                .filter(Objects::nonNull)
                .map(email -> "'" + email.trim() + "'").collect(Collectors.joining(",")));

        // Get verifiers from ETSWAP
        Map<String, List<VerifierUserVO>> verifierUserVOMap = migrationJdbcTemplate.query(sql, new VerifierUserVOMapper())
                .stream().collect(Collectors.groupingBy(VerifierUserVO::getVerificationBodyName));

        return doMigrate(regulatorUser,verificationBodies, verifierUserVOMap, mode);
    }

    /** Migrate all users. */
    private List<String> doMigrate(PmrvUser regulatorUser, List<VerificationBody> verificationBodies,
                                   Map<String, List<VerifierUserVO>> verifierUserVOMap, ExecutionMode mode) {
        // All failed entries
        List<String> failedEntries = new ArrayList<>();

        verifierUserVOMap.forEach((vbName, verifierUserVOs) ->
                verificationBodies.stream().filter(vb -> vb.getName().equalsIgnoreCase(vbName)).findFirst().ifPresentOrElse(verificationBody ->
                    // Find admin for VB
                    authorityRepository.findAllByVerificationBodyId(verificationBody.getId()).stream()
                            .filter(authority -> authority.getCode().equals(AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE))
                            .findFirst().ifPresentOrElse(authority ->
                                    // Use verifier admin from PMRV
                                    verifierUserVOs.stream().map(migrationVerifierMapper::toVerifierUserInvitationDTO).forEach(v ->
                                            sendVerifierUserInvitation(mode, failedEntries, verificationBody.getId(), authority.getUserId(), v))
                                    , () ->
                                    // If no Verifier admin exists find the one from provided emails
                                    verifierUserVOs.stream()
                                            .filter(verifierUserVO -> verifierUserVO.getRoleCode().trim().equals(VERIFIER_ADMIN))
                                            .findFirst().map(migrationVerifierMapper::toAdminVerifierUserInvitationDTO).ifPresentOrElse(admin -> {

                                                // Send invitation if verifier admin exists in provided emails
                                                List<VerifierUserInvitationDTO> verifiersToBeInvited = verifierUserVOs
                                                        .stream().filter(v -> !v.getEmail().equals(admin.getEmail()))
                                                        .map(migrationVerifierMapper::toVerifierUserInvitationDTO).collect(Collectors.toList());

                                                sendInvitations(failedEntries, verificationBody.getId(), admin, verifiersToBeInvited, regulatorUser, mode);

                                                }, () ->
                                                    failedEntries.addAll(verifierUserVOs.stream().map(v -> constructErrorMessage(v.getEmail(), verificationBody.getId(),
                                                            "No verifier admin")).collect(Collectors.toList())))
                            )
                , () -> failedEntries.add("Verification Body not exists: " + vbName)));

        return failedEntries;
    }

    private void sendInvitations(List<String> failedEntries, Long verificationBodyId, AdminVerifierUserInvitationDTO adminVerifier,
                                 List<VerifierUserInvitationDTO> verifiers, PmrvUser regulatorUser, ExecutionMode mode) {

        List<String> adminValidationErrors = validator.validate(adminVerifier).stream()
                .map(constraint -> constructErrorMessage(adminVerifier.getEmail(), verificationBodyId,
                        constraint.getPropertyPath().iterator().next().getName() + ": " + constraint.getMessage()))
                .collect(Collectors.toList());

        if(adminValidationErrors.isEmpty()){
            // Send admin invitation
            Authority verifierAdminAuthority = sendAdminVerifierUserInvitation(mode, regulatorUser,
                    verificationBodyId, adminVerifier);

            // Send invitation for verifier user
            Authority finalVerifierAdminAuthority = verifierAdminAuthority;
            verifiers.forEach(v ->
                    sendVerifierUserInvitation(mode, failedEntries, verificationBodyId, finalVerifierAdminAuthority.getUserId(), v));
        }
        else{
            adminValidationErrors.addAll(verifiers.stream().map(v -> constructErrorMessage(v.getEmail(), verificationBodyId,
                    "Not invited by " + adminVerifier.getEmail())).collect(Collectors.toList()));
            failedEntries.addAll(adminValidationErrors);
        }
    }

    private Authority sendAdminVerifierUserInvitation(ExecutionMode mode, PmrvUser invitedFrom, Long verificationBodyId,
                                                      AdminVerifierUserInvitationDTO adminVerifierUserInvitation) {
        if (mode.equals(ExecutionMode.COMMIT)){
            verifierUserInvitationService.inviteVerifierAdminUser(invitedFrom, adminVerifierUserInvitation, verificationBodyId);

            return authorityRepository.findAllByVerificationBodyId(verificationBodyId).stream()
                    .filter(authority -> authority.getCode().equals(AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE))
                    .findFirst().orElseThrow();
        }

        return new Authority();
    }

    private void sendVerifierUserInvitation(ExecutionMode mode, List<String> failedEntries, Long verificationBodyId, String invitedFrom,
                                            VerifierUserInvitationDTO verifierUserInvitation) {

        List<String> validationErrors = validator.validate(verifierUserInvitation).stream()
                .map(constraint -> constructErrorMessage(verifierUserInvitation.getEmail(), verificationBodyId,
                        constraint.getPropertyPath().iterator().next().getName() + ": " + constraint.getMessage()))
                .collect(Collectors.toList());

        if(validationErrors.isEmpty() && mode.equals(ExecutionMode.COMMIT)){
            // Send invitation from admin verifier
            PmrvUser pmrvUser = PmrvUser.builder().userId(invitedFrom)
                    .authorities(List.of(PmrvAuthority.builder().verificationBodyId(verificationBodyId).build())).build();
            verifierUserInvitationService.inviteVerifierUser(pmrvUser, verifierUserInvitation);
        }

        failedEntries.addAll(validationErrors);
    }

    private String constructErrorMessage(String email, Long vbId, String errorMessage) {
        return "Email: " + email + " | VB: " + vbId + " | Error: " + errorMessage;
    }
}
