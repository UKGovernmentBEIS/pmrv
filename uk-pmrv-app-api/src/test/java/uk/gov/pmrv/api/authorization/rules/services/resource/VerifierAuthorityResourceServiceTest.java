package uk.gov.pmrv.api.authorization.rules.services.resource;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;

@ExtendWith(MockitoExtension.class)
class VerifierAuthorityResourceServiceTest {

    @InjectMocks
    private VerifierAuthorityResourceService service;

    @Mock
    private AuthorityRepository authorityRepository;

    @Test
    void findUserScopedRequestTaskTypes() {
        Map<Long, Set<String>> scopedRequestTaskTypesAsString = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name()));
        Map<Long, Set<String>> expectedScopedRequestTaskTypes = Map.of(1L, Set.of(INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name()));

        // Mock
        when(authorityRepository.findResourceSubTypesVerifierUserHasScope("userId", ResourceType.REQUEST_TASK, Scope.REQUEST_TASK_VIEW))
                .thenReturn(scopedRequestTaskTypesAsString);

        // Invoke
        Map<Long, Set<String>> actual = service.findUserScopedRequestTaskTypes("userId");

        // Assert
        assertEquals(expectedScopedRequestTaskTypes, actual);
        verify(authorityRepository, times(1))
                .findResourceSubTypesVerifierUserHasScope("userId", ResourceType.REQUEST_TASK, Scope.REQUEST_TASK_VIEW);
    }
    
    @Test
    void findUsersWithScopeOnResourceTypeAndSubTypeAndVerificationBodyId() {
        ResourceType resourceType = ResourceType.REQUEST_TASK; 
        String resourceSubType = RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT.name();
        Scope scope = Scope.REQUEST_TASK_VIEW;
        Long verificationBodyId = 1L;
        
        when(authorityRepository.findVerifierUsersWithScopeOnResourceTypeAndResourceSubTypeAndVerificationBodyId(resourceType, resourceSubType, scope, verificationBodyId))
            .thenReturn(List.of("user1"));
        
        List<String> result = service.findUsersWithScopeOnResourceTypeAndSubTypeAndVerificationBodyId(resourceType, resourceSubType, scope, verificationBodyId);
        
        assertThat(result).isEqualTo(List.of("user1"));
        verify(authorityRepository, times(1))
            .findVerifierUsersWithScopeOnResourceTypeAndResourceSubTypeAndVerificationBodyId(resourceType, resourceSubType, scope, verificationBodyId);
    }
    
    @Test
    void findUsersByVerificationBodyId() {
        Long verificationBodyId = 1L;
        
        when(authorityRepository.findVerifierUsersByVerificationBodyId(verificationBodyId))
            .thenReturn(List.of("user1"));
        
        List<String> result = service.findUsersByVerificationBodyId(verificationBodyId);
        
        assertThat(result).isEqualTo(List.of("user1"));
        verify(authorityRepository, times(1))
            .findVerifierUsersByVerificationBodyId(verificationBodyId);
    }
}
