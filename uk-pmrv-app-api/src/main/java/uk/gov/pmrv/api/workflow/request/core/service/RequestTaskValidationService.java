package uk.gov.pmrv.api.workflow.request.core.service;

import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;

@Service
@RequiredArgsConstructor
public class RequestTaskValidationService {

    private final Validator validator;

    public void validateRequestTaskPayload(RequestTaskPayload requestTaskPayload) {
        Set<ConstraintViolation<RequestTaskPayload>> violations = validator.validate(requestTaskPayload);
        if(!violations.isEmpty()) {
            throw new ConstraintViolationException(violations);
        }
    }
}
