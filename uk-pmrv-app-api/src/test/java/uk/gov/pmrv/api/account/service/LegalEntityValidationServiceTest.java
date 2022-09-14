package uk.gov.pmrv.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.repository.LegalEntityRepository;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class LegalEntityValidationServiceTest {

    @InjectMocks
    private LegalEntityValidationService service;

    @Mock
    private LegalEntityRepository legalEntityRepository;

    @Test
    void validateNameExistenceInOtherActiveLegalEntities_exists() {
        Long id = 1L;
        String name = "name";

        when(legalEntityRepository.existsByNameAndStatusAndIdNot(name, LegalEntityStatus.ACTIVE, id)).thenReturn(true);

        BusinessException be = assertThrows(BusinessException.class,
            () -> service.validateNameExistenceInOtherActiveLegalEntities(name, id));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.LEGAL_ENTITY_ALREADY_EXISTS);

        verify(legalEntityRepository, times(1))
            .existsByNameAndStatusAndIdNot(name, LegalEntityStatus.ACTIVE, id);
    }

    @Test
    void validateNameExistenceInOtherActiveLegalEntities_not_exists() {
        Long id = 1L;
        String name = "name";

        when(legalEntityRepository.existsByNameAndStatusAndIdNot(name, LegalEntityStatus.ACTIVE, id)).thenReturn(false);

        service.validateNameExistenceInOtherActiveLegalEntities(name, id);

        verify(legalEntityRepository, times(1))
            .existsByNameAndStatusAndIdNot(name, LegalEntityStatus.ACTIVE, id);
    }
}