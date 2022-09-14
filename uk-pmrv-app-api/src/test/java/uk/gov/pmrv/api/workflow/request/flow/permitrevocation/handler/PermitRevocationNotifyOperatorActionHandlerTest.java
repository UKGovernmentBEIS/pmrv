package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.utils.DateUtils;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.NotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationOutcome;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.validation.PermitRevocationValidator;

@ExtendWith(MockitoExtension.class)
class PermitRevocationNotifyOperatorActionHandlerTest {

    @InjectMocks
    private PermitRevocationNotifyOperatorActionHandler handler;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private PermitRevocationValidator validator;

    @Mock
    private WorkflowService workflowService;

    @Test
    void process_with_fee_charge() {
        final DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("regulator")
            .build();
        
        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload =
            NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION_PAYLOAD)
                .decisionNotification(decisionNotification)
                .build();

        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final String processTaskId = "processTaskId";
        final PermitRevocation permitRevocation = PermitRevocation.builder()
            .reason("reason")
            .annualEmissionsReportRequired(true)
            .annualEmissionsReportDate(LocalDate.of(2022, 2, 4))
            .effectiveDate(LocalDate.of(2025, 1, 2))
            .feeCharged(true)
            .feeDate(LocalDate.of(2023, 2, 3))
            .build();
        final PermitRevocationApplicationSubmitRequestTaskPayload taskPayload =
            PermitRevocationApplicationSubmitRequestTaskPayload.builder()
                .permitRevocation(permitRevocation)
                .build();
        final BigDecimal amount = BigDecimal.valueOf(2530.2);
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .payload(taskPayload)
            .request(Request.builder().payload(PermitRevocationRequestPayload.builder().paymentAmount(amount).build()).build())
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        //invoke
        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION,
            pmrvUser,
            taskActionPayload);
            
        assertThat(requestTask.getRequest().getSubmissionDate()).isNotNull();
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(validator, times(1)).validateNotifyUsers(requestTask, decisionNotification, pmrvUser);
        Map<String, Object> taskVariables = new HashMap<>();  // `HashMap` permits null values and null key.
        taskVariables.put(BpmnProcessConstants.REVOCATION_OUTCOME, PermitRevocationOutcome.NOTIFY_OPERATOR);
        taskVariables.put(BpmnProcessConstants.REVOCATION_EFFECTIVE_DATE, DateUtils.convertLocalDateToDate(permitRevocation.getEffectiveDate()));
        taskVariables.put(BpmnProcessConstants.REVOCATION_REMINDER_EFFECTIVE_DATE, DateUtils.convertLocalDateToDate(permitRevocation.getEffectiveDate().minus(28, ChronoUnit.DAYS)));
        taskVariables.put(BpmnProcessConstants.PAYMENT_EXPIRATION_DATE, DateUtils.convertLocalDateToDate(permitRevocation.getFeeDate()));
        verify(workflowService, times(1)).completeTask(processTaskId, taskVariables);
        verify(validator, times(1)).validateSubmitRequestTaskPayload(taskPayload);
        
        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(requestPayload.getPermitRevocation()).isEqualTo(permitRevocation);
        assertThat(requestPayload.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(requestPayload.getPaymentAmount()).isEqualTo(amount);
    }

    @Test
    void process_without_fee_charge() {
        final DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("operator"))
            .signatory("regulator")
            .build();

        final NotifyOperatorForDecisionRequestTaskActionPayload taskActionPayload =
            NotifyOperatorForDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION_PAYLOAD)
                .decisionNotification(decisionNotification)
                .build();

        final PmrvUser pmrvUser = PmrvUser.builder().build();
        final String processTaskId = "processTaskId";
        final PermitRevocation permitRevocation = PermitRevocation.builder()
            .reason("reason")
            .annualEmissionsReportRequired(true)
            .annualEmissionsReportDate(LocalDate.of(2022, 2, 4))
            .effectiveDate(LocalDate.of(2025, 1, 2))
            .feeCharged(false)
            .build();
        final PermitRevocationApplicationSubmitRequestTaskPayload taskPayload =
            PermitRevocationApplicationSubmitRequestTaskPayload.builder()
                .permitRevocation(permitRevocation)
                .build();
        final BigDecimal amount = BigDecimal.valueOf(2530.2);
        final RequestTask requestTask = RequestTask.builder()
            .id(1L)
            .processTaskId(processTaskId)
            .payload(taskPayload)
            .request(Request.builder().payload(PermitRevocationRequestPayload.builder().paymentAmount(amount).build()).build())
            .build();

        when(requestTaskService.findTaskById(1L)).thenReturn(requestTask);

        //invoke
        handler.process(requestTask.getId(),
            RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION,
            pmrvUser,
            taskActionPayload);

        assertThat(requestTask.getRequest().getSubmissionDate()).isNotNull();
        verify(requestTaskService, times(1)).findTaskById(requestTask.getId());
        verify(validator, times(1)).validateNotifyUsers(requestTask, decisionNotification, pmrvUser);
        Map<String, Object> taskVariables = new HashMap<>();  // `HashMap` permits null values and null key.
        taskVariables.put(BpmnProcessConstants.REVOCATION_OUTCOME, PermitRevocationOutcome.NOTIFY_OPERATOR);
        taskVariables.put(BpmnProcessConstants.REVOCATION_EFFECTIVE_DATE, DateUtils.convertLocalDateToDate(permitRevocation.getEffectiveDate()));
        taskVariables.put(BpmnProcessConstants.REVOCATION_REMINDER_EFFECTIVE_DATE, DateUtils.convertLocalDateToDate(permitRevocation.getEffectiveDate().minus(28, ChronoUnit.DAYS)));
        taskVariables.put(BpmnProcessConstants.PAYMENT_EXPIRATION_DATE, null);
        verify(workflowService, times(1)).completeTask(processTaskId, taskVariables);
        verify(validator, times(1)).validateSubmitRequestTaskPayload(taskPayload);

        final PermitRevocationRequestPayload requestPayload = (PermitRevocationRequestPayload) requestTask.getRequest().getPayload();
        assertThat(requestPayload.getPermitRevocation()).isEqualTo(permitRevocation);
        assertThat(requestPayload.getDecisionNotification()).isEqualTo(decisionNotification);
        assertThat(requestPayload.getPaymentAmount()).isEqualTo(BigDecimal.ZERO);
    }

    @Test
    void getTypes() {
        assertThat(handler.getTypes()).containsExactly(RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION);
    }
}