package uk.gov.pmrv.api.workflow.request.application.requestaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.authorization.rules.services.AuthorizationRulesQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestActionRepository;
import uk.gov.pmrv.api.workflow.request.core.transform.RequestActionCustomMapperHandler;

@ExtendWith(MockitoExtension.class)
class RequestActionQueryServiceTest {

    @InjectMocks
    private RequestActionQueryService service;

    @Mock
    private RequestActionRepository requestActionRepository;
    
    @Mock
    private AuthorizationRulesQueryService authorizationRulesQueryService;
    
    @Mock
    private RequestActionCustomMapperHandler customMapperHandler;

    @Test
    void getRequestActionById() {
        PmrvUser user = PmrvUser.builder().userId("user").build();
        Long requestActionId = 1L; 
        String submitterId = "submitterId";
        RequestAction requestAction = RequestAction.builder().id(requestActionId).submitterId(submitterId).submitter("fn ln").build();
        
        when(requestActionRepository.findById(requestActionId)).thenReturn(Optional.of(requestAction));
        when(customMapperHandler.getMapper(any(), any())).thenReturn(Optional.empty());
        
        //invoke
        RequestActionDTO result = service.getRequestActionById(requestActionId, user);
        assertThat(result.getId()).isEqualTo(requestActionId);
        assertThat(result.getSubmitter()).isEqualTo("fn ln");
        
        verify(requestActionRepository, times(1)).findById(requestActionId);
    }
    
    @Test
    void getRequestActionById_not_found() {
        PmrvUser user = PmrvUser.builder().userId("user").build();
        Long requestActionId = 1L; 
        
        when(requestActionRepository.findById(requestActionId)).thenReturn(Optional.empty());
        
        //invoke
        BusinessException be = assertThrows(BusinessException.class, () -> service.getRequestActionById(requestActionId, user));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        
        verify(requestActionRepository, times(1)).findById(requestActionId);
    }

    @Test
    void getRequestActionsByRequestId() {
        final String requestId = "1";
        PmrvUser authUser = PmrvUser.builder().userId("user").roleType(RoleType.OPERATOR).build();
        
        RequestAction requestAction1 = RequestAction.builder()
                .id(1L)
                .type(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED)
                .submitterId("user1")
                .submitter("fn ln 1")
                .build();
        
        RequestAction requestAction2 = RequestAction.builder()
                .id(2L)
                .type(RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCOUNT_APPROVED)
                .submitterId("user2")
                .submitter("fn ln 2")
                .build();
        
        List<RequestAction> requestActions = List.of(requestAction1, requestAction2);
        
        when(requestActionRepository.findAllByRequestId(requestId))
            .thenReturn(requestActions);
            
        Set<String> userAllowedRequestActionTypes = Set.of(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED.name());
        when(authorizationRulesQueryService.findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST_ACTION, RoleType.OPERATOR))
            .thenReturn(userAllowedRequestActionTypes);
        
        List<RequestActionInfoDTO> result = service.getRequestActionsByRequestId(requestId, authUser);
        
        assertThat(result.size()).isEqualTo(1);
        assertThat(result.get(0)).isEqualTo(RequestActionInfoDTO.builder()
                .id(1L)
                .type(RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED)
                .submitter("fn ln 1")
                .build());
        verify(requestActionRepository, times(1)).findAllByRequestId(requestId);
        verify(authorizationRulesQueryService, times(1)).findResourceSubTypesByResourceTypeAndRoleType(ResourceType.REQUEST_ACTION, RoleType.OPERATOR);
        verifyNoMoreInteractions(requestActionRepository, authorizationRulesQueryService);
    }
    
}
