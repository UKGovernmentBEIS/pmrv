package uk.gov.pmrv.api.authorization.rules.services.authorization;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.domain.ResourceType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class PmrvResourceAuthorizationServiceDelegatorTest {

    private PmrvResourceAuthorizationServiceDelegator pmrvResourceAuthorizationServiceDelegator;
    private PmrvAccountAuthorizationService pmrvAccountAuthorizationService;
    private PmrvCompAuthAuthorizationService pmrvCompAuthAuthorizationService;
    private PmrvVerificationBodyAuthorizationService pmrvVerificationBodyAuthorizationService;

    private final PmrvUser user = PmrvUser.builder().userId("user").build();
    private final AuthorizationCriteria criteria = AuthorizationCriteria.builder().build();

    @BeforeAll
    void beforeAll() {
        pmrvAccountAuthorizationService = Mockito.mock(PmrvAccountAuthorizationService.class);
        pmrvCompAuthAuthorizationService = Mockito.mock(PmrvCompAuthAuthorizationService.class);
        pmrvVerificationBodyAuthorizationService = Mockito.mock(PmrvVerificationBodyAuthorizationService.class);
        pmrvResourceAuthorizationServiceDelegator =
            new PmrvResourceAuthorizationServiceDelegator(List.of(pmrvAccountAuthorizationService,
                pmrvCompAuthAuthorizationService, pmrvVerificationBodyAuthorizationService));

        when(pmrvAccountAuthorizationService.getResourceType()).thenReturn(ResourceType.ACCOUNT);
        when(pmrvCompAuthAuthorizationService.getResourceType()).thenReturn(ResourceType.CA);
        when(pmrvVerificationBodyAuthorizationService.getResourceType()).thenReturn(ResourceType.VERIFICATION_BODY);
    }

    @Test
    void isAuthorized_resource_account() {
        when(pmrvAccountAuthorizationService.isAuthorized(user, criteria)).thenReturn(true);

        assertTrue(pmrvResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.ACCOUNT, user, criteria));

        verify(pmrvAccountAuthorizationService, times(1)).isAuthorized(user, criteria);
    }

    @Test
    void isAuthorized_resource_competent_authority() {
        when(pmrvCompAuthAuthorizationService.isAuthorized(user, criteria)).thenReturn(true);

        assertTrue(pmrvResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.CA, user, criteria));

        verify(pmrvCompAuthAuthorizationService, times(1)).isAuthorized(user, criteria);
    }

    @Test
    void isAuthorized_resource_verification_body() {
        when(pmrvVerificationBodyAuthorizationService.isAuthorized(user, criteria)).thenReturn(true);

        assertTrue(pmrvResourceAuthorizationServiceDelegator.isAuthorized(ResourceType.VERIFICATION_BODY, user, criteria));

        verify(pmrvVerificationBodyAuthorizationService, times(1)).isAuthorized(user, criteria);
    }

    @Test
    void isAuthorized_resource_null() {
        assertFalse(pmrvResourceAuthorizationServiceDelegator.isAuthorized(null, user, criteria));
    }
}