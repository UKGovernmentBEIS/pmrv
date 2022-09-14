package uk.gov.pmrv.api.mireport.service.generator;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.RoleDTO;
import uk.gov.pmrv.api.authorization.core.service.RoleService;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.mireport.domain.dto.AccountUserContact;
import uk.gov.pmrv.api.mireport.domain.dto.AccountsUsersContactsMiReportResult;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportResult;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportParams;
import uk.gov.pmrv.api.mireport.enumeration.MiReportType;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityAccountDTO;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInfoDTO;

@Service
@RequiredArgsConstructor
public class AccountsUsersContactsReportGenerator implements MiReportGenerator {

    private final OperatorAuthorityQueryService operatorAuthorityQueryService;
    private final AccountQueryService accountQueryService;
    private final AccountContactQueryService accountContactQueryService;
    private final PermitQueryService permitQueryService;
    private final UserAuthService userAuthService;
    private final RoleService roleService;

    @Override
    public MiReportType getReportType() {
        return MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS;
    }

    /**
     * Creates a report for accounts, users and contacts for all the operators who are under the provided competent authority.
     *
     * @param competentAuthority the competent authority
     * @return the reports
     */
    @Override
    @Transactional(readOnly = true)
    public MiReportResult generateMiReport(CompetentAuthority competentAuthority, MiReportParams reportParams) {
        Map<Long, AccountDTO> operatorAccounts = getAccountsByCompetentAuthority(competentAuthority);
        List<Long> operatorAccountIds = new ArrayList<>(operatorAccounts.keySet());
        List<AuthorityInfoDTO> operatorAuthorities = operatorAuthorityQueryService.findByAccountIds(operatorAccountIds);

        Map<Long, Map<AccountContactType, String>> operatorContactsByAccountId = getOperatorAccountContacts(operatorAccountIds);
        Map<Long, PermitEntityAccountDTO> permitByAccountId = permitQueryService.getPermitByAccountIds(operatorAccountIds);
        Map<String, OperatorUserInfoDTO> operatorUserInfoByKeycloakUserId = getOperatorUserInfoByUserIds(operatorAuthorities);
        Map<String, String> roleNameByCode = roleService.getOperatorRoles().stream().collect(Collectors.toMap(RoleDTO::getCode, RoleDTO::getName));

        List<AccountUserContact> payload = operatorAuthorities.stream()
            .map(operatorAuthority -> {
                AccountDTO operatorAccount = operatorAccounts.get(operatorAuthority.getAccountId());
                PermitEntityAccountDTO permit = permitByAccountId.get(operatorAccount.getId());
                OperatorUserInfoDTO operatorUserInfo = operatorUserInfoByKeycloakUserId.get(operatorAuthority.getUserId());
                Set<AccountContactType> accountContactTypes = getAccountContactTypes(operatorContactsByAccountId.get(operatorAccount.getId()),
                    operatorAuthority.getUserId());
                return createAccountUserContact(permit, operatorUserInfo, operatorAuthority, roleNameByCode, operatorAccount, accountContactTypes);
            }).collect(Collectors.toList());

        return AccountsUsersContactsMiReportResult.builder()
            .reportType(getReportType())
            .payload(payload)
            .build();
    }

    private Map<String, OperatorUserInfoDTO> getOperatorUserInfoByUserIds(List<AuthorityInfoDTO> operatorAuthorities) {
        return userAuthService.getUsersWithAttributes(extractKeycloakUserIds(operatorAuthorities),
                OperatorUserInfoDTO.class)
            .stream()
            .collect(Collectors.toMap(OperatorUserInfoDTO::getId, Function.identity()));
    }

    private Map<Long, AccountDTO> getAccountsByCompetentAuthority(CompetentAuthority competentAuthority) {
        return accountQueryService.getAccountsByCompetentAuthority(competentAuthority)
            .stream()
            .collect(Collectors.toMap(AccountDTO::getId, Function.identity()));
    }

    private Map<Long, Map<AccountContactType, String>> getOperatorAccountContacts(List<Long> accountIds) {
        return accountContactQueryService.findContactTypesByAccountIds(accountIds);
    }

    private Set<AccountContactType> getAccountContactTypes(Map<AccountContactType, String> contacts, String userId) {
        return contacts
            .entrySet()
            .stream()
            .filter(accountContactType -> accountContactType.getValue().equals(userId))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());
    }

    private static List<String> extractKeycloakUserIds(List<AuthorityInfoDTO> operatorAuthorities) {
        return operatorAuthorities.stream().map(AuthorityInfoDTO::getUserId).collect(Collectors.toList());
    }

    private static AccountUserContact createAccountUserContact(
        PermitEntityAccountDTO permit,
        OperatorUserInfoDTO operatorUserInfo,
        AuthorityInfoDTO operatorAuthorityInfo,
        Map<String, String> roleNameByCode,
        AccountDTO operatorAccount,
        Set<AccountContactType> accountContactTypes
    ) {
        return AccountUserContact.builder()
            .accountType(operatorAccount.getAccountType())
            .accountId(operatorAccount.getId())
            .accountName(operatorAccount.getName())
            .accountStatus(operatorAccount.getStatus())
            .permitId(Optional.ofNullable(permit).map(PermitEntityAccountDTO::getPermitEntityId).orElse(null))
            .permitType(Optional.ofNullable(permit).map(PermitEntityAccountDTO::getPermitType).map(PermitType::toString).orElse(null))
            .legalEntityName(operatorAccount.getLegalEntity().getName())
            .isPrimaryContact(accountContactTypes.contains(AccountContactType.PRIMARY))
            .isSecondaryContact(accountContactTypes.contains(AccountContactType.SECONDARY))
            .isFinancialContact(accountContactTypes.contains(AccountContactType.FINANCIAL))
            .isServiceContact(accountContactTypes.contains(AccountContactType.SERVICE))
            .authorityStatus(operatorAuthorityInfo.getAuthorityStatus())
            .name(Optional.ofNullable(operatorUserInfo).map(OperatorUserInfoDTO::getFullName).orElse(null))
            .telephone(Optional.ofNullable(operatorUserInfo).map(OperatorUserInfoDTO::getTelephone).orElse(null))
            .lastLogon(
                Optional.ofNullable(operatorUserInfo).filter(operatorUserInfoDTO -> operatorUserInfoDTO.getLastLoginDate() != null)
                    .map(operatorUserInfoDTO -> formatLastLoginDate(operatorUserInfoDTO.getLastLoginDate())).orElse(null))
            .email(Optional.ofNullable(operatorUserInfo).map(OperatorUserInfoDTO::getEmail).orElse(null))
            .role(roleNameByCode.get(operatorAuthorityInfo.getCode()))
            .build();
    }

    private static String formatLastLoginDate(String lastLoginDate) {
        return LocalDateTime.parse(lastLoginDate, DateTimeFormatter.ISO_DATE_TIME).format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss"));
    }
}
