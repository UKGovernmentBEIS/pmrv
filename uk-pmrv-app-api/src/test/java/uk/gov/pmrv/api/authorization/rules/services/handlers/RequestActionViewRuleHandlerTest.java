package uk.gov.pmrv.api.authorization.rules.services.handlers;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.rules.domain.AuthorizationRuleScopePermission;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestActionAuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers.RequestActionAuthorityInfoProvider;
import uk.gov.pmrv.api.authorization.rules.services.authorization.AuthorizationCriteria;
import uk.gov.pmrv.api.authorization.rules.services.authorization.PmrvAuthorizationService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;

import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;

@ExtendWith(MockitoExtension.class)
class RequestActionViewRuleHandlerTest {
    
    @InjectMocks
    private RequestActionViewRuleHandler handler;

    @Mock
    private PmrvAuthorizationService pmrvAuthorizationService;

    @Mock
    private RequestActionAuthorityInfoProvider requestActionAuthorityInfoProvider;
    
    @Test
    void evaluateRules() {
        PmrvUser user = PmrvUser.builder().userId("user").build();
        String resourceId = "1";
        Set<AuthorizationRuleScopePermission> authorizationRules = Set.of(
                AuthorizationRuleScopePermission.builder()
                    .resourceSubType(RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCEPTED.name())
                    .handler("handler")
                    .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK).build(),
                AuthorizationRuleScopePermission.builder()
                    .resourceSubType(RequestActionType.PERMIT_ISSUANCE_APPLICATION_SUBMITTED.name())
                    .handler("handler")
                    .permission(Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_VIEW_TASK).build()
                );
        RequestActionAuthorityInfoDTO requestActionInfoDTO = RequestActionAuthorityInfoDTO.builder()
                .id(Long.valueOf(resourceId))
                .type(RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCEPTED.name())
                .authorityInfo(ResourceAuthorityInfo.builder()
                        .accountId(1L)
                        .competentAuthority(ENGLAND)
                        .verificationBodyId(1L).build())
                .build();
        
        when(requestActionAuthorityInfoProvider.getRequestActionAuthorityInfo(Long.valueOf(resourceId)))
            .thenReturn(requestActionInfoDTO);
        
        //invoke
        handler.evaluateRules(authorizationRules, user, resourceId);
        
        verify(requestActionAuthorityInfoProvider, times(1)).getRequestActionAuthorityInfo(Long.valueOf(resourceId));
        
        ArgumentCaptor<AuthorizationCriteria> criteriaCaptor = ArgumentCaptor.forClass(AuthorizationCriteria.class);
        verify(pmrvAuthorizationService, times(1)).authorize(Mockito.eq(user), criteriaCaptor.capture());
        AuthorizationCriteria criteria = criteriaCaptor.getValue();
        assertThat(criteria).isEqualTo(
                AuthorizationCriteria.builder()
                    .accountId(1L)
                    .competentAuthority(CompetentAuthority.ENGLAND)
                    .verificationBodyId(1L)
                    .permission(Permission.PERM_INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW_EXECUTE_TASK).build());
    }
    
    @Test
    void evaluateRules_no_rules_applied() {
        PmrvUser user = PmrvUser.builder().userId("user").build();
        String resourceId = "1";
        Set<AuthorizationRuleScopePermission> authorizationRules = Set.of(
                AuthorizationRuleScopePermission.builder()
                    .resourceSubType(RequestActionType.PERMIT_ISSUANCE_APPLICATION_SUBMITTED.name())
                    .handler("handler")
                    .permission(Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_VIEW_TASK).build()
                );
        RequestActionAuthorityInfoDTO requestActionInfoDTO = RequestActionAuthorityInfoDTO.builder()
                .id(Long.valueOf(resourceId))
                .type(RequestActionType.INSTALLATION_ACCOUNT_OPENING_ACCEPTED.name())
                .authorityInfo(ResourceAuthorityInfo.builder()
                        .accountId(1L)
                        .competentAuthority(ENGLAND)
                        .verificationBodyId(1L).build())
                .build();
        
        when(requestActionAuthorityInfoProvider.getRequestActionAuthorityInfo(Long.valueOf(resourceId)))
            .thenReturn(requestActionInfoDTO);
        
        //invoke
        BusinessException be = assertThrows(BusinessException.class, () -> handler.evaluateRules(authorizationRules, user, resourceId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN);
        
        verify(requestActionAuthorityInfoProvider, times(1)).getRequestActionAuthorityInfo(Long.valueOf(resourceId));
        verifyNoInteractions(pmrvAuthorizationService);
    }
    
    @Test
    void evaluateRules_no_request_action_found() {
        PmrvUser user = PmrvUser.builder().userId("user").build();
        String resourceId = "1";
        Set<AuthorizationRuleScopePermission> authorizationRules = Set.of(
                AuthorizationRuleScopePermission.builder()
                    .resourceSubType(RequestActionType.PERMIT_ISSUANCE_APPLICATION_SUBMITTED.name())
                    .handler("handler")
                    .permission(Permission.PERM_PERMIT_ISSUANCE_APPLICATION_SUBMIT_VIEW_TASK).build()
                );
        
        when(requestActionAuthorityInfoProvider.getRequestActionAuthorityInfo(Long.valueOf(resourceId)))
            .thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        
        //invoke
        BusinessException be = assertThrows(BusinessException.class, () -> handler.evaluateRules(authorizationRules, user, resourceId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        
        verify(requestActionAuthorityInfoProvider, times(1)).getRequestActionAuthorityInfo(Long.valueOf(resourceId));
        verifyNoInteractions(pmrvAuthorizationService);
    }

}
