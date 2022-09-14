package uk.gov.pmrv.api.authorization.rules.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

@ExtendWith(MockitoExtension.class)
class PmrvUserAuthorizationServiceTest {

    @InjectMocks
    private PmrvUserAuthorizationService pmrvUserAuthorizationService;

    @Mock
    private AuthorizationRulesService authorizationRulesService;

    @Test
    void authorize_no_resource() {
        String serviceName = "serviceName";
        PmrvUser pmrvUser = PmrvUser.builder().build();
        List<PmrvAuthority> authorities = List.of(
            PmrvAuthority.builder().accountId(1L).build()
        );
        pmrvUser.setAuthorities(authorities);

        assertDoesNotThrow(() -> pmrvUserAuthorizationService.authorize(pmrvUser, serviceName));

        verify(authorizationRulesService, times(1)).evaluateRules(pmrvUser, serviceName);
    }

    @Test
    void authorize_no_resource_sub_type() {
        String serviceName = "serviceName";
        String resourceId = "resourceId";
        PmrvUser pmrvUser = PmrvUser.builder().build();
        List<PmrvAuthority> authorities = List.of(
            PmrvAuthority.builder().accountId(1L).build()
        );
        pmrvUser.setAuthorities(authorities);

        pmrvUserAuthorizationService.authorize(pmrvUser, serviceName, resourceId);

        verify(authorizationRulesService, times(1)).evaluateRules(pmrvUser, serviceName, resourceId);
    }

    @Test
    void authorize_with_resource_and_resource_sub_type() {
        String serviceName = "serviceName";
        String resourceId = "resourceId";
        String resourceSubType = "resourceSubType";

        PmrvUser pmrvUser = PmrvUser.builder().build();
        List<PmrvAuthority> authorities = List.of(
            PmrvAuthority.builder().accountId(1L).build()
        );
        pmrvUser.setAuthorities(authorities);

        pmrvUserAuthorizationService.authorize(pmrvUser, serviceName, resourceId, resourceSubType);

        verify(authorizationRulesService, times(1))
            .evaluateRules(pmrvUser, serviceName, resourceId, resourceSubType);
    }
    
    @Test
    void authorize_installation_create_request_action() {
        String serviceName = "serviceName";
        String resourceId = null;
        String resourceSubType = RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name();

        PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();

        pmrvUserAuthorizationService.authorize(pmrvUser, serviceName, resourceId, resourceSubType);

        verify(authorizationRulesService, times(1))
            .evaluateRules(pmrvUser, serviceName, resourceId, resourceSubType);
    }

}