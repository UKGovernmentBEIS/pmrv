package uk.gov.pmrv.api.account.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountHeaderInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchResults;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.EmitterType;
import uk.gov.pmrv.api.account.domain.enumeration.InstallationCategory;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.transform.AccountMapper;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
class AccountQueryServiceTest {

    @InjectMocks
    private AccountQueryService accountQueryService;

    @Mock
    private AccountRepository accountRepository;

    @Mock
    private AccountMapper accountMapper;

    @Test
    void getAccountsByStatus() {
        AccountStatus status = AccountStatus.LIVE;
        Account account = createAccount(1L, "name", status, CompetentAuthority.ENGLAND,
                1L, 1L, "leName", LegalEntityStatus.ACTIVE);
        List<Account> accounts = List.of(account);
        List<AccountDTO> expectedAccounts = List.of(createAccountDTO(account.getName(), account.getLegalEntity().getId(),
                account.getLegalEntity().getName(), account.getCompetentAuthority()));

        when(accountRepository.findAllByStatusIs(status)).thenReturn(accounts);
        when(accountMapper.toAccountDTO(account)).thenReturn(expectedAccounts.get(0));

        // Invoke
        List<AccountDTO> actualAccounts = accountQueryService.getAccountsByStatus(status);

        // Verify
        assertThat(actualAccounts).isEqualTo(expectedAccounts);
        verify(accountRepository, times(1)).findAllByStatusIs(status);
        verify(accountMapper, times(1)).toAccountDTO(account);
    }

    @Test
    void getAccountCa() {
        final Long accountId = 1L;
        Account account =
                createAccount(accountId, "name", AccountStatus.LIVE, CompetentAuthority.ENGLAND, 1L, 1L, "leName",
                        LegalEntityStatus.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        CompetentAuthority accountCa = accountQueryService.getAccountCa(accountId);

        assertThat(accountCa).isEqualTo(CompetentAuthority.ENGLAND);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountCa_not_found() {
        final Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () ->
                accountQueryService.getAccountCa(accountId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountVerificationBodyId() {
        final Long accountId = 1L;
        final Long verificationBodyId = 2L;
        Account account = createAccount(accountId, "name", AccountStatus.LIVE, CompetentAuthority.ENGLAND, verificationBodyId,
                1L, "leName", LegalEntityStatus.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        //assert
        Optional<Long> accountVerificationBody = accountQueryService.getAccountVerificationBodyId(accountId);

        assertThat(accountVerificationBody).isPresent();
        assertEquals(verificationBodyId, accountVerificationBody.get());
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountVerificationBodyId_empty() {
        final Long accountId = 1L;
        Account account = createAccount(accountId, "name", AccountStatus.LIVE, CompetentAuthority.ENGLAND, null,
                1L, "leName", LegalEntityStatus.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        Optional<Long> accountVerificationBody = accountQueryService.getAccountVerificationBodyId(accountId);

        assertThat(accountVerificationBody).isEmpty();
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountVerificationBodyId_account_not_found() {
        final Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () ->
                accountQueryService.getAccountVerificationBodyId(accountId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountInstallationName() {
        final Long accountId = 1L;
        final String installationName = "name";
        Account account =
                createAccount(accountId, installationName, AccountStatus.LIVE, CompetentAuthority.ENGLAND, 1L, 1L, "leName",
                        LegalEntityStatus.ACTIVE);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        String result = accountQueryService.getAccountInstallationName(accountId);

        assertThat(result).isEqualTo(installationName);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountInstallationName_not_found() {
        final Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());
        BusinessException be = assertThrows(BusinessException.class, () ->
                accountQueryService.getAccountInstallationName(accountId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountStatus() {
        final Long accountId = 1L;
        final AccountStatus status = AccountStatus.NEW;
        Account account = Account.builder().id(accountId).status(status).build();

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));

        AccountStatus result = accountQueryService.getAccountStatus(accountId);

        assertThat(result).isEqualTo(status);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountStatus_not_found() {
        final Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        BusinessException be = assertThrows(BusinessException.class, () ->
                accountQueryService.getAccountStatus(accountId));

        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(accountRepository, times(1)).findById(accountId);
    }

    @Test
    void getAccountDetailsDTOById() {
        //prepare
        final String accountName = "account";
        final Long accountId = 1L;
        Account account = createAccount(accountId, accountName, AccountStatus.LIVE, CompetentAuthority.ENGLAND, 1L, 1L, "leName", LegalEntityStatus.ACTIVE);
        AccountDetailsDTO accountDTO = createAccountDetailsDTO(accountName, 1L, "leName");

        when(accountRepository.findAccountById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toAccountDetailsDTO(account)).thenReturn(accountDTO);

        //invoke
        AccountDetailsDTO result = accountQueryService.getAccountDetailsDTOById(accountId);

        //assert
        assertThat(result).isEqualTo(accountDTO);

        verify(accountRepository, times(1)).findAccountById(accountId);
        verify(accountMapper, times(1)).toAccountDetailsDTO(account);
    }

    @Test
    void getAccountDetailsDTOById_not_found() {
        //prepare
        final Long accountId = 1L;

        when(accountRepository.findAccountById(accountId)).thenReturn(Optional.empty());

        //assert
        BusinessException be = assertThrows(BusinessException.class, () ->
                accountQueryService.getAccountDetailsDTOById(accountId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(accountRepository, times(1)).findAccountById(accountId);
        verify(accountMapper, never()).toAccountDetailsDTO(Mockito.any());
    }

    @Test
    void getAccountsByUserAndSearchCriteria_operator() {
        final String accountName1 = "account1";
        final String accountName2 = "account2";
        final String user1 = "user1";
        PmrvUser operatorUser1 = createPmrvUser(user1, RoleType.OPERATOR);
        operatorUser1.setAuthorities(List.of(
                createAccountPmrvAuthority(1L, "code1"),
                createAccountPmrvAuthority(2L, "code2")
        ));

        final AccountSearchCriteria criteria = AccountSearchCriteria.builder().build();

        final List<AccountInfoDTO> accountDTOs =
                List.of(
                        new AccountInfoDTO(1L, accountName1, "EM00001", AccountStatus.LIVE.name(), "lename1"),
                        new AccountInfoDTO(2L, accountName2, "EM00002", AccountStatus.LIVE.name(), "lename2"));

        AccountSearchResults results = AccountSearchResults.builder().accounts(accountDTOs).total(2L).build();

        when(accountRepository.findByAccountIds(List.of(1L, 2L), criteria)).thenReturn(results);

        //invoke
        AccountSearchResults actual = accountQueryService.getAccountsByUserAndSearchCriteria(operatorUser1, criteria);

        //assert
        assertThat(actual.getAccounts()).extracting(AccountInfoDTO::getName)
                .containsOnly(accountName1, accountName2);

        //verify
        verify(accountRepository, times(1)).findByAccountIds(List.of(1L, 2L), criteria);
        verify(accountRepository, never()).findByCompAuth(Mockito.any(CompetentAuthority.class), Mockito.any());
    }

    @Test
    void getAccountsByUserAndSearchCriteria_regulator() {
        PmrvUser regulatorUser = createPmrvUser("user", RoleType.REGULATOR);
        regulatorUser.setAuthorities(List.of(
                createCompAuthPmrvAuthority(CompetentAuthority.ENGLAND)
        ));
        final String accountName1 = "acc1";
        final String accountName2 = "acc2";

        final AccountSearchCriteria criteria = AccountSearchCriteria.builder().build();

        final List<AccountInfoDTO> accountDTOs =
                List.of(
                        new AccountInfoDTO(1L, accountName1, "EM00001", AccountStatus.LIVE.name(), "lename1"),
                        new AccountInfoDTO(2L, accountName2, "EM00002", AccountStatus.LIVE.name(), "lename2"));

        AccountSearchResults results = AccountSearchResults.builder().accounts(accountDTOs).total(2L).build();

        when(accountRepository.findByCompAuth(CompetentAuthority.ENGLAND, criteria)).thenReturn(results);

        //invoke
        AccountSearchResults actual = accountQueryService.getAccountsByUserAndSearchCriteria(regulatorUser, criteria);

        //assert
        assertThat(actual.getAccounts()).extracting(AccountInfoDTO::getName)
                .containsOnly(accountName1, accountName2);

        //verify
        verify(accountRepository, times(1)).findByCompAuth(CompetentAuthority.ENGLAND, criteria);
        verify(accountRepository, never()).findByAccountIds(Mockito.anyList(), Mockito.any());
    }

    @Test
    void getAccountsByOperatorUser() {
        Long accountId = 1L;
        PmrvUser user = createPmrvUser("user", RoleType.OPERATOR);
        user.setAuthorities(List.of(createAccountPmrvAuthority(1L, "code1")));
        Account account = createAccount(accountId, "name", AccountStatus.LIVE, CompetentAuthority.ENGLAND, 1L, 1L, "leName",
                LegalEntityStatus.ACTIVE);
        AccountDTO accountDTO = createAccountDTO("account", 1L, "le", CompetentAuthority.ENGLAND);

        when(accountRepository.findAllEagerLeByIdIn(List.of(accountId)))
                .thenReturn(List.of(account));
        when(accountMapper.toAccountDTO(account)).thenReturn(accountDTO);

        //invoke
        List<AccountDTO> result = accountQueryService.getAccountsByOperatorUser(user);

        assertThat(result).isEqualTo(List.of(accountDTO));
        verify(accountRepository, times(1)).findAllEagerLeByIdIn(List.of(accountId));
        verify(accountMapper, times(1)).toAccountDTO(account);
    }

    @Test
    void getAccountsByRegulatorUserIdAndIds() {
        PmrvUser user = createPmrvUser("user", RoleType.REGULATOR);
        user.setAuthorities(List.of(
                createCompAuthPmrvAuthority(CompetentAuthority.ENGLAND)
        ));
        Long accountId = 1L;
        Account account = createAccount(accountId, "name", AccountStatus.LIVE, CompetentAuthority.ENGLAND, 1L, 1L, "leName",
                LegalEntityStatus.ACTIVE);
        AccountDTO accountDTO = createAccountDTO("account", 1L, "le", CompetentAuthority.ENGLAND);

        when(accountRepository.findByAccountIdsAndCompAuth(List.of(accountId), CompetentAuthority.ENGLAND))
                .thenReturn(List.of(account));
        when(accountMapper.toAccountDTO(account)).thenReturn(accountDTO);

        //invoke
        List<AccountDTO> result = accountQueryService.getAccountsByCompetentAuthorityAndIds(user.getCompetentAuthority(), List.of(accountId));

        assertThat(result).isEqualTo(List.of(accountDTO));
        verify(accountRepository, times(1)).findByAccountIdsAndCompAuth(List.of(accountId), CompetentAuthority.ENGLAND);
        verify(accountMapper, times(1)).toAccountDTO(account);
    }

    @Test
    void getAccountsByVerifierUserIdAndIds() {
        Long vbId = 1L;
        PmrvUser user = createPmrvUser("user", RoleType.VERIFIER);
        user.setAuthorities(List.of(
                createVerBodyPmrvAuthority(vbId)
        ));
        List<Long> accountIds = List.of(1L);
        Account account = createAccount(1L, "account", AccountStatus.LIVE, CompetentAuthority.ENGLAND, vbId,
                1L, "le", LegalEntityStatus.ACTIVE);
        AccountDTO accountDTO = createAccountDTO("account", 1L, "le", CompetentAuthority.ENGLAND);

        when(accountRepository.findByAccountIdsAndVerBody(accountIds, vbId))
                .thenReturn(List.of(account));
        when(accountMapper.toAccountDTO(account)).thenReturn(accountDTO);

        //invoke
        List<AccountDTO> result = accountQueryService.getAccountsByVerifierUserIdAndIds(user, accountIds);

        assertThat(result).isEqualTo(List.of(accountDTO));
        verify(accountRepository, times(1)).findByAccountIdsAndVerBody(accountIds, vbId);
        verify(accountMapper, times(1)).toAccountDTO(account);
    }

    @Test
    void isExistingActiveAccountName() {
        boolean result = accountQueryService.isExistingActiveAccountName("accountName");
        assertThat(result).isFalse();
    }

    @Test
    void getAccountHeaderInfoById() {
        Long accountId = 1L;
        String accountName = "accountName";
        AccountStatus accountStatus = AccountStatus.AWAITING_SURRENDER;
        EmitterType emitterType = EmitterType.GHGE;
        InstallationCategory installationCategory = InstallationCategory.B;
        Account account = Account.builder()
            .id(accountId)
            .name(accountName)
            .status(accountStatus)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .accountType(AccountType.INSTALLATION)
            .emitterType(emitterType)
            .installationCategory(installationCategory)
            .build();

        AccountHeaderInfoDTO accountHeaderInfoDTO = AccountHeaderInfoDTO.builder()
            .name(accountName)
            .status(accountStatus)
            .emitterType(emitterType)
            .installationCategory(installationCategory)
            .build();

        List<AccountStatus> notAcceptedAccountStatuses = List.of(AccountStatus.UNAPPROVED, AccountStatus.DENIED);

        when(accountRepository.findByIdAndStatusNotIn(accountId, notAcceptedAccountStatuses)).thenReturn(Optional.of(account));
        when(accountMapper.toAccountHeaderInfoDTO(account)).thenReturn(accountHeaderInfoDTO);

        Optional<AccountHeaderInfoDTO> result = accountQueryService.getAccountHeaderInfoById(accountId);

        assertEquals(Optional.of(accountHeaderInfoDTO), result);
        verify(accountRepository, times(1)).findByIdAndStatusNotIn(accountId, notAcceptedAccountStatuses);
        verify(accountMapper, times(1)).toAccountHeaderInfoDTO(account);
    }

    @Test
    void getAccountHeaderInfoById_invalid_status() {
        Long accountId = 1L;

        List<AccountStatus> notAcceptedAccountStatuses = List.of(AccountStatus.UNAPPROVED, AccountStatus.DENIED);

        when(accountRepository.findByIdAndStatusNotIn(accountId, notAcceptedAccountStatuses)).thenReturn(Optional.empty());

        Optional<AccountHeaderInfoDTO> accountHeaderInfoById = accountQueryService.getAccountHeaderInfoById(accountId);

        assertEquals(Optional.empty(), accountHeaderInfoById);
        verify(accountRepository, times(1)).findByIdAndStatusNotIn(accountId, notAcceptedAccountStatuses);
        verifyNoInteractions(accountMapper);
    }

    @Test
    void getAccountDTOById() {
        Long accountId = 1L;
        Account account =
                createAccount(accountId, "name", AccountStatus.LIVE, CompetentAuthority.ENGLAND, 1L, 1L, "leName",
                        LegalEntityStatus.ACTIVE);
        AccountDTO accountDTO = createAccountDTO("account", 1L, "leName", CompetentAuthority.ENGLAND);

        when(accountRepository.findById(accountId)).thenReturn(Optional.of(account));
        when(accountMapper.toAccountDTO(account)).thenReturn(accountDTO);

        AccountDTO result = accountQueryService.getAccountDTOById(accountId);

        assertThat(result).isEqualTo(accountDTO);
        verify(accountRepository, times(1)).findById(accountId);
        verify(accountMapper, times(1)).toAccountDTO(account);
    }

    @Test
    void getAccountDTOById_not_found() {
        Long accountId = 1L;

        when(accountRepository.findById(accountId)).thenReturn(Optional.empty());

        //assert
        BusinessException be = assertThrows(BusinessException.class, () ->
                accountQueryService.getAccountDTOById(accountId));
        assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);

        verify(accountRepository, times(1)).findById(accountId);
        verifyNoInteractions(accountMapper);
    }

    private AccountDetailsDTO createAccountDetailsDTO(String accountName, Long legalEntityId, String legalEntityName) {
        return AccountDetailsDTO.builder()
                .name(accountName)
                .legalEntityName(legalEntityName)
                .location(LocationOnShoreDTO.builder()
                        .gridReference("20 20 20 S")
                        .address(AddressDTO.builder()
                                .city("city")
                                .country("GR")
                                .line1("line")
                                .postcode("postcode")
                                .build()).build())
                .accountType(AccountType.INSTALLATION)
                .build();
    }

    private AccountDTO createAccountDTO(String accountName, Long legalEntityId, String legalEntityName, CompetentAuthority ca) {
        return AccountDTO.builder()
                .name(accountName)
                .legalEntity(createLegalEntityDTO(legalEntityId, legalEntityName))
                .location(LocationOnShoreDTO.builder()
                        .gridReference("20 20 20 S")
                        .address(AddressDTO.builder()
                                .city("city")
                                .country("GR")
                                .line1("line")
                                .postcode("postcode")
                                .build()).build())
                .accountType(AccountType.INSTALLATION)
                .competentAuthority(ca)
                .commencementDate(LocalDate.now(ZoneId.of("UTC")))
                .build();
    }

    private Account createAccount(
            Long accountId, String accountName, AccountStatus accountStatus, CompetentAuthority ca, Long verificationBodyId,
            Long legalEntityId, String legalEntityName, LegalEntityStatus legalEntityStatus) {
        return Account.builder()
                .id(accountId)
                .name(accountName)
                .legalEntity(createLegalEntity(legalEntityId, legalEntityName, legalEntityStatus))
                .status(accountStatus)
                .location(LocationOnShore.builder()
                        .gridReference("20 20 20 S")
                        .address(Address.builder()
                                .city("city")
                                .country("GR")
                                .line1("line")
                                .postcode("postcode")
                                .build())
                        .build())
                .accountType(AccountType.INSTALLATION)
                .competentAuthority(ca)
                .verificationBodyId(verificationBodyId)
                .commencementDate(LocalDate.now(ZoneId.of("UTC")))
                .build();
    }


    private LegalEntityDTO createLegalEntityDTO(Long legalEntityId, String legalEntityName) {
        return LegalEntityDTO.builder()
                .id(legalEntityId)
                .name(legalEntityName)
                .address(AddressDTO.builder()
                        .city("city")
                        .country("GR")
                        .line1("line")
                        .postcode("postcode")
                        .build())
                .type(LegalEntityType.LIMITED_COMPANY)
                .referenceNumber("88888888")
                .build();
    }

    private LegalEntity createLegalEntity(Long legalEntityId, String legalEntityName, LegalEntityStatus status) {
        return LegalEntity.builder()
                .id(legalEntityId)
                .name(legalEntityName)
                .status(status)
                .location(
                        LocationOnShore.builder()
                                .gridReference("grid")
                                .address(
                                        Address.builder()
                                                .city("city")
                                                .country("GR")
                                                .line1("line")
                                                .postcode("postcode")
                                                .build())
                                .build())
                .type(LegalEntityType.LIMITED_COMPANY)
                .referenceNumber("88888888").build();
    }

    private PmrvUser createPmrvUser(String userId, RoleType roleType) {
        return PmrvUser.builder()
                .userId(userId)
                .email("email@email")
                .firstName("fn")
                .lastName("ln")
                .roleType(roleType)
                .build();
    }

    private PmrvAuthority createAccountPmrvAuthority(Long accountId, String code) {
        return PmrvAuthority.builder()
                .accountId(accountId)
                .code(code)
                .build();
    }

    private PmrvAuthority createCompAuthPmrvAuthority(CompetentAuthority compAuth) {
        return PmrvAuthority.builder()
                .competentAuthority(compAuth)
                .permissions(List.of(Permission.PERM_CA_USERS_EDIT))
                .build();
    }

    private PmrvAuthority createVerBodyPmrvAuthority(Long verBody) {
        return PmrvAuthority.builder()
                .verificationBodyId(verBody)
                .permissions(List.of(Permission.PERM_VB_USERS_EDIT))
                .build();
    }
}
