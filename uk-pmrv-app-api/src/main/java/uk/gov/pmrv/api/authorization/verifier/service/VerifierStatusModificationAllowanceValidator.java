package uk.gov.pmrv.api.authorization.verifier.service;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;

import org.springframework.stereotype.Component;

import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.verifier.domain.VerifierAuthorityUpdateDTO;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class VerifierStatusModificationAllowanceValidator implements VerifierAuthorityUpdateValidator {

    private final AuthorityRepository authorityRepository;

    @Override
    public void validateUpdate(List<VerifierAuthorityUpdateDTO> verifiersUpdate, Long verificationBodyId) {
        if(ObjectUtils.isEmpty(verifiersUpdate)){
            return;
        }

        // Get verifiers
        Set<String> userIds = verifiersUpdate.stream().map(VerifierAuthorityUpdateDTO::getUserId)
                .collect(Collectors.toSet());
        Map<String, Authority> authorities = authorityRepository
                .findAllByUserIdInAndVerificationBodyId(userIds, verificationBodyId).stream()
                .collect(Collectors.toMap(Authority::getUserId, authority -> authority));

        // Validate each one
        verifiersUpdate.forEach(verifier -> {
            Authority authority = authorities.getOrDefault(verifier.getUserId(), new Authority());

            if(AuthorityStatus.ACCEPTED.equals(authority.getStatus())
                    && !(verifier.getAuthorityStatus().equals(AuthorityStatus.ACTIVE)
                        || verifier.getAuthorityStatus().equals(AuthorityStatus.ACCEPTED))){

                throw new BusinessException(ErrorCode.AUTHORITY_INVALID_STATUS,
                        verifier.getUserId(), verifier.getAuthorityStatus().name());
            }
        });
    }
}
