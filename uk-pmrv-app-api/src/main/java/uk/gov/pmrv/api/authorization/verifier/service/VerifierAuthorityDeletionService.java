package uk.gov.pmrv.api.authorization.verifier.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.verifier.event.VerifierAuthorityDeletionEvent;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;

import java.util.List;

import static uk.gov.pmrv.api.common.exception.ErrorCode.AUTHORITY_USER_IS_NOT_VERIFIER;
import static uk.gov.pmrv.api.common.exception.ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY;

@Log4j2
@Service
@RequiredArgsConstructor
public class VerifierAuthorityDeletionService {

    private final AuthorityRepository authorityRepository;
    private final ApplicationEventPublisher eventPublisher;
    private final VerifierAdminExistenceValidator verifierAdminExistenceValidator;

    @Transactional
    public void deleteVerifierAuthority(String userId, PmrvUser authUser) {
        switch (authUser.getRoleType()) {
            case REGULATOR:
                deleteVerifierAuthority(userId);
                break;
            case VERIFIER:
                deleteVerifierAuthority(userId, authUser.getVerificationBodyId());
                break;
            default:
                throw new UnsupportedOperationException(
                    String.format("User with role type %s can not delete verifier user", authUser.getRoleType()));
        }
    }

    @Transactional
    public void deleteVerifierAuthority(String userId, Long verificationBodyId) {
        Authority authority = authorityRepository.findByUserIdAndVerificationBodyId(userId, verificationBodyId)
            .orElseThrow(() -> new BusinessException(AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY));

        deleteAuthority(authority);
    }

    @Transactional
    public void deleteVerifierAuthorities(Long verificationBodyId) {
        List<Authority> authorities = authorityRepository.findAllByVerificationBodyId(verificationBodyId);
        authorityRepository.deleteAll(authorities);

        authorities.forEach(this::publishVerifierUserDeletedEvent);
    }

    private void deleteVerifierAuthority(String userId) {
        Authority authority = authorityRepository.findByUserIdAndVerificationBodyIdNotNull(userId)
                .orElseThrow(() -> new BusinessException(AUTHORITY_USER_IS_NOT_VERIFIER));

        deleteAuthority(authority);
    }

    private void deleteAuthority(Authority authority) {
        verifierAdminExistenceValidator.validateDeletion(authority);
        authorityRepository.delete(authority);
        publishVerifierUserDeletedEvent(authority);
    }

    private void publishVerifierUserDeletedEvent(Authority authority) {
        eventPublisher.publishEvent(VerifierAuthorityDeletionEvent.builder()
                .userId(authority.getUserId())
                .build());
    }
}
