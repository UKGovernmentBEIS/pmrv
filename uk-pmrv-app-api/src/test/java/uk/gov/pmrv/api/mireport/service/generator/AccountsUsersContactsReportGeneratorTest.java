package uk.gov.pmrv.api.mireport.service.generator;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.authorization.AuthorityConstants.OPERATOR_ROLE_CODE;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.service.AccountContactQueryService;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.domain.Permission;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.AuthorityRoleDTO;
import uk.gov.pmrv.api.authorization.core.domain.dto.RoleDTO;
import uk.gov.pmrv.api.authorization.core.service.RoleService;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityQueryService;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.mireport.domain.dto.AccountUserContact;
import uk.gov.pmrv.api.mireport.domain.dto.AccountsUsersContactsMiReportResult;
import uk.gov.pmrv.api.mireport.domain.dto.EmptyMiReportParams;
import uk.gov.pmrv.api.mireport.enumeration.MiReportType;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityAccountDTO;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInfoDTO;

@ExtendWith(MockitoExtension.class)
public class AccountsUsersContactsReportGeneratorTest {

    @InjectMocks
    private AccountsUsersContactsReportGenerator service;

    @Mock
    private AccountQueryService accountQueryService;

    @Mock
    private PermitQueryService permitQueryService;

    @Mock
    private UserAuthService userAuthService;

    @Mock
    private RoleService roleService;

    @Mock
    private OperatorAuthorityQueryService operatorAuthorityQueryService;

    @Mock
    private AccountContactQueryService accountContactQueryService;

    @Test
    public void getReportType() {
        assertThat(service.getReportType()).isEqualTo(MiReportType.LIST_OF_ACCOUNTS_USERS_CONTACTS);
    }

    @ParameterizedTest
    @MethodSource("permitValues")
    public void generateMiReport(String permitId, PermitType permitType) {
        PmrvUser pmrvUser = createPmrvUser("1", RoleType.REGULATOR);
        EmptyMiReportParams reportParams = EmptyMiReportParams.builder().build();
        PermitEntityAccountDTO permitEntityAccountDTO = mock(PermitEntityAccountDTO.class);
        Long regulatorAccountId = 1L;
        Long operatorAccountId = 2L;
        String operatorUserId = "userId";
        pmrvUser.setAuthorities(List.of(createCompAuthPmrvAuthority(CompetentAuthority.ENGLAND, regulatorAccountId)));
        Map<AccountContactType, String> contacts = Map.of(AccountContactType.PRIMARY, operatorUserId, AccountContactType.SERVICE, operatorUserId);
        AccountDTO operatorAccountDto = createOperatorAccountDTO(operatorAccountId);
        AuthorityInfoDTO operatorAuthorityInfoDTO = AuthorityInfoDTO.builder()
            .userId(operatorUserId)
            .accountId(operatorAccountId)
            .code(OPERATOR_ROLE_CODE)
            .authorityStatus(AuthorityStatus.ACTIVE)
            .build();
        AuthorityRoleDTO operatorAuthorityRoleDTO = AuthorityRoleDTO.builder().authorityStatus(AuthorityStatus.ACTIVE).userId(operatorUserId).build();
        OperatorUserInfoDTO operatorUserInfoDTO = OperatorUserInfoDTO.builder()
            .id(operatorUserId)
            .firstName("firstname")
            .lastName("lastname")
            .email("test@test.com")
            .phoneNumber("6939")
            .phoneNumberCode("30")
            .lastLoginDate(DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now()))
            .build();

        when(permitEntityAccountDTO.getPermitEntityId()).thenReturn(permitId);
        when(permitEntityAccountDTO.getPermitType()).thenReturn(permitType);
        when(accountQueryService.getAccountsByCompetentAuthority(pmrvUser.getCompetentAuthority())).thenReturn(
            List.of(operatorAccountDto));
        when(permitQueryService.getPermitByAccountIds(List.of(operatorAccountId))).thenReturn(Map.of(operatorAccountId, permitEntityAccountDTO));
        when(accountContactQueryService.findContactTypesByAccountIds(List.of(operatorAccountId))).thenReturn(Map.of(operatorAccountId, contacts));
        when(operatorAuthorityQueryService.findByAccountIds(Collections.singletonList(operatorAccountId))).thenReturn(List.of(operatorAuthorityInfoDTO));
        when(userAuthService.getUsersWithAttributes(Collections.singletonList(operatorAuthorityRoleDTO.getUserId()), OperatorUserInfoDTO.class)).thenReturn(
            Collections.singletonList(operatorUserInfoDTO));
        when(roleService.getOperatorRoles()).thenReturn(List.of(RoleDTO.builder().code(OPERATOR_ROLE_CODE).name("Operator").build()));

        AccountsUsersContactsMiReportResult report = (AccountsUsersContactsMiReportResult) service.generateMiReport(pmrvUser.getCompetentAuthority(), reportParams);

        assertThat(report.getPayload().size()).isEqualTo(1);
        AccountUserContact accountUserContact = report.getPayload().get(0);

        assertThat(accountUserContact.getAccountId()).isEqualTo(operatorAccountDto.getId());
        assertThat(accountUserContact.getAccountName()).isEqualTo(operatorAccountDto.getName());
        assertThat(accountUserContact.getAccountStatus()).isEqualTo(operatorAccountDto.getStatus());
        assertThat(accountUserContact.getAccountType()).isEqualTo(operatorAccountDto.getAccountType());
        assertThat(accountUserContact.getAuthorityStatus()).isEqualTo(operatorAuthorityInfoDTO.getAuthorityStatus());
        assertThat(accountUserContact.getLegalEntityName()).isEqualTo(operatorAccountDto.getLegalEntity().getName());
        assertThat(accountUserContact.isFinancialContact()).isFalse();
        assertThat(accountUserContact.isSecondaryContact()).isFalse();
        assertThat(accountUserContact.isPrimaryContact()).isTrue();
        assertThat(accountUserContact.isServiceContact()).isTrue();
        assertThat(accountUserContact.getPermitId()).isEqualTo(permitId);
        assertThat(accountUserContact.getPermitType()).isEqualTo(Optional.ofNullable(permitType).map(PermitType::toString).orElse(null));
        assertThat(accountUserContact.getEmail()).isEqualTo(operatorUserInfoDTO.getEmail());
        assertThat(accountUserContact.getTelephone()).isEqualTo(operatorUserInfoDTO.getTelephone());
        assertThat(accountUserContact.getRole()).isEqualTo("Operator");
        assertThat(accountUserContact.getLastLogon()).isEqualTo(
            LocalDateTime.parse(operatorUserInfoDTO.getLastLoginDate(), DateTimeFormatter.ISO_DATE_TIME)
                .format(DateTimeFormatter.ofPattern("dd MMMM yyyy HH:mm:ss")));
    }

    private AccountDTO createOperatorAccountDTO(Long operatorAccountId) {
        return AccountDTO.builder().id(operatorAccountId).status(AccountStatus.LIVE).accountType(AccountType.INSTALLATION).legalEntity(
            LegalEntityDTO.builder().name("legal").build()).build();
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

    private PmrvAuthority createCompAuthPmrvAuthority(CompetentAuthority compAuth, Long accountId) {
        return PmrvAuthority.builder()
            .accountId(accountId)
            .competentAuthority(compAuth)
            .permissions(List.of(Permission.PERM_CA_USERS_EDIT))
            .build();
    }

    private static Stream<Arguments> permitValues() {
        return Stream.of(
            Arguments.of("permitId", PermitType.GHGE),
            Arguments.of("permitId", PermitType.HSE),
            Arguments.of(null, PermitType.GHGE),
            Arguments.of(null, PermitType.HSE),
            Arguments.of(null, null)
        );
    }
}
