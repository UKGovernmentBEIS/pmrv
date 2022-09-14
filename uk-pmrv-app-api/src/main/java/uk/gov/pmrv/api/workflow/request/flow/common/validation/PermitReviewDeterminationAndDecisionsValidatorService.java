package uk.gov.pmrv.api.workflow.request.flow.common.validation;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.Determinateable;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitPayloadGroupDecidable;

@Validated
@Service
@RequiredArgsConstructor
public class PermitReviewDeterminationAndDecisionsValidatorService {

    private final List<PermitReviewDeterminationAndDecisionsValidator<?>> validators;
    
    public void validateDeterminationObject(@NotNull @Valid final Determinateable determination) {}

    public boolean isDeterminationAndDecisionsValid(@NotNull final Determinateable determination,
                           @NotNull final PermitPayloadGroupDecidable<?> payload,
                           @NotNull final RequestType requestType) {
        final PermitReviewDeterminationAndDecisionsValidator validator = validators.stream()
				.filter(v -> v.getType().equals(determination.getType())
						&& v.getRequestType() == requestType)
                .findFirst()
                .orElseThrow(() -> {
                    throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
                });

        return validator.isValid(payload);
    }
}
