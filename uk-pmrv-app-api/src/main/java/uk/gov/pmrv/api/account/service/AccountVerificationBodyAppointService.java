package uk.gov.pmrv.api.account.service;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.event.AccountVerificationBodyAppointedEvent;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;

@RequiredArgsConstructor
@Service
public class AccountVerificationBodyAppointService {
    
    private final AccountQueryService accountQueryService;
    private final VerificationBodyQueryService verificationBodyQueryService;
    private final AccountRepository accountRepository;
    private final AccountVerificationBodyNotificationService accountVerificationBodyNotificationService;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public void appointVerificationBodyToAccount(Long verificationBodyId, Long accountId) {
        Account account = accountQueryService.getApprovedAccountById(accountId).orElseThrow(() -> {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, accountId);
        });
        
        accountQueryService
            .getAccountVerificationBodyId(account.getId())
            .ifPresent(v ->{
                throw new BusinessException(ErrorCode.VERIFICATION_BODY_ALREADY_APPOINTED_TO_ACCOUNT);
            });
        
        if(!verificationBodyQueryService.existsActiveVerificationBodyById(verificationBodyId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, verificationBodyId);
        }
        
        EmissionTradingScheme accountEmissionTradingScheme = account.getEmissionTradingScheme();
        if(!verificationBodyQueryService.isVerificationBodyAccreditedToEmissionTradingScheme(
                verificationBodyId, accountEmissionTradingScheme)) {
            throw new BusinessException(ErrorCode.VERIFICATION_BODY_NOT_ACCREDITED_TO_ACCOUNTS_EMISSION_TRADING_SCHEME);
        }
        
        //appoint
        account.setVerificationBodyId(verificationBodyId);
        accountRepository.save(account);
        
        //notify
        accountVerificationBodyNotificationService
            .notifyUsersForVerificationBodyApppointment(verificationBodyId, account);
        
        //publish event
        eventPublisher.publishEvent(
                AccountVerificationBodyAppointedEvent.builder()
                                .accountId(account.getId())
                                .verificationBodyId(verificationBodyId).build());
    }

    @Transactional
    public void replaceVerificationBodyToAccount(Long verificationBodyId, Long accountId) {
        // Validate that account exist
        Account account = accountQueryService.getApprovedAccountById(accountId).orElseThrow(() -> {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, accountId);
        });

        // Validate that VB exist
        if(!verificationBodyQueryService.existsActiveVerificationBodyById(verificationBodyId)) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND, verificationBodyId);
        }
        
        if(account.getVerificationBodyId() == null) {
            throw new BusinessException(ErrorCode.VERIFICATION_BODY_NOT_APPOINTED_TO_ACCOUNT);
        }
        
        if(account.getVerificationBodyId().equals(verificationBodyId)){
            throw new BusinessException(ErrorCode.VERIFICATION_BODY_ALREADY_APPOINTED_TO_ACCOUNT);
        }

        EmissionTradingScheme accountEmissionTradingScheme = account.getEmissionTradingScheme();
        if(!verificationBodyQueryService.isVerificationBodyAccreditedToEmissionTradingScheme(
                verificationBodyId, accountEmissionTradingScheme)) {
            throw new BusinessException(ErrorCode.VERIFICATION_BODY_NOT_ACCREDITED_TO_ACCOUNTS_EMISSION_TRADING_SCHEME);
        }

        // Update account vb appointment and delete VB contact site
        account.setVerificationBodyId(verificationBodyId);
        account.getContacts().remove(AccountContactType.VB_SITE);
        accountRepository.save(account);

        // Notify
        accountVerificationBodyNotificationService
                .notifyUsersForVerificationBodyApppointment(verificationBodyId, account);

        // TODO Publish event for change request is PENDING https://pmo.trasys.be/confluence/display/PMRV/Replace+verification+body
        /*eventPublisher.publishEvent(AccountVerificationBodyAppointedEvent.builder()
                .accountId(accountId).verificationBodyId(verificationBodyId).build());*/
    }
}
