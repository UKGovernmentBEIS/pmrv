package uk.gov.pmrv.api.workflow.request.flow.payment.domain;

public interface RequestPayloadPayable {

    RequestPaymentInfo getRequestPaymentInfo();

    void setRequestPaymentInfo(RequestPaymentInfo requestPaymentInfo);
}
