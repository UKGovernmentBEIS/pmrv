package uk.gov.pmrv.api.workflow.request.core.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.authorization.rules.services.resource.AccountRequestAuthorizationResourceService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestAction;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCategory;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;
import uk.gov.pmrv.api.workflow.request.core.validation.EnabledWorkflowValidator;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.AccountPayload;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.PermitSurrenderCreateValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.service.PermitVariationCreateValidator;

@ExtendWith(MockitoExtension.class)
class RequestServiceTest {

    @InjectMocks
    private RequestService requestService;

    @Mock
    private RequestRepository requestRepository;
    
    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;
    
    @Mock
    private AccountRequestAuthorizationResourceService accountRequestAuthorizationResourceService;
    
    @Mock
    private PermitSurrenderCreateValidator permitSurrenderCreateValidator;

    @Mock
    private PermitVariationCreateValidator permitVariationCreateValidator;
    
    @Spy
    private ArrayList<RequestCreateValidator> requestCreateValidators;

    @Mock
    private EnabledWorkflowValidator enabledWorkflowValidator;
    
    @Test
    void addActionToRequest() {
    	final String user = "user";
    	final RequestActionType type = RequestActionType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_SUBMITTED;
        Request request = createRequest("1", "1", 1L, RequestType.INSTALLATION_ACCOUNT_OPENING);
        InstallationAccountOpeningApplicationSubmittedRequestActionPayload payload = InstallationAccountOpeningApplicationSubmittedRequestActionPayload
            .builder()
            .accountPayload(createAccountPayload("account"))
            .build();
        
        //assert before
        assertThat(request.getRequestActions()).isEmpty();
        
        when(requestActionUserInfoResolver.getUserFullName(user)).thenReturn("submitter name");
        
        //invoke
        requestService.addActionToRequest(request, 
        		payload, 
        		type, 
        		user);
        
        //verify
        verify(requestActionUserInfoResolver, times(1)).getUserFullName(user);
        
        //assert
        assertThat(request.getRequestActions()).hasSize(1);
        assertThat(request.getRequestActions()).extracting(RequestAction::getType).containsOnly(type);
        assertThat(request.getRequestActions()).extracting(RequestAction::getPayload).containsOnly(payload);
    }

    @Test
    void updateRequestStatus() {
        Request request = Request.builder().id("1").status(RequestStatus.IN_PROGRESS).build();
        Request savedRequest = Request.builder().id("1").status(RequestStatus.APPROVED).build();

        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        // Invoke
        requestService.updateRequestStatus(request.getId(), RequestStatus.APPROVED);

        // Verify
        verify(requestRepository, times(1)).save(request);
    }
    
    @Test
    void terminateRequest() {
    	//prepare data
        String processId = "1";
        Request request = createRequest("1", processId, 1L, RequestType.PERMIT_ISSUANCE);
        request.setPayload(PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
        .build()
        );
        request.setStatus(RequestStatus.IN_PROGRESS);
        
        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));
        
        //assert before
        assertThat(request.getStatus()).isEqualTo(RequestStatus.IN_PROGRESS);
        
        //invoke
        requestService.terminateRequest(request.getId(), processId);
        
        //assert
        assertThat(request.getStatus()).isEqualTo(RequestStatus.COMPLETED);
        assertThat(request.getPayload()).isNull();

        //verify
        verify(requestRepository, times(1)).save(request);
        verify(requestRepository, never()).delete(request);
    }

    @Test
    void terminateRequest_system_message_notification() {
        //prepare data
        String processId = "1";
        Request request = createRequest("1", processId, 1L, RequestType.SYSTEM_MESSAGE_NOTIFICATION);
        request.setPayload(PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.SYSTEM_MESSAGE_NOTIFICATION_REQUEST_PAYLOAD)
            .build()
        );

        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        //invoke
        requestService.terminateRequest(request.getId(), processId);

        //verify
        verify(requestRepository, times(1)).delete(request);
        verify(requestRepository, never()).save(request);
    }

    @Test
    void terminateRequest_not_same_process_instance() {
        //prepare data
        String processId = "1";
        Request request = createRequest("1","2", 1L, RequestType.PERMIT_ISSUANCE);
        request.setStatus(RequestStatus.IN_PROGRESS);

        when(requestRepository.findById(request.getId())).thenReturn(Optional.of(request));

        //invoke
        requestService.terminateRequest(request.getId(), processId);

        //verify
        verifyNoMoreInteractions(requestRepository);
    }
    
    @Test
    void getAvailableWorkflows() {
        requestCreateValidators.add(permitSurrenderCreateValidator);
        when(enabledWorkflowValidator.isWorkflowEnabled(any(RequestType.class))).thenReturn(true);

        final RequestCategory category = RequestCategory.PERMIT;
        final PmrvUser user = PmrvUser.builder().userId("user").build();
        final long accountId = 1L;
        final RequestCreateValidationResult result = RequestCreateValidationResult.builder().valid(true).build();
        final HashSet<String> actionTypes = new HashSet<>();
        actionTypes.add(RequestCreateActionType.AER.name());
        actionTypes.add(RequestCreateActionType.PERMIT_SURRENDER.name());
        actionTypes.add(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name());

        when(accountRequestAuthorizationResourceService.findRequestCreateActionsByAccountId(user, accountId))
            .thenReturn(actionTypes);
        when(permitSurrenderCreateValidator.getType()).thenReturn(RequestCreateActionType.PERMIT_SURRENDER);
        when(permitSurrenderCreateValidator.validateAction(accountId)).thenReturn(result);

        final Map<RequestCreateActionType, RequestCreateValidationResult> availableWorkflows = 
            requestService.getAvailableWorkflows(accountId, user, category);

        verify(accountRequestAuthorizationResourceService, times(1))
            .findRequestCreateActionsByAccountId(user, accountId);
        verify(permitSurrenderCreateValidator, times(1)).validateAction(accountId);
        
        assertThat(availableWorkflows).containsExactly(Map.entry(RequestCreateActionType.PERMIT_SURRENDER, result));
    }

    @Test
    void getAvailableWorkflows_exclude_disallowed_workflows() {
        requestCreateValidators.add(permitSurrenderCreateValidator);
        requestCreateValidators.add(permitVariationCreateValidator);
        when(enabledWorkflowValidator.isWorkflowEnabled(RequestType.PERMIT_VARIATION)).thenReturn(true);
        when(enabledWorkflowValidator.isWorkflowEnabled(RequestType.PERMIT_SURRENDER)).thenReturn(false);

        final RequestCategory category = RequestCategory.PERMIT;
        final PmrvUser user = PmrvUser.builder().userId("user").build();
        final long accountId = 1L;
        final RequestCreateValidationResult result = RequestCreateValidationResult.builder().valid(true).build();
        final HashSet<String> actionTypes = new HashSet<>();
        actionTypes.add(RequestCreateActionType.PERMIT_VARIATION.name());
        actionTypes.add(RequestCreateActionType.PERMIT_SURRENDER.name());
        actionTypes.add(RequestCreateActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_APPLICATION.name());

        when(accountRequestAuthorizationResourceService.findRequestCreateActionsByAccountId(user, accountId))
            .thenReturn(actionTypes);
        when(permitSurrenderCreateValidator.getType()).thenReturn(RequestCreateActionType.PERMIT_SURRENDER);
        when(permitVariationCreateValidator.getType()).thenReturn(RequestCreateActionType.PERMIT_VARIATION);
        when(permitVariationCreateValidator.validateAction(accountId)).thenReturn(result);

        final Map<RequestCreateActionType, RequestCreateValidationResult> availableWorkflows =
            requestService.getAvailableWorkflows(accountId, user, category);

        verify(accountRequestAuthorizationResourceService, times(1))
            .findRequestCreateActionsByAccountId(user, accountId);
        verify(permitVariationCreateValidator, times(1)).validateAction(accountId);
        verify(permitSurrenderCreateValidator, times(0)).validateAction(accountId);

        assertThat(availableWorkflows).containsExactly(Map.entry(RequestCreateActionType.PERMIT_VARIATION, result));
    }
    
    private Request createRequest(String requestId, String processInstanceId, Long accountId, RequestType type) {
        Request request = new Request();
        request.setId(requestId);
        request.setAccountId(accountId);
        request.setProcessInstanceId(processInstanceId);
        request.setType(type);
        return request;
    }
    
    private AccountPayload createAccountPayload(String name) {
        return AccountPayload.builder()
            .accountType(AccountType.INSTALLATION)
            .name(name)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .commencementDate(LocalDate.of(2020,8,6))
            .location(new LocationDTO(LocationType.ONSHORE))
            .legalEntity(LegalEntityDTO.builder()
                .type(LegalEntityType.PARTNERSHIP)
                .name("myle")
                .referenceNumber("09546038")
                .noReferenceNumberReason("noCompaniesRefDetails")
                .address(AddressDTO.builder()
                    .line1("line1")
                    .line2("line2")
                    .city("city")
                    .country("GR")
                    .postcode("postcode").build()).build()).build();
    }
    
}
