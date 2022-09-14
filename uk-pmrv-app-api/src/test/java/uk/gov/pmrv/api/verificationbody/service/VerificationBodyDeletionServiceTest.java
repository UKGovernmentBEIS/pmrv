package uk.gov.pmrv.api.verificationbody.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationEventPublisher;

import uk.gov.pmrv.api.verificationbody.domain.event.VerificationBodyDeletedEvent;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.verificationbody.repository.VerificationBodyRepository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerificationBodyDeletionServiceTest {

    @InjectMocks
    private VerificationBodyDeletionService service;

    @Mock
    private VerificationBodyRepository verificationBodyRepository;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Test
    void deleteVerificationBodyById() {
        Long verificationBodyId = 1L;

        // Mock
        when(verificationBodyRepository.existsById(verificationBodyId)).thenReturn(true);

        // Invoke
        service.deleteVerificationBodyById(verificationBodyId);

        // Assert
        verify(verificationBodyRepository, times(1)).existsById(verificationBodyId);
        verify(verificationBodyRepository, times(1)).deleteById(verificationBodyId);
        verify(eventPublisher, times(1)).publishEvent(new VerificationBodyDeletedEvent(verificationBodyId));
    }

    @Test
    void deleteVerificationBodyById_no_vb() {
        Long verificationBodyId = 1L;

        // Mock
        when(verificationBodyRepository.existsById(verificationBodyId)).thenReturn(false);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class, () ->
                service.deleteVerificationBodyById(verificationBodyId));

        // Assert
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
        verify(verificationBodyRepository, times(1)).existsById(verificationBodyId);
        verify(verificationBodyRepository, never()).deleteById(anyLong());
        verify(eventPublisher, never()).publishEvent(any());
    }
}
