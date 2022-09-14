package uk.gov.pmrv.api.workflow.request.flow.permitrevocation.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.payment.service.PaymentAmountService;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocation;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitrevocation.domain.PermitRevocationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitRevocationApplicationSubmitInitializerTest {

    @InjectMocks
    private PermitRevocationApplicationSubmitInitializer initializer;

    @Mock
    private PaymentAmountService paymentAmountService;

    @Test
    void initializePayload() {
        final BigDecimal feeAmount = BigDecimal.valueOf(2450);
        
        final Request request = Request.builder().build();

        when(paymentAmountService.getAmount(request)).thenReturn(feeAmount);

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);
        
        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_REVOCATION_APPLICATION_SUBMIT_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PermitRevocationApplicationSubmitRequestTaskPayload.class);

        PermitRevocationApplicationSubmitRequestTaskPayload permitRevocationApplicationSubmitRequestTaskPayload =
            (PermitRevocationApplicationSubmitRequestTaskPayload)requestTaskPayload;
        assertThat(permitRevocationApplicationSubmitRequestTaskPayload.getFeeAmount()).isEqualTo(feeAmount);
    }

    @Test
    void initializePayload_afterPeerReview() {
        
        final BigDecimal feeAmount = BigDecimal.valueOf(2450);
        final PermitRevocation permitRevocation = PermitRevocation.builder().reason("the reason").build();
        final Request request = Request.builder()
            .payload(PermitRevocationRequestPayload.builder()
                .permitRevocation(permitRevocation)
                .sectionsCompleted(Map.of("section1", true))
                .build())
            .build();

        when(paymentAmountService.getAmount(request)).thenReturn(feeAmount);

        final RequestTaskPayload requestTaskPayload = initializer.initializePayload(request);

        assertThat(requestTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_REVOCATION_APPLICATION_SUBMIT_PAYLOAD);
        assertThat(requestTaskPayload).isInstanceOf(PermitRevocationApplicationSubmitRequestTaskPayload.class);

        PermitRevocationApplicationSubmitRequestTaskPayload permitRevocationApplicationSubmitRequestTaskPayload =
            (PermitRevocationApplicationSubmitRequestTaskPayload)requestTaskPayload;
        assertThat(permitRevocationApplicationSubmitRequestTaskPayload.getFeeAmount()).isEqualTo(feeAmount);
        assertThat(permitRevocationApplicationSubmitRequestTaskPayload.getPermitRevocation()).isEqualTo(permitRevocation);
        assertThat(permitRevocationApplicationSubmitRequestTaskPayload.getSectionsCompleted()).isEqualTo(Map.of("section1", true));
    }

    @Test
    void getRequestTaskTypes() {
        assertEquals(initializer.getRequestTaskTypes(), Set.of(RequestTaskType.PERMIT_REVOCATION_APPLICATION_SUBMIT));
    }
}
