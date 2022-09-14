package uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.handler;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.service.AccountAmendService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskValidationService;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningAmendApplicationRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class InstallationAccountOpeningAmendApplicationActionHandlerTest {

    @InjectMocks
    private InstallationAccountOpeningAmendApplicationActionHandler handler;

	@Mock
	private RequestTaskService requestTaskService;

    @Mock
    private AccountAmendService accountAmendService;

    @Mock
    private RequestTaskValidationService requestTaskValidationService;

    @Test
    void doProcess() {
    	//prepare data
    	final Long accountId = 1L;
    	PmrvUser userSubmitted = PmrvUser.builder().userId("user1").build();
    	RequestTask requestTask = createRequestTask(userSubmitted, accountId);
        AccountPayload initialAccountPayload = createAccountPayload("accountname", "leName");
        InstallationAccountOpeningApplicationRequestTaskPayload requestTaskPayload = InstallationAccountOpeningApplicationRequestTaskPayload
            .builder()
            .payloadType(RequestTaskPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD)
            .accountPayload(initialAccountPayload)
            .build();
        requestTask.setPayload(requestTaskPayload);
        AccountDTO initialAccountDTO = createAccountDTO("accountname", "leName");

		AccountPayload amendAccountPayload = createAccountPayload("accountname", "leName2");
        InstallationAccountOpeningAmendApplicationRequestTaskActionPayload amendAccountSubmitPayload = InstallationAccountOpeningAmendApplicationRequestTaskActionPayload
            .builder()
            .payloadType(RequestTaskActionPayloadType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION_PAYLOAD)
            .accountPayload(amendAccountPayload)
            .build();

        PmrvUser userAmend = PmrvUser.builder().userId("user2").build();
		AccountDTO amendAccountDTO = createAccountDTO("accountname", "leName2");

        InstallationAccountOpeningApplicationRequestTaskPayload newRequestTaskPayload = InstallationAccountOpeningApplicationRequestTaskPayload
            .builder()
            .payloadType(RequestTaskPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD)
            .accountPayload(amendAccountPayload)
            .build();

		when(requestTaskService.findTaskById(10L)).thenReturn(requestTask);
		doNothing().when(requestTaskValidationService).validateRequestTaskPayload(newRequestTaskPayload);

		//invoke
    	handler.process(requestTask.getId(), RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION, userAmend, amendAccountSubmitPayload);

    	//verify
    	verify(accountAmendService, times(1)).amendAccount(accountId, initialAccountDTO, amendAccountDTO, userAmend);
        verify(requestTaskService, times(1)).updateRequestTaskPayload(requestTask, newRequestTaskPayload);
        verify(requestTaskValidationService, times(1)).validateRequestTaskPayload(newRequestTaskPayload);
    }

    private RequestTask createRequestTask(PmrvUser user, Long accountId) {
    	return RequestTask.builder()
				.id(10L)
				.request(Request.builder()
						.type(RequestType.INSTALLATION_ACCOUNT_OPENING)
						.competentAuthority(CompetentAuthority.WALES)
						.status(RequestStatus.IN_PROGRESS)
						.accountId(accountId)
						.requestActions(new ArrayList<>())
						.build())
				.build();
    }

    private AccountPayload createAccountPayload(String accountName, String leName) {
        return AccountPayload.builder()
	            .accountType(AccountType.INSTALLATION)
	            .name(accountName)
	            .competentAuthority(CompetentAuthority.WALES)
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

    private AccountDTO createAccountDTO(String accountName, String leName) {
        return AccountDTO.builder()
        		.accountType(AccountType.INSTALLATION)
                .name(accountName)
                .competentAuthority(CompetentAuthority.WALES)
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
