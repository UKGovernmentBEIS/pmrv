package uk.gov.pmrv.api.user.operator.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.AuthorityConstants;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserAcceptInvitationDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;
import uk.gov.pmrv.api.user.operator.service.EmitterContactAcceptInvitationService;
import uk.gov.pmrv.api.user.operator.service.OperatorRoleCodeAcceptInvitationServiceDefaultImpl;
import uk.gov.pmrv.api.user.operator.service.OperatorRoleCodeAcceptInvitationServiceDelegator;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ExtendWith(MockitoExtension.class)
class OperatorRoleCodeAcceptInvitationServiceDelegatorTest {

    private OperatorRoleCodeAcceptInvitationServiceDelegator serviceDelegator;
    private EmitterContactAcceptInvitationService emitterContactAcceptInvitationService;
    private OperatorRoleCodeAcceptInvitationServiceDefaultImpl operatorRoleCodeAcceptInvitationServiceDefaultImpl;

    @BeforeAll
    void setUp() {
        emitterContactAcceptInvitationService = mock(EmitterContactAcceptInvitationService.class);
        operatorRoleCodeAcceptInvitationServiceDefaultImpl = mock(OperatorRoleCodeAcceptInvitationServiceDefaultImpl.class);

        serviceDelegator = new OperatorRoleCodeAcceptInvitationServiceDelegator(
            List.of(emitterContactAcceptInvitationService, operatorRoleCodeAcceptInvitationServiceDefaultImpl));

        when(emitterContactAcceptInvitationService.getRoleCodes()).thenReturn(Set.of(AuthorityConstants.EMITTER_CONTACT));
        when(operatorRoleCodeAcceptInvitationServiceDefaultImpl.getRoleCodes())
            .thenReturn(Set.of(AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE));

    }

    @Test
    void acceptInvitation_when_not_emitter_contact() {
        String roleCode = AuthorityConstants.OPERATOR_ADMIN_ROLE_CODE;
        OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation = OperatorUserAcceptInvitationDTO.builder().build();

        when(operatorRoleCodeAcceptInvitationServiceDefaultImpl.acceptInvitation(operatorUserAcceptInvitation))
            .thenReturn(UserInvitationStatus.PENDING_USER_REGISTRATION);

        serviceDelegator.acceptInvitation(operatorUserAcceptInvitation, roleCode);

        verify(operatorRoleCodeAcceptInvitationServiceDefaultImpl, times(1))
            .acceptInvitation(operatorUserAcceptInvitation);
    }

    @Test
    void acceptInvitation_when_emitter_contact() {
        String roleCode = AuthorityConstants.EMITTER_CONTACT;
        OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation = OperatorUserAcceptInvitationDTO.builder().build();

        when(emitterContactAcceptInvitationService.acceptInvitation(operatorUserAcceptInvitation))
            .thenReturn(UserInvitationStatus.PENDING_USER_REGISTRATION_NO_PASSWORD);

        serviceDelegator.acceptInvitation(operatorUserAcceptInvitation, roleCode);

        verify(emitterContactAcceptInvitationService, times(1))
            .acceptInvitation(operatorUserAcceptInvitation);
    }

    @Test
    void acceptInvitation_when_no_matched_role_code() {
        String roleCode = AuthorityConstants.VERIFIER_ADMIN_ROLE_CODE;
        OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation = OperatorUserAcceptInvitationDTO.builder().build();

        BusinessException businessException = assertThrows(BusinessException.class,
            () ->serviceDelegator.acceptInvitation(operatorUserAcceptInvitation, roleCode));

        assertEquals(ErrorCode.AUTHORITY_USER_IS_NOT_OPERATOR, businessException.getErrorCode());
    }
}