package uk.gov.pmrv.api.account.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.repository.LegalEntityRepository;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@Service
@RequiredArgsConstructor
public class LegalEntityValidationService {

    private final LegalEntityRepository legalEntityRepository;

    public void validateNameExistenceInOtherActiveLegalEntities(String name, Long legalEntityId) {
        if(legalEntityRepository.existsByNameAndStatusAndIdNot(name, LegalEntityStatus.ACTIVE, legalEntityId)) {
            throw new BusinessException(ErrorCode.LEGAL_ENTITY_ALREADY_EXISTS);
        }
    }
}
