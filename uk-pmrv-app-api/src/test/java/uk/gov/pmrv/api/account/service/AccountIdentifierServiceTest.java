package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.AccountIdentifier;
import uk.gov.pmrv.api.account.repository.AccountIdentifierRepository;
import uk.gov.pmrv.api.common.exception.BusinessException;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.exception.ErrorCode.RESOURCE_NOT_FOUND;

@ExtendWith(MockitoExtension.class)
class AccountIdentifierServiceTest {

    @InjectMocks
    private AccountIdentifierService accountIdentifierService;

    @Mock
    private AccountIdentifierRepository accountIdentifierRepository;

    @Test
    void incrementAndGet() {
        long identifierId = 1L;
        AccountIdentifier identifier = AccountIdentifier.builder().id(1).accountId(identifierId).build();
        AccountIdentifier newIdentifier = AccountIdentifier.builder().id(1).accountId(identifierId + 1).build();

        when(accountIdentifierRepository.findAccountIdentifier()).thenReturn(Optional.of(identifier));

        // Invoke
        Long result = accountIdentifierService.incrementAndGet();

        // Verify
        assertThat(result).isEqualTo(identifierId + 1);
        verify(accountIdentifierRepository, times(1)).findAccountIdentifier();
        verify(accountIdentifierRepository, times(1)).save(newIdentifier);
    }

    @Test
    void incrementAndGet_not_found() {
        doThrow(new BusinessException((RESOURCE_NOT_FOUND))).when(accountIdentifierRepository)
                .findAccountIdentifier();

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> accountIdentifierService.incrementAndGet());

        // Verify
        assertThat(businessException.getErrorCode()).isEqualTo(RESOURCE_NOT_FOUND);
        verify(accountIdentifierRepository, times(1)).findAccountIdentifier();
        verifyNoMoreInteractions(accountIdentifierRepository);
    }
}
