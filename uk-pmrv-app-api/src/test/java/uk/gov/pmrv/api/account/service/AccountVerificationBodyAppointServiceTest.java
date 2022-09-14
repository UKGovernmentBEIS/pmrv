package uk.gov.pmrv.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.context.ApplicationEventPublisher;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.event.AccountVerificationBodyAppointedEvent;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;

@ExtendWith(MockitoExtension.class)
class AccountVerificationBodyAppointServiceTest {

    @InjectMocks
    private AccountVerificationBodyAppointService service;
    
    @Mock
    private AccountQueryService accountQueryService;
    
    @Mock
    private VerificationBodyQueryService verificationBodyQueryService;

    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private AccountVerificationBodyNotificationService accountVerificationBodyNotificationService;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @Test
    void appointVerificationBodyToAccount() {
        Long verificationBodyId = 1L;
        Long accountId = 1L;
        
        Account account = Account.builder()
                .id(accountId)
                .emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS)
                .build();
        
        when(accountQueryService.getApprovedAccountById(accountId))
            .thenReturn(Optional.of(account));

        when(accountQueryService.getAccountVerificationBodyId(accountId))
            .thenReturn(Optional.empty());
        
        when(verificationBodyQueryService.existsActiveVerificationBodyById(verificationBodyId))
            .thenReturn(true);
        
        when(verificationBodyQueryService.isVerificationBodyAccreditedToEmissionTradingScheme(verificationBodyId, account.getEmissionTradingScheme()))
            .thenReturn(true);

        //invoke
        service.appointVerificationBodyToAccount(verificationBodyId, accountId);
        
        ArgumentCaptor<Account> accountCaptor = ArgumentCaptor.forClass(Account.class);
        
        verify(accountRepository, times(1)).save(accountCaptor.capture());
        Account accountSaved = accountCaptor.getValue();
        assertThat(accountSaved.getVerificationBodyId()).isEqualTo(verificationBodyId);
        
        verify(accountQueryService, times(1)).getApprovedAccountById(accountId);
        verify(accountQueryService, times(1)).getAccountVerificationBodyId(accountId);
        verify(verificationBodyQueryService, times(1)).existsActiveVerificationBodyById(verificationBodyId);
        verify(verificationBodyQueryService, times(1)).isVerificationBodyAccreditedToEmissionTradingScheme(verificationBodyId, account.getEmissionTradingScheme());
        verify(accountVerificationBodyNotificationService, times(1))
            .notifyUsersForVerificationBodyApppointment(verificationBodyId, accountSaved);
        verify(eventPublisher, times(1)).publishEvent(AccountVerificationBodyAppointedEvent.builder()
                                .accountId(accountId)
                                .verificationBodyId(verificationBodyId).build());
    }
    
    @Test
    void appointVerificationBodyToAccount_vb_not_found() {
        Long verificationBodyId = 1L;
        Long accountId = 1L;
        
        Account account = Account.builder().id(accountId).emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS).build();
        when(accountQueryService.getApprovedAccountById(accountId))
            .thenReturn(Optional.of(account));

        when(accountQueryService.getAccountVerificationBodyId(accountId))
            .thenReturn(Optional.empty());
        
        when(verificationBodyQueryService.existsActiveVerificationBodyById(verificationBodyId))
            .thenReturn(false);
        
        BusinessException be = assertThrows(BusinessException.class, () ->
                service.appointVerificationBodyToAccount(verificationBodyId, accountId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        
        verify(accountQueryService, times(1)).getApprovedAccountById(accountId);
        verify(accountQueryService, times(1)).getAccountVerificationBodyId(accountId);
        verify(verificationBodyQueryService, times(1)).existsActiveVerificationBodyById(verificationBodyId);
        verifyNoMoreInteractions(verificationBodyQueryService);
        verifyNoInteractions(accountRepository, accountVerificationBodyNotificationService, eventPublisher);
    }
    
    @Test
    void appointVerificationBodyToAccount_account_already_appointed() {
        Long verificationBodyId = 1L;
        Long accountId = 1L;
        
        Account account = Account.builder().id(accountId).emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS).build();
        when(accountQueryService.getApprovedAccountById(accountId))
            .thenReturn(Optional.of(account));

        when(accountQueryService.getAccountVerificationBodyId(accountId))
            .thenReturn(Optional.of(verificationBodyId));
        
        BusinessException be = assertThrows(BusinessException.class, () ->
                service.appointVerificationBodyToAccount(verificationBodyId, accountId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.VERIFICATION_BODY_ALREADY_APPOINTED_TO_ACCOUNT);
        
        verify(accountQueryService, times(1)).getApprovedAccountById(accountId);
        verifyNoMoreInteractions(accountQueryService);
        verifyNoInteractions(verificationBodyQueryService, accountRepository, accountVerificationBodyNotificationService, eventPublisher);
    }
    
    @Test
    void appointVerificationBodyToAccount_account_not_found() {
        Long verificationBodyId = 1L;
        Long accountId = 1L;
        
        when(accountQueryService.getApprovedAccountById(accountId))
        .thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () ->
                service.appointVerificationBodyToAccount(verificationBodyId, accountId));
        
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        
        verify(accountQueryService, times(1)).getApprovedAccountById(accountId);
        verifyNoMoreInteractions(accountQueryService);
        verifyNoInteractions(verificationBodyQueryService, accountRepository, accountVerificationBodyNotificationService, eventPublisher);
    }
    
    @Test
    void appointVerificationBodyToAccount_vb_not_accredited_to_account_emission_scheme() {
        Long verificationBodyId = 1L;
        Long accountId = 1L;
        
        Account account = Account.builder()
                .id(accountId)
                .emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS)
                .build();
        
        when(accountQueryService.getApprovedAccountById(accountId))
            .thenReturn(Optional.of(account));

        when(accountQueryService.getAccountVerificationBodyId(accountId))
            .thenReturn(Optional.empty());
        
        when(verificationBodyQueryService.existsActiveVerificationBodyById(verificationBodyId))
            .thenReturn(true);
        
        when(verificationBodyQueryService.isVerificationBodyAccreditedToEmissionTradingScheme(verificationBodyId, account.getEmissionTradingScheme()))
            .thenReturn(false);
        
        BusinessException be = assertThrows(BusinessException.class, () ->
                service.appointVerificationBodyToAccount(verificationBodyId, accountId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.VERIFICATION_BODY_NOT_ACCREDITED_TO_ACCOUNTS_EMISSION_TRADING_SCHEME);
        
        verify(accountQueryService, times(1)).getApprovedAccountById(accountId);
        verify(accountQueryService, times(1)).getAccountVerificationBodyId(accountId);
        verify(verificationBodyQueryService, times(1)).existsActiveVerificationBodyById(verificationBodyId);
        verify(verificationBodyQueryService, times(1)).isVerificationBodyAccreditedToEmissionTradingScheme(verificationBodyId, account.getEmissionTradingScheme());
        verifyNoInteractions(accountRepository, accountVerificationBodyNotificationService, eventPublisher);
    }

    @Test
    void replaceVerificationBodyToAccount() {
        final Long accountId = 1L;
        final Long currentVerificationBodyId = 1L;
        final Long newVerificationBodyId = 2L;
        
        Account account = Account.builder()
                .id(accountId)
                .verificationBodyId(currentVerificationBodyId)
                .emissionTradingScheme(EmissionTradingScheme.EU_ETS_INSTALLATIONS)
                .contacts(new HashMap<>(){{put(AccountContactType.VB_SITE, "userId");}}).build();

        // Mock
        when(accountQueryService.getApprovedAccountById(accountId)).thenReturn(Optional.of(account));
        when(verificationBodyQueryService.existsActiveVerificationBodyById(newVerificationBodyId)).thenReturn(true);
        when(verificationBodyQueryService.isVerificationBodyAccreditedToEmissionTradingScheme(newVerificationBodyId, account.getEmissionTradingScheme()))
            .thenReturn(true);
        
        // Invoke
        service.replaceVerificationBodyToAccount(newVerificationBodyId, accountId);

        // Assert
        assertTrue(account.getContacts().isEmpty());
        assertEquals(newVerificationBodyId, account.getVerificationBodyId());
        verify(accountQueryService, times(1)).getApprovedAccountById(accountId);
        verify(verificationBodyQueryService, times(1)).existsActiveVerificationBodyById(newVerificationBodyId);
        verify(verificationBodyQueryService, times(1)).isVerificationBodyAccreditedToEmissionTradingScheme(newVerificationBodyId, account.getEmissionTradingScheme());
        verify(accountRepository, times(1)).save(account);
        verify(accountVerificationBodyNotificationService, times(1))
                .notifyUsersForVerificationBodyApppointment(newVerificationBodyId, account);
    }

    @Test
    void replaceVerificationBodyToAccount_no_account() {
        final Long accountId = 1L;
        final Long newVerificationBodyId = 2L;

        // Mock
        when(accountQueryService.getApprovedAccountById(accountId)).thenReturn(Optional.empty());

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> service.replaceVerificationBodyToAccount(newVerificationBodyId, accountId));

        // Assert
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
        verify(accountQueryService, times(1)).getApprovedAccountById(anyLong());
        verify(verificationBodyQueryService, never()).existsActiveVerificationBodyById(anyLong());
        verify(verificationBodyQueryService, never()).isVerificationBodyAccreditedToEmissionTradingScheme(Mockito.anyLong(), Mockito.any(EmissionTradingScheme.class));
        verify(accountRepository, never()).save(any());
        verify(accountVerificationBodyNotificationService, never())
                .notifyUsersForVerificationBodyApppointment(anyLong(), any());
    }

    @Test
    void replaceVerificationBodyToAccount_no_vb() {
        final Long accountId = 1L;
        final Long currentVerificationBodyId = 1L;
        final Long newVerificationBodyId = 2L;
        Account account = Account.builder().verificationBodyId(currentVerificationBodyId)
                .contacts(new HashMap<>(){{put(AccountContactType.VB_SITE, "userId");}}).build();

        // Mock
        when(accountQueryService.getApprovedAccountById(accountId)).thenReturn(Optional.of(account));
        when(verificationBodyQueryService.existsActiveVerificationBodyById(newVerificationBodyId)).thenReturn(false);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> service.replaceVerificationBodyToAccount(newVerificationBodyId, accountId));

        // Assert
        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
        verify(accountQueryService, times(1)).getApprovedAccountById(accountId);
        verify(verificationBodyQueryService, times(1)).existsActiveVerificationBodyById(newVerificationBodyId);
        verify(verificationBodyQueryService, never()).isVerificationBodyAccreditedToEmissionTradingScheme(Mockito.anyLong(), Mockito.any(EmissionTradingScheme.class));
        verify(accountRepository, never()).save(any());
        verify(accountVerificationBodyNotificationService, never())
                .notifyUsersForVerificationBodyApppointment(anyLong(), any());
    }

    @Test
    void replaceVerificationBodyToAccount_appoint_same_vb() {
        final Long accountId = 1L;
        final Long currentVerificationBodyId = 1L;
        final Long newVerificationBodyId = 1L;
        Account account = Account.builder()
                .verificationBodyId(currentVerificationBodyId)
                .contacts(new HashMap<>(){{put(AccountContactType.VB_SITE, "userId");}})
                .build();

        // Mock
        when(accountQueryService.getApprovedAccountById(accountId)).thenReturn(Optional.of(account));
        when(verificationBodyQueryService.existsActiveVerificationBodyById(newVerificationBodyId)).thenReturn(true);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> service.replaceVerificationBodyToAccount(newVerificationBodyId, accountId));

        // Assert
        assertEquals(ErrorCode.VERIFICATION_BODY_ALREADY_APPOINTED_TO_ACCOUNT, businessException.getErrorCode());
        verify(accountQueryService, times(1)).getApprovedAccountById(accountId);
        verify(verificationBodyQueryService, times(1)).existsActiveVerificationBodyById(newVerificationBodyId);
        verify(verificationBodyQueryService, never()).isVerificationBodyAccreditedToEmissionTradingScheme(Mockito.anyLong(), Mockito.any(EmissionTradingScheme.class));
        verify(accountRepository, never()).save(any());
        verify(accountVerificationBodyNotificationService, never())
                .notifyUsersForVerificationBodyApppointment(anyLong(), any());
    }

    @Test
    void replaceVerificationBodyToAccount_no_vb_appointment() {
        final Long accountId = 1L;
        final Long newVerificationBodyId = 2L;
        Account account = Account.builder()
                .contacts(new HashMap<>(){{put(AccountContactType.VB_SITE, "userId");}}).build();

        // Mock
        when(accountQueryService.getApprovedAccountById(accountId)).thenReturn(Optional.of(account));
        when(verificationBodyQueryService.existsActiveVerificationBodyById(newVerificationBodyId)).thenReturn(true);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> service.replaceVerificationBodyToAccount(newVerificationBodyId, accountId));

        // Assert
        assertEquals(ErrorCode.VERIFICATION_BODY_NOT_APPOINTED_TO_ACCOUNT, businessException.getErrorCode());
        verify(accountQueryService, times(1)).getApprovedAccountById(accountId);
        verify(verificationBodyQueryService, times(1)).existsActiveVerificationBodyById(newVerificationBodyId);
        verify(accountRepository, never()).save(any());
        verify(accountVerificationBodyNotificationService, never())
            .notifyUsersForVerificationBodyApppointment(anyLong(), any());
    }
    
    @Test
    void replaceVerificationBodyToAccount_vb_not_accredited_to_account_emission_scheme() {
        final Long accountId = 1L;
        final Long currentVerificationBodyId = 1L;
        final Long newVerificationBodyId = 2L;
        Account account = Account.builder()
                .verificationBodyId(currentVerificationBodyId)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .contacts(new HashMap<>(){{put(AccountContactType.VB_SITE, "userId");}})
                .build();

        // Mock
        when(accountQueryService.getApprovedAccountById(accountId)).thenReturn(Optional.of(account));
        when(verificationBodyQueryService.existsActiveVerificationBodyById(newVerificationBodyId)).thenReturn(true);
        when(verificationBodyQueryService.isVerificationBodyAccreditedToEmissionTradingScheme(newVerificationBodyId, account.getEmissionTradingScheme()))
            .thenReturn(false);

        // Invoke
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> service.replaceVerificationBodyToAccount(newVerificationBodyId, accountId));

        // Assert
        assertEquals(ErrorCode.VERIFICATION_BODY_NOT_ACCREDITED_TO_ACCOUNTS_EMISSION_TRADING_SCHEME, businessException.getErrorCode());
        verify(accountQueryService, times(1)).getApprovedAccountById(accountId);
        verify(verificationBodyQueryService, times(1)).existsActiveVerificationBodyById(newVerificationBodyId);
        verify(verificationBodyQueryService, times(1)).isVerificationBodyAccreditedToEmissionTradingScheme(newVerificationBodyId, account.getEmissionTradingScheme());
        verify(accountRepository, never()).save(any());
        verify(accountVerificationBodyNotificationService, never())
                .notifyUsersForVerificationBodyApppointment(anyLong(), any());
    }
}
