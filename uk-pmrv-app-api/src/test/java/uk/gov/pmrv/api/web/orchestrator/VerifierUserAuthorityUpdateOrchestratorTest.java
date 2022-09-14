package uk.gov.pmrv.api.web.orchestrator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.verifier.domain.VerifierAuthorityUpdateDTO;
import uk.gov.pmrv.api.authorization.verifier.service.VerifierAuthorityUpdateService;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserNotificationGateway;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class VerifierUserAuthorityUpdateOrchestratorTest {

    @InjectMocks
    private VerifierUserAuthorityUpdateOrchestrator service;

    @Mock
    private VerifierAuthorityUpdateService verifierAuthorityUpdateService;

    @Mock
    private VerifierUserNotificationGateway verifierUserNotificationGateway;

    @Test
    void updateVerifierAuthorities() {
        String userId = "userId";
        List<VerifierAuthorityUpdateDTO> verifiersUpdate = List.of(VerifierAuthorityUpdateDTO.builder().userId(userId).build());

        when(verifierAuthorityUpdateService.updateVerifierAuthorities(verifiersUpdate, 1L))
                .thenReturn(List.of(userId));

        // Invoke
        service.updateVerifierAuthorities(verifiersUpdate, 1L);

        // Verify
        verify(verifierAuthorityUpdateService, times(1))
                .updateVerifierAuthorities(verifiersUpdate, 1L);
        verify(verifierUserNotificationGateway, times(1))
                .notifyUsersUpdateStatus(List.of(userId));
    }

    @Test
    void updateVerifierAuthorities_empty_notifications() {
        List<VerifierAuthorityUpdateDTO> verifiersUpdate = List.of(VerifierAuthorityUpdateDTO.builder().userId("user").build());

        when(verifierAuthorityUpdateService.updateVerifierAuthorities(verifiersUpdate, 1L)).thenReturn(List.of());

        // Invoke
        service.updateVerifierAuthorities(verifiersUpdate, 1L);

        // Verify
        verify(verifierAuthorityUpdateService, times(1))
                .updateVerifierAuthorities(verifiersUpdate, 1L);
        verify(verifierUserNotificationGateway, never())
                .notifyUsersUpdateStatus(anyList());
    }
}
