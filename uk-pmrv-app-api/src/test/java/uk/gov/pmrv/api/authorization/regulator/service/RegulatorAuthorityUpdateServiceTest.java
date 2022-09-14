package uk.gov.pmrv.api.authorization.regulator.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.service.AuthorityAssignmentService;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorUserUpdateStatusDTO;
import uk.gov.pmrv.api.authorization.regulator.event.RegulatorUserStatusDisabledEvent;
import uk.gov.pmrv.api.authorization.rules.domain.Scope;
import uk.gov.pmrv.api.authorization.rules.services.resource.CompAuthAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.user.core.service.UserNotificationService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.core.domain.Permission.PERM_CA_USERS_EDIT;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup.MANAGE_USERS_AND_CONTACTS;
import static uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel.EXECUTE;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.common.exception.ErrorCode.AUTHORITY_USER_NOT_RELATED_TO_CA;

@ExtendWith(MockitoExtension.class)
class RegulatorAuthorityUpdateServiceTest {

    @InjectMocks
    private RegulatorAuthorityUpdateService regulatorAuthorityUpdateService;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private AuthorityAssignmentService authorityAssignmentService;

    @Mock
    private CompAuthAuthorizationResourceService compAuthAuthorizationResourceService;
    
    @Mock
    private RegulatorStatusModificationAllowanceValidator regulatorStatusModificationAllowanceValidator;

    @Mock
    private ApplicationEventPublisher eventPublisher;

    @Mock
    private UserNotificationService userNotificationService;
    
    
    @Test
    void updateRegulatorUsersStatus() {
        final CompetentAuthority ca = ENGLAND;
        String user1 = "user1";
        String user2 = "user2";
        Authority regulatorUserAuthority1 = Authority.builder().userId(user1).status(AuthorityStatus.ACCEPTED).build();
        Authority regulatorUserAuthority2 = Authority.builder().userId(user2).status(AuthorityStatus.ACTIVE).build();

        RegulatorUserUpdateStatusDTO regulatorUserUpdateStatus1 =
            RegulatorUserUpdateStatusDTO.builder().userId(user1).authorityStatus(AuthorityStatus.ACTIVE).build();
        RegulatorUserUpdateStatusDTO regulatorUserUpdateStatus2 =
            RegulatorUserUpdateStatusDTO.builder().userId(user2).authorityStatus(AuthorityStatus.DISABLED).build();
        List<RegulatorUserUpdateStatusDTO>
            regulatorUsers = List.of(regulatorUserUpdateStatus1, regulatorUserUpdateStatus2);
        PmrvUser authUser = buildRegulatorUser("regUserId", ca);

        //mock
        when(authorityRepository.findByUserIdAndCompetentAuthority(user1, ca))
            .thenReturn(Optional.of(regulatorUserAuthority1));
        when(authorityRepository.findByUserIdAndCompetentAuthority(user2, ca))
            .thenReturn(Optional.of(regulatorUserAuthority2));

        //invoke
        final List<String> activatedRegulators =
            regulatorAuthorityUpdateService.updateRegulatorUsersStatus(regulatorUsers, authUser);
        
        assertThat(activatedRegulators).hasSize(1);
        assertThat(activatedRegulators.get(0)).isEqualTo("user1");
        assertThat(authorityRepository.findByUserIdAndCompetentAuthority(user1, ca)).isPresent()
            .map(Authority::getStatus).hasValue(regulatorUserUpdateStatus1.getAuthorityStatus());
        assertThat(authorityRepository.findByUserIdAndCompetentAuthority(user2, ca)).isPresent()
            .map(Authority::getStatus).hasValue(regulatorUserUpdateStatus2.getAuthorityStatus());
        
        verify(eventPublisher, times(1)).publishEvent(new RegulatorUserStatusDisabledEvent(user2));
        verify(eventPublisher, never()).publishEvent(new RegulatorUserStatusDisabledEvent(user1));
        verify(regulatorStatusModificationAllowanceValidator, times(1)).validateUpdate(regulatorUsers,ca);
    }

    @Test
    void updateRegulatorUsersStatus_auth_user_not_related_to_ca() {
        final CompetentAuthority ca = ENGLAND;
        String user1 = "user1";
        String user2 = "user2";
        Authority regulatorUserAuthority1 = Authority.builder().userId(user1).status(AuthorityStatus.PENDING).build();

        RegulatorUserUpdateStatusDTO regulatorUserUpdateStatus1 =
            RegulatorUserUpdateStatusDTO.builder().userId(user1).authorityStatus(AuthorityStatus.ACTIVE).build();
        RegulatorUserUpdateStatusDTO regulatorUserUpdateStatus2 =
            RegulatorUserUpdateStatusDTO.builder().userId(user2).authorityStatus(AuthorityStatus.DISABLED).build();
        List<RegulatorUserUpdateStatusDTO>
            regulatorUsers = List.of(regulatorUserUpdateStatus1, regulatorUserUpdateStatus2);
        PmrvUser authUser = buildRegulatorUser("regUserId", ca);

        //mock
        when(authorityRepository.findByUserIdAndCompetentAuthority(user1, ca))
            .thenReturn(Optional.of(regulatorUserAuthority1));
        when(authorityRepository.findByUserIdAndCompetentAuthority(user2, ca))
            .thenReturn(Optional.empty());

        //invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> regulatorAuthorityUpdateService.updateRegulatorUsersStatus(regulatorUsers, authUser));

        assertThat(businessException.getErrorCode()).isEqualTo(AUTHORITY_USER_NOT_RELATED_TO_CA);
        assertThat(authorityRepository.findByUserIdAndCompetentAuthority(user1, ca)).isPresent()
            .map(Authority::getStatus).hasValue(regulatorUserAuthority1.getStatus());
        
        verifyNoInteractions(eventPublisher);
        verify(regulatorStatusModificationAllowanceValidator, times(1)).validateUpdate(regulatorUsers, ca);
    }

    @Test
    void updateRegulatorUserPermissions() {
        final String userId = "userId";
        final CompetentAuthority ca = ENGLAND;
        final PmrvUser authUser = PmrvUser.builder().userId("authId").roleType(RoleType.REGULATOR)
            .authorities(
                List.of(PmrvAuthority.builder()
                    .competentAuthority(ca)
                    .permissions(List.of(PERM_CA_USERS_EDIT))
                    .build()))
            .build();

        Map<RegulatorPermissionGroup, RegulatorPermissionLevel> newAuthorityPermissions = Map.of(MANAGE_USERS_AND_CONTACTS, EXECUTE);

        Authority authority = Authority.builder().build();

        //Mock
        when(authorityRepository.findByUserIdAndCompetentAuthority(userId, ca))
            .thenReturn(Optional.of(authority));
        when(authorityRepository.existsByUserIdAndCompetentAuthority(userId, ca))
                .thenReturn(true);
        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(authUser, ca, Scope.EDIT_USER))
            .thenReturn(true);

        // Invoke
        regulatorAuthorityUpdateService.updateRegulatorUserPermissions(authUser, userId, newAuthorityPermissions);

        // Assert
        verify(compAuthAuthorizationResourceService, times(1))
            .hasUserScopeToCompAuth(authUser, ca, Scope.EDIT_USER);
        verify(authorityRepository, times(1))
            .findByUserIdAndCompetentAuthority(userId, ca);
        verify(authorityAssignmentService, times(1))
            .updateAuthorityWithNewPermissions(authority, List.of(PERM_CA_USERS_EDIT));
    }

    @Test
    void updateRegulatorUserPermissions_auth_user_has_no_scope() {
        final String userId = "userId";
        final CompetentAuthority ca = ENGLAND;
        final PmrvUser authUser = PmrvUser.builder().userId("authId").roleType(RoleType.REGULATOR)
            .authorities(
                List.of(PmrvAuthority.builder()
                    .competentAuthority(ca)
                    .build()))
            .build();

        when(compAuthAuthorizationResourceService.hasUserScopeToCompAuth(authUser, ca, Scope.EDIT_USER))
            .thenReturn(false);
        when(authorityRepository.existsByUserIdAndCompetentAuthority(userId, ca))
                .thenReturn(true);
        // Invoke
        regulatorAuthorityUpdateService.updateRegulatorUserPermissions(authUser, userId, Map.of());

        // Assert
        verify(compAuthAuthorizationResourceService, times(1)).hasUserScopeToCompAuth(authUser, ca, Scope.EDIT_USER);
        verify(authorityRepository, never()).findByUserIdAndCompetentAuthority(anyString(), any());
        verify(authorityAssignmentService, never()).updateAuthorityWithNewPermissions(any(), anyList());
    }

    @Test
    void updateRegulatorUserPermissions_auth_user_not_exists_in_ca() {
        final String userId = "userId";
        final CompetentAuthority ca = ENGLAND;
        final PmrvUser authUser = PmrvUser.builder().userId("authId").roleType(RoleType.REGULATOR)
                .authorities(
                        List.of(PmrvAuthority.builder()
                                .competentAuthority(ca)
                                .build()))
                .build();

        when(authorityRepository.existsByUserIdAndCompetentAuthority(userId, ca))
                .thenReturn(false);

        try {
            regulatorAuthorityUpdateService.updateRegulatorUserPermissions(authUser, userId, Map.of());
        } catch (Exception exception) {
            assertEquals("User is not related to competent authority", exception.getMessage());
        }

        verify(compAuthAuthorizationResourceService, never()).hasUserScopeToCompAuth(authUser, ca, Scope.EDIT_USER);
        verify(authorityRepository, never()).findByUserIdAndCompetentAuthority(anyString(), any());
        verify(authorityRepository, times(1)).existsByUserIdAndCompetentAuthority(anyString(), any());
        verify(authorityAssignmentService, never()).updateAuthorityWithNewPermissions(any(), anyList());

    }

    private PmrvUser buildRegulatorUser(String userId, CompetentAuthority ca, Permission...permissions) {
        return PmrvUser.builder()
            .userId(userId)
            .roleType(RoleType.REGULATOR)
            .authorities(
                List.of(PmrvAuthority.builder()
                    .competentAuthority(ca)
                    .permissions(Arrays.asList(permissions))
                    .build()
                )
            )
            .build();

    }

}