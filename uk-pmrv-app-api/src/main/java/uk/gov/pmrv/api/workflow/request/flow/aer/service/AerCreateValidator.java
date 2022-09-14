package uk.gov.pmrv.api.workflow.request.flow.aer.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestCreateValidator;

import java.time.Year;

@Service
@RequiredArgsConstructor
public class AerCreateValidator implements RequestCreateValidator {

    private final AerCreateValidatorService aerCreateValidatorService;

    @Override
    public RequestCreateValidationResult validateAction(Long accountId) {
       return aerCreateValidatorService.validate(accountId, Year.now());
    }

    @Override
    public RequestCreateActionType getType() {
        return RequestCreateActionType.AER;
    }
}
