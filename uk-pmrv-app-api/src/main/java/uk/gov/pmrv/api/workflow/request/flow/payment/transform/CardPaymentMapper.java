package uk.gov.pmrv.api.workflow.request.flow.payment.transform;

import org.mapstruct.Mapper;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentGetResult;
import uk.gov.pmrv.api.workflow.request.flow.payment.domain.CardPaymentProcessResponseDTO;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface CardPaymentMapper {

    CardPaymentProcessResponseDTO toCardPaymentProcessResponseDTO(PaymentGetResult paymentGetResult);
}
