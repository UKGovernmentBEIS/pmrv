package uk.gov.pmrv.api.authorization.core.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.core.domain.Authority;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.authorization.core.transform.AuthorityMapper;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.ACTIVE;
import static uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus.PENDING;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;

@ExtendWith(MockitoExtension.class)
class AuthorityServiceTest {
	
	@InjectMocks
	private AuthorityService authorityService;
	
	@Mock
	private AuthorityRepository authorityRepository;

	@Mock
    private AuthorityMapper authorityMapper;

    @Test
    void findAssignedPermissionsByUserId() {
		String USER_ID = "userId";
        when(authorityRepository.findAssignedPermissionsByUserId(USER_ID)).thenReturn(List.of(Permission.PERM_ACCOUNT_USERS_EDIT));

        List<Permission> permissions = authorityService.findAssignedPermissionsByUserId(USER_ID);

        assertEquals(Permission.PERM_ACCOUNT_USERS_EDIT, permissions.get(0));
        verify(authorityRepository, times(1)).findAssignedPermissionsByUserId(USER_ID);
    }

    @Test
    void getActiveAuthoritiesWithAssignedPermissions() {
    	Long ACCOUNT_ID = 1L;
		String USER_ID = "userId";
	    Authority authority = createAuthority(USER_ID, ACCOUNT_ID, ENGLAND, ACTIVE);
	    AuthorityDTO authorityDTO = createAuthorityDTO(ACCOUNT_ID, ENGLAND, Collections.emptyList());
	    when(authorityRepository.findByUserIdAndStatus(USER_ID, ACTIVE)).thenReturn(Collections.singletonList(authority));

	    when(authorityMapper.toAuthorityDTO(authority)).thenReturn(authorityDTO);

        List<AuthorityDTO> activeAuthorities = authorityService.getActiveAuthoritiesWithAssignedPermissions(USER_ID);

        assertThat(activeAuthorities).hasSize(1);
    }

    @Test
    void getActiveAuthoritiesWithAssignedPermissionsReturnEmptyList() {
    	String USER_ID = "userId";
        when(authorityRepository.findByUserIdAndStatus(USER_ID, ACTIVE)).thenReturn(Collections.emptyList());

        List<AuthorityDTO> activeAuthorities = authorityService.getActiveAuthoritiesWithAssignedPermissions(USER_ID);

        assertTrue(activeAuthorities.isEmpty());
    }
    
    @Test
    void existsByUserId() {
    	final String user = "user";
    	
    	when(authorityRepository.existsByUserId(user)).thenReturn(true);
    	
    	//invoke
    	boolean result = authorityService.existsByUserId(user);
    	
    	assertThat(result).isTrue();
    	verify(authorityRepository, times(1)).existsByUserId(user);
    }
    
    @Test
    void existsByUserIdAndAccountId() {
        final String userId = "user";
        final Long accountId = 1L;
        
        when(authorityRepository.findByUserIdAndAccountId(userId, accountId)).thenReturn(Optional.of(Authority.builder().id(1L).build()));
        
        //invoke
        boolean result = authorityService.existsByUserIdAndAccountId(userId, accountId);
        
        assertThat(result).isTrue();
        verify(authorityRepository, times(1)).findByUserIdAndAccountId(userId, accountId);
    }
    
    @Test
    void existsByUserIdAndAccountId_not_found() {
        final String userId = "user";
        final Long accountId = 1L;
        
        when(authorityRepository.findByUserIdAndAccountId(userId, accountId)).thenReturn(Optional.empty());
        
        //invoke
        boolean result = authorityService.existsByUserIdAndAccountId(userId, accountId);
        
        assertThat(result).isFalse();
        verify(authorityRepository, times(1)).findByUserIdAndAccountId(userId, accountId);
    }

    @Test
    void findAuthorityByUuidAndStatusPending() {
        final String userId = "userId";
        final String uuid = "uuid";
        Authority authority = createAuthority(userId, 1L, ENGLAND, ACTIVE);
        AuthorityInfoDTO expected = AuthorityInfoDTO.builder().userId(userId).authorityStatus(ACTIVE).build();

        // Mock
        when(authorityRepository.findByUuidAndStatus(uuid, PENDING)).thenReturn(Optional.of(authority));
        when(authorityMapper.toAuthorityInfoDTO(authority)).thenReturn(expected);

        // Invoke
        Optional<AuthorityInfoDTO> actualOptionalAuthorityInfo = authorityService.findAuthorityByUuidAndStatusPending(uuid);

        // Assert
        assertTrue(actualOptionalAuthorityInfo.isPresent());
        assertEquals(expected, actualOptionalAuthorityInfo.get());
        verify(authorityRepository, times(1)).findByUuidAndStatus(uuid, PENDING);
        verify(authorityMapper, times(1)).toAuthorityInfoDTO(authority);
    }

    @Test
    void findAuthorityByUuidAndStatusPending_not_found() {
        final String uuid = "uuid";

        // Mock
        when(authorityRepository.findByUuidAndStatus(uuid, PENDING)).thenReturn(Optional.empty());

        // Invoke
        Optional<AuthorityInfoDTO> actualOptionalAuthorityInfo = authorityService.findAuthorityByUuidAndStatusPending(uuid);

        // Assert
        assertFalse(actualOptionalAuthorityInfo.isPresent());
        verify(authorityRepository, times(1)).findByUuidAndStatus(uuid, PENDING);
        verify(authorityMapper, never()).toAuthorityInfoDTO(any());
    }
    
    private Authority createAuthority(String userId, Long accountId, CompetentAuthority competentAuthority, AuthorityStatus status) {
	    return Authority.builder()
            .userId(userId)
            .competentAuthority(competentAuthority)
            .accountId(accountId)
            .status(status)
            .build();
    }

    private AuthorityDTO createAuthorityDTO(Long accountId, CompetentAuthority competentAuthority,
                                            List<Permission> permissions) {
	    AuthorityDTO authorityDTO = AuthorityDTO.builder()
	        .accountId(accountId)
            .competentAuthority(competentAuthority)
            .build();

	    authorityDTO.setAuthorityPermissions(permissions);

	    return authorityDTO;
    }
    
}
