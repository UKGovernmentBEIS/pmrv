package uk.gov.pmrv.api.workflow.payment.client.service;

import java.math.BigDecimal;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.provider.PMRVRestApi;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.payment.client.domain.CreatePaymentRequest;
import uk.gov.pmrv.api.workflow.payment.client.domain.PaymentResponse;
import uk.gov.pmrv.api.workflow.payment.client.domain.enumeration.RestEndPointEnum;
import uk.gov.pmrv.api.workflow.payment.config.property.GovukPayProperties;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentCreateInfo;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentCreateResult;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentGetInfo;
import uk.gov.pmrv.api.workflow.payment.domain.dto.PaymentGetResult;
import uk.gov.pmrv.api.workflow.payment.transform.PaymentMapper;

@Log4j2
@Component
@RequiredArgsConstructor
public class GovukPayService {

    private final RestTemplate restTemplate;
    private final GovukPayProperties govukPayProperties;

    private static final PaymentMapper PAYMENT_MAPPER = Mappers.getMapper(PaymentMapper.class);

    public static final int POUND_TO_PENCE_CONVERTER_FACTOR = 100;

    public PaymentCreateResult createPayment(PaymentCreateInfo paymentCreateInfo) {
        PaymentResponse paymentResponse = performCreatePaymentApiCall(paymentCreateInfo);

        if(paymentResponse == null) {
            throw new BusinessException(ErrorCode.PAYMENT_PROCESSING_FAILED);
        }

        return PAYMENT_MAPPER.toPaymentCreateResult(paymentResponse);
    }

    public PaymentGetResult getPayment(PaymentGetInfo paymentGetInfo) {
        PaymentResponse paymentResponse = performGetPaymentApiCall(paymentGetInfo);

        if(paymentResponse == null) {
            throw new BusinessException(ErrorCode.PAYMENT_PROCESSING_FAILED);
        }

        return PAYMENT_MAPPER.toPaymentGetResult(paymentResponse);
    }

    private PaymentResponse performCreatePaymentApiCall(PaymentCreateInfo paymentCreateInfo) {
        PMRVRestApi pmrvRestApi = PMRVRestApi.builder()
            .baseUrl(govukPayProperties.getServiceUrl())
            .restEndPoint(RestEndPointEnum.GOV_UK_CREATE_PAYMENT)
            .headers(httpHeaders(paymentCreateInfo.getCompetentAuthority()))
            .body(CreatePaymentRequest.builder()
                .amount(paymentCreateInfo.getAmount().multiply(BigDecimal.valueOf(POUND_TO_PENCE_CONVERTER_FACTOR)).intValue())
                .reference(paymentCreateInfo.getPaymentRefNum())
                .description(paymentCreateInfo.getDescription())
                .returnUrl(paymentCreateInfo.getReturnUrl())
                .build())
            .restTemplate(restTemplate)
            .build();

        try{
            ResponseEntity<PaymentResponse> res = pmrvRestApi.performApiCall();
            return res.getBody();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, e.getMessage());
        }
    }

    private PaymentResponse performGetPaymentApiCall(PaymentGetInfo paymentGetInfo) {
        PMRVRestApi pmrvRestApi = PMRVRestApi.builder()
            .uri(UriComponentsBuilder
                .fromHttpUrl(govukPayProperties.getServiceUrl())
                .path(RestEndPointEnum.GOV_UK_GET_PAYMENT.getEndPoint())
                .buildAndExpand(paymentGetInfo.getPaymentId())
            )
            .restEndPoint(RestEndPointEnum.GOV_UK_GET_PAYMENT)
            .headers(httpHeaders(paymentGetInfo.getCompetentAuthority()))
            .restTemplate(restTemplate)
            .build();

        try{
            ResponseEntity<PaymentResponse> res = pmrvRestApi.performApiCall();
            return res.getBody();
        } catch (HttpClientErrorException e) {
            log.error(e.getMessage());
            throw new BusinessException(ErrorCode.INTERNAL_SERVER, e.getMessage());
        }
    }

    private HttpHeaders httpHeaders(CompetentAuthority competentAuthority) {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(govukPayProperties.getApiKeys().get(competentAuthority.name().toLowerCase()));
        return httpHeaders;
    }
}
