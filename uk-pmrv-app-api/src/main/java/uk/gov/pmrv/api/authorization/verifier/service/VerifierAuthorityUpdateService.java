package uk.gov.pmrv.api.authorization.verifier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Role;
import uk.gov.pmrv.api.authorization.verifier.domain.VerifierAuthorityUpdateDTO;
import uk.gov.pmrv.api.authorization.verifier.event.VerifierUserDisabledEvent;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.repository.RoleRepository;
import uk.gov.pmrv.api.authorization.core.service.AuthorityAssignmentService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.verifier.service.VerifierAdminCreationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@Log4j2
@Service
@RequiredArgsConstructor
public class VerifierAuthorityUpdateService {

    private final List<VerifierAuthorityUpdateValidator> verifierAuthorityUpdateValidators;
	private final AuthorityAssignmentService authorityAssignmentService;
	private final AuthorityRepository authorityRepository;
    private final RoleRepository roleRepository;
    private final ApplicationEventPublisher eventPublisher;

	@Transactional
    public List<String> updateVerifierAuthorities(List<VerifierAuthorityUpdateDTO> verifiersUpdate, Long verificationBodyId) {
        // Validate
        verifierAuthorityUpdateValidators.forEach(service ->
                service.validateUpdate(verifiersUpdate, verificationBodyId));

        // Update authority
        List<String> activatedVerifiers = new ArrayList<>();
        verifiersUpdate.forEach(verifier ->
                Optional.ofNullable(updateVerifierAuthority(verifier, verificationBodyId))
                        .ifPresent(activatedVerifiers::add)
        );

        return activatedVerifiers;
    }

    public void updateTemporaryStatusByVerificationBodyIds(Set<Long> verificationBodyIds) {
        List<Authority> authorities = authorityRepository.findAllByVerificationBodyIdInAndStatusIn(verificationBodyIds,
                Set.of(AuthorityStatus.TEMP_DISABLED, AuthorityStatus.TEMP_DISABLED_PENDING));

        authorities.stream().filter(authority -> AuthorityStatus.TEMP_DISABLED.equals(authority.getStatus()))
                .forEach(authority -> authority.setStatus(AuthorityStatus.ACTIVE));

        authorities.stream().filter(authority -> AuthorityStatus.TEMP_DISABLED_PENDING.equals(authority.getStatus()))
                .forEach(authority -> authority.setStatus(AuthorityStatus.PENDING));

        authorityRepository.saveAll(authorities);
    }

    public void updateStatusToTemporaryByVerificationBodyIds(Set<Long> verificationBodyIds) {
        List<Authority> authorities = authorityRepository.findAllByVerificationBodyIdInAndStatusIn(verificationBodyIds,
                Set.of(AuthorityStatus.ACTIVE, AuthorityStatus.PENDING));

        authorities.stream().filter(authority -> AuthorityStatus.ACTIVE.equals(authority.getStatus()))
                .forEach(authority -> authority.setStatus(AuthorityStatus.TEMP_DISABLED));

        authorities.stream().filter(authority -> AuthorityStatus.PENDING.equals(authority.getStatus()))
                .forEach(authority -> authority.setStatus(AuthorityStatus.TEMP_DISABLED_PENDING));

        authorityRepository.saveAll(authorities);
    }

    private String updateVerifierAuthority(VerifierAuthorityUpdateDTO verifierUpdate, Long verificationBodyId) {
        Optional<Authority> authorityOptional =  authorityRepository.findByUserIdAndVerificationBodyId(verifierUpdate.getUserId(), verificationBodyId);

        if(authorityOptional.isEmpty()) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY, verifierUpdate.getUserId());
        }

        Authority authority = authorityOptional.get();
        AuthorityStatus previousStatus = authority.getStatus();

        // Update role permissions
        String newRoleCode = verifierUpdate.getRoleCode();
        if (!newRoleCode.equals(authority.getCode())) {
            Optional<Role> newRoleOptional = roleRepository.findByCode(newRoleCode);
            if (newRoleOptional.isEmpty()) {
                log.error("Role not found for code: " + newRoleCode);
            } else {
                authority = authorityAssignmentService.updateAuthorityWithNewRole(authority, newRoleOptional.get());
            }
        }
        // Update status
        authority.setStatus(verifierUpdate.getAuthorityStatus());
        authorityRepository.save(authority);

        // If authority status gets disabled remove user as vb site contact
        if (verifierUpdate.getAuthorityStatus().equals(AuthorityStatus.DISABLED)) {
            eventPublisher.publishEvent(VerifierUserDisabledEvent.builder()
                .userId(verifierUpdate.getUserId())
                .build());
        }

        // Add notification message for enable user from accepted invitation
        if(previousStatus.equals(AuthorityStatus.ACCEPTED)
                && verifierUpdate.getAuthorityStatus().equals(AuthorityStatus.ACTIVE)){
            if(verifierUpdate.getRoleCode().equals(AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE)){
                // Enable verification body for Verifier admin
                eventPublisher.publishEvent(VerifierAdminCreationEvent.builder()
                        .verificationBodyId(authority.getVerificationBodyId())
                        .build());
            }

            return verifierUpdate.getUserId();
        }
        return null;
    }

}
