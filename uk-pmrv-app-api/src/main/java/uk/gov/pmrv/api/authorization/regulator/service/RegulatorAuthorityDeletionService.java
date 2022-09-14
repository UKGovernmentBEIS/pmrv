package uk.gov.pmrv.api.authorization.regulator.service;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.regulator.event.RegulatorAuthorityDeletionEvent;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RegulatorAuthorityDeletionService {

    private final AuthorityRepository authorityRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void deleteCurrentRegulatorUser(PmrvUser authUser) {
        deleteRegulator(authUser.getUserId());
    }

    @Transactional
    public void deleteRegulatorUser(String userId, PmrvUser authUser) {
        CompetentAuthority ca = authUser.getCompetentAuthority();
        validateUserToBeDeleted(userId, ca);
        deleteRegulator(userId);
    }

    private void deleteRegulator(String userId) {

        final List<Authority> authorities = authorityRepository.findByUserId(userId);
        if (ObjectUtils.isEmpty(authorities)) {
            throw new BusinessException(ErrorCode.USER_NOT_EXIST);
        }
        final RegulatorAuthorityDeletionEvent event = RegulatorAuthorityDeletionEvent.builder()
                .userId(userId)
                .build();

        authorityRepository.deleteByUserId(userId);
        eventPublisher.publishEvent(event);
    }

    private void validateUserToBeDeleted(String userId, CompetentAuthority competentAuthority) {
        if (!authorityRepository.existsByUserIdAndCompetentAuthority(userId, competentAuthority)) {
            throw new BusinessException(ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_CA);
        }
    }
}
