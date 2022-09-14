package uk.gov.pmrv.api.authorization.regulator.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.service.AuthorityAssignmentService;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorAuthorityService;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class RegulatorAuthorityServiceTest {

    @InjectMocks
    private RegulatorAuthorityService service;

    @Mock
    private AuthorityRepository authorityRepository;

    @Mock
    private AuthorityAssignmentService authorityAssignmentService;

    @Test
    void existsByUserIdAndCompetentAuthority() {
        final String user = "user";
        final CompetentAuthority ca = ENGLAND;

        when(authorityRepository.existsByUserIdAndCompetentAuthority(user, ca)).thenReturn(true);

        //invoke
        boolean result = service.existsByUserIdAndCompetentAuthority(user, ca);

        assertThat(result).isTrue();
        verify(authorityRepository, times(1)).existsByUserIdAndCompetentAuthority(user, ca);
    }

    @Test
    void createRegulatorAuthorityPermissions_new() {
        final PmrvUser modificationUser = PmrvUser.builder().userId("authId").build();
        final String userId = "userId";
        final CompetentAuthority competentAuthority = ENGLAND;
        final List<Permission> permissions = List.of(Permission.PERM_ACCOUNT_USERS_EDIT);

        when(authorityRepository.findByUserIdAndCompetentAuthority(userId, competentAuthority))
            .thenReturn(Optional.empty());
        
        String authorityUuid = "uuid";
        Authority authority = Authority.builder()
                .userId(userId)
                .status(AuthorityStatus.PENDING)
                .createdBy(modificationUser.getUserId())
                .uuid(authorityUuid)
                .build();
        when(authorityAssignmentService.createAuthorityWithPermissions(Mockito.any(Authority.class), Mockito.eq(permissions)))
                .thenReturn(authority);

        String result = service.createRegulatorAuthorityPermissions(modificationUser, userId, competentAuthority, permissions);
        assertThat(result).isEqualTo(authorityUuid);
        
        verify(authorityRepository, times(1)).findByUserIdAndCompetentAuthority(userId, competentAuthority);
        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityAssignmentService, times(1))
            .createAuthorityWithPermissions(authorityCaptor.capture(), eq(permissions));
        Authority authoritySaved = authorityCaptor.getValue();
        assertThat(authoritySaved.getUserId()).isEqualTo(userId);
        assertThat(authoritySaved.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(authoritySaved.getUuid()).isNotNull();
        assertThat(authoritySaved.getStatus()).isEqualTo(AuthorityStatus.PENDING);
        assertThat(authoritySaved.getCreatedBy()).isEqualTo(modificationUser.getUserId());
        assertThat(authoritySaved.getAccountId()).isNull();
        assertThat(authoritySaved.getVerificationBodyId()).isNull();
    }

    @Test
    void createRegulatorAuthorityPermissions_update() {
        final PmrvUser authUser = PmrvUser.builder().userId("authId").build();
        final String userId = "userId";
        final CompetentAuthority competentAuthority = ENGLAND;
        final List<Permission> permissions = List.of(Permission.PERM_ACCOUNT_USERS_EDIT);

        final Authority authorityOld =
            Authority.builder()
                .userId(userId)
                .competentAuthority(competentAuthority)
                .status(AuthorityStatus.PENDING)
                .build();
        
        String authorityUuid = "uuid";
        Authority authorityUpdated =
                Authority.builder()
                    .userId(userId)
                    .competentAuthority(competentAuthority)
                    .status(AuthorityStatus.PENDING)
                    .uuid(authorityUuid)
                    .build();

        when(authorityRepository.findByUserIdAndCompetentAuthority(userId, competentAuthority))
            .thenReturn(Optional.of(authorityOld));
        when(authorityAssignmentService.updatePendingAuthorityWithNewPermissions(authorityOld, permissions, authUser.getUserId()))
            .thenReturn(authorityUpdated);

        String result = service.createRegulatorAuthorityPermissions(authUser, userId, competentAuthority, permissions);

        assertThat(result).isEqualTo(authorityUuid);
        
        verify(authorityRepository, times(1)).findByUserIdAndCompetentAuthority(userId, competentAuthority);
        ArgumentCaptor<Authority> authorityCaptor = ArgumentCaptor.forClass(Authority.class);
        verify(authorityAssignmentService, times(1))
            .updatePendingAuthorityWithNewPermissions(authorityCaptor.capture(), eq(permissions),
                eq(authUser.getUserId()));
        Authority authoritySaved = authorityCaptor.getValue();
        assertThat(authoritySaved.getUserId()).isEqualTo(userId);
        assertThat(authoritySaved.getCompetentAuthority()).isEqualTo(competentAuthority);
    }

    @Test
    void createRegulatorAuthorityPermissions_authority_found_with_status_not_pending() {
        final PmrvUser authUser = PmrvUser.builder().userId("authId").build();
        final String userId = "userId";
        final CompetentAuthority competentAuthority = ENGLAND;
        final List<Permission> permissions = List.of(Permission.PERM_ACCOUNT_USERS_EDIT);

        final Authority authorityOld =
            Authority.builder()
                .userId(userId)
                .competentAuthority(competentAuthority)
                .status(AuthorityStatus.DISABLED)
                .build();
        when(authorityRepository.findByUserIdAndCompetentAuthority(userId, competentAuthority))
            .thenReturn(Optional.ofNullable(authorityOld));

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> service.createRegulatorAuthorityPermissions(authUser, userId, competentAuthority, permissions));

        // Assert
        assertEquals(ErrorCode.AUTHORITY_USER_UPDATE_NON_PENDING_AUTHORITY_NOT_ALLOWED,
            businessException.getErrorCode());

        verify(authorityRepository, times(1)).findByUserIdAndCompetentAuthority(userId, competentAuthority);
        verify(authorityRepository, never()).save(Mockito.any());
    }
}
