package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.config.property.AppProperties;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.payment.client.service.GovukPayService;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentCreateInfo;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentCreateResult;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentGetInfo;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentGetResult;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentStateInfo;
import uk.gov.pmrv.api.workflow.payment.domain.enumeration.PaymentMethodType;
import uk.gov.pmrv.api.workflow.request.WorkflowService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.CardPaymentCreateResponseDTO;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.CardPaymentProcessResponseDTO;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.CardPaymentStateDTO;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentMakeRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.PaymentOutcome;

@ExtendWith(MockitoExtension.class)
class CardPaymentServiceTest {

    @InjectMocks
    private CardPaymentService cardPaymentService;

    @Mock
    private RequestTaskService requestTaskService;

    @Mock
    private GovukPayService govukPayService;

    @Mock
    private PaymentCompleteService paymentCompleteService;

    @Mock
    private WorkflowService workflowService;

    @Mock
    private AppProperties appProperties;

    @Test
    void processPayment_create_new() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        PmrvUser authUser = PmrvUser.builder().build();
        String paymentRefNum = "AEM-098-1";
        BigDecimal amount = BigDecimal.valueOf(2302.54);
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        Request request = Request.builder().type(requestType).competentAuthority(competentAuthority).build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .paymentMethodTypes(Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD))
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .build();
        String webUrl = "http://www.pmrv.uk";
        AppProperties.Web web = createAppWeb(webUrl);
        String cardPaymentDesc = "Pay for a permit";
        PaymentCreateInfo paymentCreateInfo = PaymentCreateInfo.builder()
            .competentAuthority(competentAuthority)
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .description(cardPaymentDesc)
            .returnUrl(webUrl + "/payment/" + requestTaskId + "/make/confirmation?method=CREDIT_OR_DEBIT_CARD")
            .build();
        String clientPaymentId = "clientPaymentId";
        String nextUrl = "nextUrl";
        PaymentCreateResult paymentCreateResult = PaymentCreateResult.builder()
            .paymentId(clientPaymentId)
            .nextUrl(nextUrl)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(appProperties.getWeb()).thenReturn(web);
        when(govukPayService.createPayment(paymentCreateInfo)).thenReturn(paymentCreateResult);

        //invoke
        CardPaymentCreateResponseDTO cardPaymentCreateResponseDTO =
            cardPaymentService.createCardPayment(requestTaskId, requestTaskActionType, authUser);

        //verify
        ArgumentCaptor<RequestTask> requestTaskCaptor = ArgumentCaptor.forClass(RequestTask.class);
        verify(requestTaskService, times(1)).saveRequestTask(requestTaskCaptor.capture());

        RequestTask updatedRequestTask = requestTaskCaptor.getValue();
        assertThat(updatedRequestTask.getPayload()).isInstanceOf(PaymentMakeRequestTaskPayload.class);
        PaymentMakeRequestTaskPayload taskPayloadSaved = (PaymentMakeRequestTaskPayload) updatedRequestTask.getPayload();
        assertEquals(clientPaymentId, taskPayloadSaved.getExternalPaymentId());

        assertNotNull(cardPaymentCreateResponseDTO);
        assertEquals(nextUrl, cardPaymentCreateResponseDTO.getNextUrl());
        assertNull(cardPaymentCreateResponseDTO.getPendingPaymentExist());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(govukPayService, times(1)).createPayment(paymentCreateInfo);
    }

    @Test
    void processPayment_pending_payment_exists() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        PmrvUser authUser = PmrvUser.builder().build();
        String paymentRefNum = "AEM-098-1";
        BigDecimal amount = BigDecimal.valueOf(2302.54);
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        String externalPaymentId = "externalPaymentId";
        Request request = Request.builder().type(requestType).competentAuthority(competentAuthority).build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .paymentMethodTypes(Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD))
            .externalPaymentId(externalPaymentId)
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        CardPaymentCreateResponseDTO cardPaymentCreateResponseDTO =
            cardPaymentService.createCardPayment(requestTaskId, requestTaskActionType, authUser);

        //verify
        assertNotNull(cardPaymentCreateResponseDTO);
        assertTrue(cardPaymentCreateResponseDTO.getPendingPaymentExist());
        assertNull(cardPaymentCreateResponseDTO.getNextUrl());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verifyNoInteractions(govukPayService);
        verifyNoMoreInteractions(requestTaskService);
    }

    @Test
    void processPayment_not_supported() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        PmrvUser authUser = PmrvUser.builder().build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .paymentMethodTypes(Set.of(PaymentMethodType.BANK_TRANSFER))
            .build();
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        //invoke
        BusinessException businessException = assertThrows(BusinessException.class,
            () -> cardPaymentService.createCardPayment(requestTaskId, requestTaskActionType, authUser));

        //verify
        assertEquals(ErrorCode.INVALID_PAYMENT_METHOD, businessException.getErrorCode());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verifyNoMoreInteractions(requestTaskService);
        verifyNoInteractions(govukPayService);
    }

    @Test
    void getCardPaymentState_finished_with_status_success() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        String userId = "userId";
        PmrvUser authUser = PmrvUser.builder().userId(userId).build();
        String paymentRefNum = "AEM-098-1";
        String externalPaymentId = "n4brhul26f2hn1lt992ejj10ht";
        BigDecimal amount = BigDecimal.valueOf(2302.54);
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        Request request = Request.builder().type(requestType).competentAuthority(competentAuthority).build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .paymentMethodTypes(Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD))
            .externalPaymentId(externalPaymentId)
            .build();
        String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .processTaskId(processTaskId)
            .build();
        PaymentGetInfo paymentGetInfo = PaymentGetInfo.builder()
            .paymentId(externalPaymentId)
            .competentAuthority(competentAuthority)
            .build();
        PaymentStateInfo paymentStateInfo = PaymentStateInfo.builder()
            .status("success")
            .finished(true)
            .build();
        PaymentGetResult paymentGetResult = PaymentGetResult.builder()
            .paymentId(externalPaymentId)
            .state(paymentStateInfo)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(govukPayService.getPayment(paymentGetInfo)).thenReturn(paymentGetResult);

        //invoke
        CardPaymentProcessResponseDTO cardPaymentStateResponse =
            cardPaymentService.processExistingCardPayment(requestTaskId, requestTaskActionType, authUser);

        assertEquals(externalPaymentId, cardPaymentStateResponse.getPaymentId());
        assertNull(cardPaymentStateResponse.getNextUrl());
        CardPaymentStateDTO cardPaymentStateDTO = cardPaymentStateResponse.getState();
        assertNotNull(cardPaymentStateDTO);
        assertEquals(paymentStateInfo.getStatus(), cardPaymentStateDTO.getStatus());
        assertEquals(paymentStateInfo.isFinished(), cardPaymentStateDTO.isFinished());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(govukPayService, times(1)).getPayment(paymentGetInfo);
        verify(paymentCompleteService, times(1)).complete(request, authUser);
        verify(workflowService, times(1))
            .completeTask(processTaskId, Map.of(BpmnProcessConstants.PAYMENT_OUTCOME, PaymentOutcome.SUCCEEDED));
        verify(requestTaskService, never()).saveRequestTask(any());
    }

    @Test
    void getCardPaymentState_no_payment_id() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        PmrvUser authUser = PmrvUser.builder().build();
        String paymentRefNum = "AEM-098-1";
        BigDecimal amount = BigDecimal.valueOf(2302.54);
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        Request request = Request.builder().type(requestType).competentAuthority(competentAuthority).build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .paymentMethodTypes(Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD))
            .build();
        String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .processTaskId(processTaskId)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);

        BusinessException businessException = assertThrows(BusinessException.class,
            () -> cardPaymentService.processExistingCardPayment(requestTaskId, requestTaskActionType, authUser));

        //verify
        assertEquals(ErrorCode.EXTERNAL_PAYMENT_ID_NOT_EXIST, businessException.getErrorCode());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verifyNoInteractions(govukPayService, paymentCompleteService, workflowService);
        verify(requestTaskService, never()).saveRequestTask(any());
    }

    @Test
    void getCardPaymentState_finished_with_status_not_success() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        String userId = "userId";
        PmrvUser authUser = PmrvUser.builder().userId(userId).build();
        String paymentRefNum = "AEM-098-1";
        String externalPaymentId = "n4brhul26f2hn1lt992ejj10ht";
        BigDecimal amount = BigDecimal.valueOf(2302.54);
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        Request request = Request.builder().type(requestType).competentAuthority(competentAuthority).build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .paymentMethodTypes(Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD))
            .externalPaymentId(externalPaymentId)
            .build();
        String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .processTaskId(processTaskId)
            .build();
        PaymentGetInfo paymentGetInfo = PaymentGetInfo.builder()
            .paymentId(externalPaymentId)
            .competentAuthority(competentAuthority)
            .build();
        PaymentStateInfo paymentStateInfo = PaymentStateInfo.builder()
            .status("fail")
            .finished(true)
            .code("P0020")
            .message("Payment expired")
            .build();
        PaymentGetResult paymentGetResult = PaymentGetResult.builder()
            .paymentId(externalPaymentId)
            .state(paymentStateInfo)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(govukPayService.getPayment(paymentGetInfo)).thenReturn(paymentGetResult);

        //invoke
        CardPaymentProcessResponseDTO cardPaymentStateResponse =
            cardPaymentService.processExistingCardPayment(requestTaskId, requestTaskActionType, authUser);


        //verify
        assertEquals(externalPaymentId, cardPaymentStateResponse.getPaymentId());
        assertNull(cardPaymentStateResponse.getNextUrl());
        CardPaymentStateDTO cardPaymentStateDTO = cardPaymentStateResponse.getState();
        assertNotNull(cardPaymentStateDTO);
        assertEquals(paymentStateInfo.getStatus(), cardPaymentStateDTO.getStatus());
        assertEquals(paymentStateInfo.isFinished(), cardPaymentStateDTO.isFinished());
        assertEquals(paymentStateInfo.getCode(), cardPaymentStateDTO.getCode());
        assertEquals(paymentStateInfo.getMessage(), cardPaymentStateDTO.getMessage());

        ArgumentCaptor<RequestTask> requestTaskCaptor = ArgumentCaptor.forClass(RequestTask.class);
        verify(requestTaskService, times(1)).saveRequestTask(requestTaskCaptor.capture());

        RequestTask updatedRequestTask = requestTaskCaptor.getValue();
        assertThat(updatedRequestTask.getPayload()).isInstanceOf(PaymentMakeRequestTaskPayload.class);
        PaymentMakeRequestTaskPayload taskPayloadSaved = (PaymentMakeRequestTaskPayload) updatedRequestTask.getPayload();
        assertNull(taskPayloadSaved.getExternalPaymentId());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(govukPayService, times(1)).getPayment(paymentGetInfo);
        verifyNoInteractions(paymentCompleteService, workflowService);
    }

    @Test
    void getCardPaymentState_not_finished() {
        Long requestTaskId =  1L;
        RequestTaskActionType requestTaskActionType = RequestTaskActionType.PAYMENT_PAY_BY_CARD;
        String userId = "userId";
        PmrvUser authUser = PmrvUser.builder().userId(userId).build();
        String paymentRefNum = "AEM-098-1";
        String externalPaymentId = "n4brhul26f2hn1lt992ejj10ht";
        BigDecimal amount = BigDecimal.valueOf(2302.54);
        RequestType requestType = RequestType.PERMIT_ISSUANCE;
        CompetentAuthority competentAuthority = CompetentAuthority.WALES;
        Request request = Request.builder().type(requestType).competentAuthority(competentAuthority).build();
        PaymentMakeRequestTaskPayload requestTaskPayload = PaymentMakeRequestTaskPayload.builder()
            .amount(amount)
            .paymentRefNum(paymentRefNum)
            .paymentMethodTypes(Set.of(PaymentMethodType.CREDIT_OR_DEBIT_CARD))
            .externalPaymentId(externalPaymentId)
            .build();
        String processTaskId = "processTaskId";
        RequestTask requestTask = RequestTask.builder()
            .id(requestTaskId)
            .payload(requestTaskPayload)
            .request(request)
            .processTaskId(processTaskId)
            .build();
        PaymentGetInfo paymentGetInfo = PaymentGetInfo.builder()
            .paymentId(externalPaymentId)
            .competentAuthority(competentAuthority)
            .build();
        PaymentStateInfo paymentStateInfo = PaymentStateInfo.builder()
            .status("created")
            .finished(false)
            .build();
        String nextUrl = "nextUrl";
        PaymentGetResult paymentGetResult = PaymentGetResult.builder()
            .paymentId(externalPaymentId)
            .state(paymentStateInfo)
            .nextUrl(nextUrl)
            .build();

        when(requestTaskService.findTaskById(requestTaskId)).thenReturn(requestTask);
        when(govukPayService.getPayment(paymentGetInfo)).thenReturn(paymentGetResult);

        //invoke
        CardPaymentProcessResponseDTO cardPaymentStateResponse =
            cardPaymentService.processExistingCardPayment(requestTaskId, requestTaskActionType, authUser);


        //verify
        assertEquals(externalPaymentId, cardPaymentStateResponse.getPaymentId());
        assertEquals(nextUrl, cardPaymentStateResponse.getNextUrl());
        CardPaymentStateDTO cardPaymentStateDTO = cardPaymentStateResponse.getState();
        assertNotNull(cardPaymentStateDTO);
        assertEquals(paymentStateInfo.getStatus(), cardPaymentStateDTO.getStatus());
        assertEquals(paymentStateInfo.isFinished(), cardPaymentStateDTO.isFinished());
        assertEquals(paymentStateInfo.getCode(), cardPaymentStateDTO.getCode());
        assertEquals(paymentStateInfo.getMessage(), cardPaymentStateDTO.getMessage());

        verify(requestTaskService, times(1)).findTaskById(requestTaskId);
        verify(govukPayService, times(1)).getPayment(paymentGetInfo);
        verifyNoInteractions(paymentCompleteService, workflowService);
        verify(requestTaskService, never()).saveRequestTask(any());
    }

    private AppProperties.Web createAppWeb(String url) {
        AppProperties.Web web = new AppProperties.Web();
        web.setUrl(url);
        return web;
    }
}