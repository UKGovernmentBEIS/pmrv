package uk.gov.pmrv.api.authorization.rules.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.repository.AuthorizationRuleRepository;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthorizationRulesQueryServiceTest {
    
    @InjectMocks
    private AuthorizationRulesQueryService service;
    
    @Mock
    private AuthorizationRuleRepository authorizationRuleRepository;
    
    @Test
    void findByResourceTypeAndResourceSubTypeAndScope() {
        ResourceType resourceType = ResourceType.REQUEST_TASK;
        String resourceSubType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name();
        
        when(authorizationRuleRepository.findRoleTypeByResourceTypeAndSubType(resourceType, resourceSubType))
            .thenReturn(Optional.of(RoleType.OPERATOR));
        
        Optional<RoleType> result = service.findRoleTypeByResourceTypeAndSubType(resourceType, resourceSubType);
        
        assertThat(result)
                .isNotEmpty()
                .contains(RoleType.OPERATOR);
        verify(authorizationRuleRepository, times(1)).findRoleTypeByResourceTypeAndSubType(resourceType, resourceSubType);
    }
    
    @Test
    void findByResourceTypeAndResourceSubTypeAndScope_not_found() {
        ResourceType resourceType = ResourceType.REQUEST_TASK;
        String resourceSubType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW.name();
        
        when(authorizationRuleRepository.findRoleTypeByResourceTypeAndSubType(resourceType, resourceSubType))
            .thenReturn(Optional.empty());
        
        Optional<RoleType> result = service.findRoleTypeByResourceTypeAndSubType(resourceType, resourceSubType);
        
        assertThat(result).isEmpty();
        verify(authorizationRuleRepository, times(1)).findRoleTypeByResourceTypeAndSubType(resourceType, resourceSubType);
    }
}
