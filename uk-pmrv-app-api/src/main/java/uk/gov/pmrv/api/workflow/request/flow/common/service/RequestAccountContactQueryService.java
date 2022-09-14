package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

@Service
@RequiredArgsConstructor
public class RequestAccountContactQueryService {

    private final AccountContactQueryService accountContactQueryService;
    private final UserAuthService userAuthService;
    
    public Optional<UserInfoDTO> getRequestAccountContact(Request request, AccountContactType contactType) {
        return accountContactQueryService
            .findContactByAccountAndContactType(request.getAccountId(), contactType)
            .map(userAuthService::getUserByUserId);
    }
    
    public UserInfoDTO getRequestAccountPrimaryContact(Request request) {
        return getRequestAccountContact(request, AccountContactType.PRIMARY)
            .orElseThrow(() -> new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
    }
}
