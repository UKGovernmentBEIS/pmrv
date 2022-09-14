package uk.gov.pmrv.api.workflow.request.flow.aer.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.account.domain.dto.AccountDTO;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.StartProcessRequestService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestMetadataType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerRequestMetadata;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.constants.BpmnProcessConstants;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

import java.time.Year;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AerCreationService {

    private final StartProcessRequestService startProcessRequestService;
    private final AerCreateValidatorService aerCreateValidatorService;
    private final AerDueDateService aerDueDateService;

    @Transactional
    public Request createRequestAer(AccountDTO account) {
        Year aerYear = Year.now().minusYears(1);

        // Validate if AER is allowed
        RequestCreateValidationResult validationResult = aerCreateValidatorService.validate(account.getId(), aerYear);
        if(!validationResult.isValid()) {
            throw new BusinessException(ErrorCode.AER_CREATION_NOT_ALLOWED, validationResult);
        }

        // Create and start workflow
        Map<String, Object> processVars = new HashMap<>();
        processVars.put(BpmnProcessConstants.AER_EXPIRATION_DATE, aerDueDateService.generateDueDate());
        return createRequestAer(account, aerYear, processVars);
    }

    private Request createRequestAer(AccountDTO account, Year aerYear, Map<String, Object> processVars) {
        RequestParams params = RequestParams.builder()
                .type(RequestType.AER)
                .ca(account.getCompetentAuthority())
                .accountId(account.getId())
                .requestPayload(AerRequestPayload.builder()
                        .payloadType(RequestPayloadType.AER_REQUEST_PAYLOAD)
                        .build())
                .requestMetadata(AerRequestMetadata.builder()
                        .type(RequestMetadataType.AER)
                        .year(aerYear).build())
                .processVars(processVars)
                .build();

        return startProcessRequestService.startProcess(params);
    }
}
