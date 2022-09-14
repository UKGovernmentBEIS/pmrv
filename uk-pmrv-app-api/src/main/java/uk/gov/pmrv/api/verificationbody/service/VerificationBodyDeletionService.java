package uk.gov.pmrv.api.verificationbody.service;

import lombok.RequiredArgsConstructor;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.verificationbody.domain.event.VerificationBodyDeletedEvent;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;

@Service
@RequiredArgsConstructor
public class VerificationBodyDeletionService {

    private final VerificationBodyRepository verificationBodyRepository;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void deleteVerificationBodyById(Long verificationBodyId) {
        if(!verificationBodyRepository.existsById(verificationBodyId)){
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, verificationBodyId);
        }

        // Delete VB
        verificationBodyRepository.deleteById(verificationBodyId);

        // Publish event for deleting verification body
        eventPublisher.publishEvent(new VerificationBodyDeletedEvent(verificationBodyId));
    }
}
