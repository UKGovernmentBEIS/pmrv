package uk.gov.pmrv.api.workflow.request.flow.common.service;

import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

public interface RequestCreateValidator {

    @Transactional
    RequestCreateValidationResult validateAction(Long accountId);

    RequestCreateActionType getType();
}
