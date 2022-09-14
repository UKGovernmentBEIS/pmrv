package uk.gov.pmrv.api.workflow.request.flow.payment.service;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPayloadPayable;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.RequestPaymentInfo;
import uk.gov.pmrv.api.workflow.request.flow.payment.transform.PaymentPayloadMapper;

@Service
@RequiredArgsConstructor
public class PaymentCompleteRequestActionService {

    private final RequestService requestService;
    private static final PaymentPayloadMapper PAYMENT_PAYLOAD_MAPPER = Mappers.getMapper(PaymentPayloadMapper.class);

    public void markAsPaid(String requestId) {
        addRequestActionSubmittedByPayer(
            requestId,
            RequestActionPayloadType.PAYMENT_MARKED_AS_PAID_PAYLOAD,
            RequestActionType.PAYMENT_MARKED_AS_PAID)
        ;
    }

    public void markAsReceived(String requestId) {
        Request request = requestService.findRequestById(requestId);
        RequestPayload requestPayload = request.getPayload();
        RequestPaymentInfo requestPaymentInfo = ((RequestPayloadPayable) requestPayload).getRequestPaymentInfo();

        requestService.addActionToRequest(
            request,
            PAYMENT_PAYLOAD_MAPPER.toPaymentProcessedRequestActionPayload(
                request.getId(),
                requestPaymentInfo,
                RequestActionPayloadType.PAYMENT_MARKED_AS_RECEIVED_PAYLOAD
            ),
            RequestActionType.PAYMENT_MARKED_AS_RECEIVED,
            requestPayload.getRegulatorAssignee()
        );
    }

    public void cancel(String requestId) {
        Request request = requestService.findRequestById(requestId);
        RequestPayload requestPayload = request.getPayload();
        RequestPaymentInfo requestPaymentInfo = ((RequestPayloadPayable) requestPayload).getRequestPaymentInfo();

        requestService.addActionToRequest(
            request,
            PAYMENT_PAYLOAD_MAPPER.toPaymentCancelledRequestActionPayload(requestPaymentInfo),
            RequestActionType.PAYMENT_CANCELLED,
            requestPayload.getRegulatorAssignee()
        );
    }

    public void complete(String requestId) {
        addRequestActionSubmittedByPayer(
            requestId,
            RequestActionPayloadType.PAYMENT_COMPLETED_PAYLOAD,
            RequestActionType.PAYMENT_COMPLETED
        );
    }

    private void addRequestActionSubmittedByPayer(String requestId, RequestActionPayloadType requestActionPayloadType,
                                                  RequestActionType requestActionType) {
        Request request = requestService.findRequestById(requestId);
        RequestPayloadPayable requestPayload = (RequestPayloadPayable) request.getPayload();
        RequestPaymentInfo requestPaymentInfo = requestPayload.getRequestPaymentInfo();


        requestService.addActionToRequest(
            request,
            PAYMENT_PAYLOAD_MAPPER.toPaymentProcessedRequestActionPayload(
                request.getId(),
                requestPaymentInfo,
                requestActionPayloadType
            ),
            requestActionType,
            requestPaymentInfo.getPaidById()
        );
    }
}
