package uk.gov.pmrv.api.account.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationEventPublisher;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyNameInfoDTO;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccountVerificationBodyServiceTest {

    @InjectMocks
    private AccountVerificationBodyService service;
    
    @Mock
    private AccountQueryService accountQueryService;
    
    @Mock
    private VerificationBodyQueryService verificationBodyQueryService;

    @Mock
    private AccountRepository accountRepository;
    
    @Mock
    private ApplicationEventPublisher eventPublisher;
    
    @Test
    void getVerificationBodyNameInfoByAccount() {
        Long accountId = 1L;
        Long vbId = 1L;
        
        when(accountQueryService.getAccountVerificationBodyId(accountId))
            .thenReturn(Optional.of(vbId));
        
        when(verificationBodyQueryService.getVerificationBodyNameInfoById(vbId))
            .thenReturn(VerificationBodyNameInfoDTO.builder().id(vbId).name("vb").build());
        
        Optional<VerificationBodyNameInfoDTO> result = service.getVerificationBodyNameInfoByAccount(accountId);

        Optional<VerificationBodyNameInfoDTO> vbExpected = Optional.of(VerificationBodyNameInfoDTO.builder().id(vbId).name("vb").build());
        assertEquals(vbExpected, result);

        verify(accountQueryService, times(1)).getAccountVerificationBodyId(accountId);
        verify(verificationBodyQueryService, times(1)).getVerificationBodyNameInfoById(vbId);
    }
    
    @Test
    void getVerificationBodyNameInfoByAccount_no_vb_found() {
        Long accountId = 1L;
        
        when(accountQueryService.getAccountVerificationBodyId(accountId))
            .thenReturn(Optional.empty());

        Optional<VerificationBodyNameInfoDTO> verificationBodyNameInfoByAccount = service.getVerificationBodyNameInfoByAccount(accountId);

        assertEquals(Optional.empty(), verificationBodyNameInfoByAccount);
        verify(accountQueryService, times(1)).getAccountVerificationBodyId(accountId);
        verify(verificationBodyQueryService, never()).getVerificationBodyNameInfoById(Mockito.anyLong());
    }
    
    @Test
    void getAllActiveVerificationBodiesAccreditedToAccountEmissionTradingScheme() {
        Long accountId = 1L;
        EmissionTradingScheme ets = EmissionTradingScheme.EU_ETS_INSTALLATIONS;
        Account account = Account.builder().id(accountId).emissionTradingScheme(ets).build();
        
        when(accountQueryService.getAccountById(accountId)).thenReturn(account);
        
        List<VerificationBodyNameInfoDTO> verificationBodies = List.of(
                VerificationBodyNameInfoDTO.builder().id(1L).name("1").build()
                );
        when(verificationBodyQueryService.getAllActiveVerificationBodiesAccreditedToEmissionTradingScheme(ets))
            .thenReturn(verificationBodies);
        
        List<VerificationBodyNameInfoDTO> result = service.getAllActiveVerificationBodiesAccreditedToAccountEmissionTradingScheme(accountId);
        assertThat(result).isEqualTo(verificationBodies);
        
        verify(verificationBodyQueryService, times(1)).getAllActiveVerificationBodiesAccreditedToEmissionTradingScheme(ets);
    }
    
    @Test
    void getAllActiveVerificationBodiesAccreditedToAccountEmissionTradingScheme_account_not_found() {
        Long accountId = 1L;
        
        when(accountQueryService.getAccountById(accountId)).thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        
        BusinessException be = assertThrows(BusinessException.class, () -> {
            service.getAllActiveVerificationBodiesAccreditedToAccountEmissionTradingScheme(accountId);    
        });
        
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        
        verify(verificationBodyQueryService, never()).getAllActiveVerificationBodiesAccreditedToEmissionTradingScheme(Mockito.any(EmissionTradingScheme.class));
    }
    
}
