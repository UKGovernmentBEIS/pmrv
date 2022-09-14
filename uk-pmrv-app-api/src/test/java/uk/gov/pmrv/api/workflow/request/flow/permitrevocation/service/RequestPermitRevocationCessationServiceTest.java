package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.PERMIT_REVOCATION_CESSATION_SUBMIT_PAYLOAD;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountStatusService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessation;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationCompletedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationContainer;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationDeterminationOutcome;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationOfficialNoticeType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitCessationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.cessation.PermitSaveCessationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.mapper.PermitCessationCompletedRequestActionPayloadMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitCessationNotifyOperatorValidator;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.service.notification.PermitRevocationOfficialNoticeService;

@ExtendWith(MockitoExtension.class)
class RequestPermitRevocationCessationServiceTest {

    @InjectMocks
    private RequestPermitRevocationCessationService service;

    @Mock
    private PermitCessationNotifyOperatorValidator cessationNotifyOperatorValidator;

    @Mock
    private AccountStatusService accountStatusService;

    @Mock
    private RequestService requestService;
    
    @Mock
    private PermitRevocationOfficialNoticeService permitRevocationOfficialNoticeService;

    @Mock
    private PermitCessationCompletedRequestActionPayloadMapper cessationCompletedRequestActionPayloadMapper;


    @Test
    void applySaveCessation() {

        final PermitCessation cessation = PermitCessation.builder()
            .determinationOutcome(PermitCessationDeterminationOutcome.APPROVED)
            .allowancesSurrenderDate(LocalDate.now())
            .numberOfSurrenderAllowances(10)
            .annualReportableEmissions(BigDecimal.valueOf(25000.25))
            .subsistenceFeeRefunded(false)
            .noticeType(PermitCessationOfficialNoticeType.NO_PROSPECT_OF_FURTHER_ALLOWANCES)
            .notes("notes")
            .build();
        final PermitSaveCessationRequestTaskActionPayload taskActionPayload =
            PermitSaveCessationRequestTaskActionPayload.builder()
                .cessation(cessation)
                .cessationCompleted(true)
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_REVOCATION_CESSATION_SUBMIT)
            .payload(PermitCessationSubmitRequestTaskPayload.builder()
                .payloadType(PERMIT_REVOCATION_CESSATION_SUBMIT_PAYLOAD)
                .cessationContainer(PermitCessationContainer.builder().allowancesSurrenderRequired(false).build())
                .cessationCompleted(false)
                .build())
            .build();

        service.applySaveCessation(requestTask, taskActionPayload);

        final PermitCessationSubmitRequestTaskPayload taskPayload =
            (PermitCessationSubmitRequestTaskPayload) requestTask.getPayload();

        assertEquals(taskPayload.getCessationContainer().getCessation(), taskActionPayload.getCessation());
        assertEquals(taskPayload.getCessationCompleted(), taskActionPayload.getCessationCompleted());
    }

    @Test
    void executeNotifyOperatorActions() {
    	final PermitRevocationRequestPayload requestPayload = PermitRevocationRequestPayload.builder()
		        .payloadType(RequestPayloadType.PERMIT_REVOCATION_REQUEST_PAYLOAD)
		        .regulatorAssignee("regulatorAssignee")
		        .build();
        final Request request = Request.builder()
        	.id("1")
            .type(RequestType.PERMIT_REVOCATION)
            .accountId(1L)
            .payload(requestPayload)
            .build();
        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload =
            NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .decisionNotification(DecisionNotification.builder().build())
                .build();
        final PermitCessationSubmitRequestTaskPayload requestTaskPayload =
            PermitCessationSubmitRequestTaskPayload.builder()
                .payloadType(RequestTaskPayloadType.PERMIT_REVOCATION_CESSATION_SUBMIT_PAYLOAD)
                .cessationContainer(PermitCessationContainer.builder()
                		.cessation(PermitCessation.builder()
                				.determinationOutcome(PermitCessationDeterminationOutcome.APPROVED)
                				.build())
                		.build())
                .build();
        final RequestTask requestTask = RequestTask.builder()
            .id(11L)
            .payload(requestTaskPayload)
            .request(request)
            .processTaskId("processTaskId")
            .build();

        final FileInfoDTO officialNotice = FileInfoDTO.builder()
        		.name("official notice.pdf")
        		.uuid(UUID.randomUUID().toString())
        		.build();
        
        final PermitCessationCompletedRequestActionPayload cessationCompletedRequestActionPayload =
            PermitCessationCompletedRequestActionPayload.builder()
                .payloadType(RequestActionPayloadType.PERMIT_REVOCATION_CESSATION_COMPLETED_PAYLOAD)
                .cessationOfficialNotice(officialNotice)
                .build();
        final PmrvUser pmrvUser = PmrvUser.builder().userId("user").build();
        
        when(permitRevocationOfficialNoticeService.generateRevocationCessationOfficialNotice(request.getId(), taskActionPayload.getDecisionNotification()))
        	.thenReturn(officialNotice);

        when(cessationCompletedRequestActionPayloadMapper.toCessationCompletedRequestActionPayload(requestTask,
            taskActionPayload,
            officialNotice,
            RequestActionPayloadType.PERMIT_REVOCATION_CESSATION_COMPLETED_PAYLOAD))
            .thenReturn(cessationCompletedRequestActionPayload);

        //invoke
        service.executeNotifyOperatorActions(requestTask, pmrvUser, taskActionPayload);

        assertThat(requestPayload.getPermitCessationContainer()).isEqualTo(requestTaskPayload.getCessationContainer());
        assertThat(requestPayload.getPermitCessationCompletedDate()).isNotNull();
        
        verify(cessationNotifyOperatorValidator, times(1))
            .validate(requestTask, pmrvUser, taskActionPayload);
        verify(accountStatusService, times(1)).handleRevocationCessationCompleted(request.getAccountId());
        verify(permitRevocationOfficialNoticeService, times(1))
            .generateRevocationCessationOfficialNotice(request.getId(), taskActionPayload.getDecisionNotification());
        verify(requestService, times(1)).addActionToRequest(request,
            cessationCompletedRequestActionPayload,
            RequestActionType.PERMIT_REVOCATION_CESSATION_COMPLETED,
            "regulatorAssignee");
        verify(permitRevocationOfficialNoticeService, times(1))
            .sendOfficialNotice(request, officialNotice, taskActionPayload.getDecisionNotification());
    }
}