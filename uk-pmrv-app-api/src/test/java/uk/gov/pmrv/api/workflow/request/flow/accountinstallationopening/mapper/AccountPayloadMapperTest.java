package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.mapper;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

import java.time.LocalDate;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApprovedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningRequestPayload;

@ExtendWith(MockitoExtension.class)
class AccountPayloadMapperTest {

    private AccountPayloadMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(AccountPayloadMapper.class);
    }

    @Test
    void toAccountDTO() {
        final String accountName = "accountName";
        final CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        final EmissionTradingScheme ets = EmissionTradingScheme.EU_ETS_INSTALLATIONS;
        final String leName = "leName";
        AccountPayload accountPayload = createAccountPayload(accountName, competentAuthority, ets, leName);

        AccountDTO accountDTO = mapper.toAccountDTO(accountPayload);

        assertThat(accountDTO.getName()).isEqualTo(accountName);
        assertThat(accountDTO.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(accountDTO.getEmissionTradingScheme()).isEqualTo(ets);
        assertThat(accountDTO.getLegalEntity().getName()).isEqualTo(leName);
    }

    @Test
    void toInstallationAccountOpeningRequestPayload() {
        final String accountName = "account";
        final CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        final EmissionTradingScheme ets = EmissionTradingScheme.EU_ETS_INSTALLATIONS;
        final String leName = "le";
        final String requestAssigneeUser = "requestAssignee";
        AccountPayload accountPayload = createAccountPayload(accountName, competentAuthority, ets, leName);

        InstallationAccountOpeningRequestPayload requestPayload =
            mapper.toInstallationAccountOpeningRequestPayload(accountPayload, requestAssigneeUser);

        assertThat(requestPayload.getPayloadType()).isEqualTo(RequestPayloadType.INSTALLATION_ACCOUNT_OPENING_REQUEST_PAYLOAD);
        assertThat((requestPayload.getOperatorAssignee())).isEqualTo(requestAssigneeUser);

        AccountPayload retrievedAccountPayload = requestPayload.getAccountPayload();

        assertThat(retrievedAccountPayload.getName()).isEqualTo(accountName);
        assertThat(retrievedAccountPayload.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(retrievedAccountPayload.getEmissionTradingScheme()).isEqualTo(ets);
        assertThat(retrievedAccountPayload.getLegalEntity().getName()).isEqualTo(leName);
    }

    @Test
    void toInstallationAccountOpeningApplicationSubmittedRequestActionPayload() {
        final String accountName = "account1";
        final CompetentAuthority competentAuthority = CompetentAuthority.SCOTLAND;
        final EmissionTradingScheme ets = EmissionTradingScheme.UK_ETS_INSTALLATIONS;
        final String leName = "le1";
        AccountPayload accountPayload = createAccountPayload(accountName, competentAuthority, ets, leName);

        InstallationAccountOpeningApplicationSubmittedRequestActionPayload accountSubmittedPayload = mapper.toInstallationAccountOpeningApplicationSubmittedRequestActionPayload(accountPayload);

        assertThat(accountSubmittedPayload.getPayloadType()).isEqualTo(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED_PAYLOAD);

        AccountPayload retrievedAccountPayload = accountSubmittedPayload.getAccountPayload();

        assertThat(retrievedAccountPayload.getName()).isEqualTo(accountName);
        assertThat(retrievedAccountPayload.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(retrievedAccountPayload.getEmissionTradingScheme()).isEqualTo(ets);
        assertThat(retrievedAccountPayload.getLegalEntity().getName()).isEqualTo(leName);
    }

    @Test
    void toInstallationAccountOpeningApplicationRequestTaskPayload() {
        final String accountName = "account";
        final CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        final EmissionTradingScheme ets = EmissionTradingScheme.UK_ETS_INSTALLATIONS;
        final String leName = "le";
        AccountPayload accountPayload = createAccountPayload(accountName, competentAuthority, ets, leName);

        InstallationAccountOpeningApplicationRequestTaskPayload
            installationAccountOpeningApplicationRequestTaskPayload =
            mapper.toInstallationAccountOpeningApplicationRequestTaskPayload(accountPayload);

        assertThat(installationAccountOpeningApplicationRequestTaskPayload.getPayloadType())
            .isEqualTo(RequestTaskPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD);

        AccountPayload retrievedAccountPayload = installationAccountOpeningApplicationRequestTaskPayload.getAccountPayload();

        assertThat(retrievedAccountPayload.getName()).isEqualTo(accountName);
        assertThat(retrievedAccountPayload.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(retrievedAccountPayload.getEmissionTradingScheme()).isEqualTo(ets);
        assertThat(retrievedAccountPayload.getLegalEntity().getName()).isEqualTo(leName);
    }

    @Test
    void toInstallationAccountOpeningApprovedRequestActionPayload() {
        final String accountName = "account1";
        final CompetentAuthority competentAuthority = CompetentAuthority.SCOTLAND;
        final EmissionTradingScheme ets = EmissionTradingScheme.UK_ETS_INSTALLATIONS;
        final String leName = "le1";
        AccountPayload accountPayload = createAccountPayload(accountName, competentAuthority, ets, leName);

        InstallationAccountOpeningApprovedRequestActionPayload accountApprovedPayload =
            mapper.toInstallationAccountOpeningApprovedRequestActionPayload(accountPayload);

        assertThat(accountApprovedPayload.getPayloadType()).isEqualTo(RequestActionPayloadType.INSTALLATION_ACCOUNT_OPENING_APPROVED_PAYLOAD);

        AccountPayload retrievedAccountPayload = accountApprovedPayload.getAccountPayload();

        assertThat(retrievedAccountPayload.getName()).isEqualTo(accountName);
        assertThat(retrievedAccountPayload.getCompetentAuthority()).isEqualTo(competentAuthority);
        assertThat(retrievedAccountPayload.getEmissionTradingScheme()).isEqualTo(ets);
        assertThat(retrievedAccountPayload.getLegalEntity().getName()).isEqualTo(leName);
    }

    private AccountPayload createAccountPayload(String accountName, CompetentAuthority ca, EmissionTradingScheme ets, String leName) {
        return AccountPayload.builder()
            .accountType(AccountType.INSTALLATION)
            .name(accountName)
            .competentAuthority(ca)
            .emissionTradingScheme(ets)
            .commencementDate(LocalDate.of(2020,8,6))
            .location(LocationOnShoreDTO.builder().build())
            .legalEntity(LegalEntityDTO.builder()
                .type(LegalEntityType.PARTNERSHIP)
                .name(leName)
                .referenceNumber("09546038")
                .noReferenceNumberReason("noCompaniesRefDetails")
                .address(AddressDTO.builder().build())
                .build())
            .build();
    }
}