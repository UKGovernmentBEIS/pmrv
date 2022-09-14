package uk.gov.pmrv.api.permit.validation;

import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;

@Component
@RequiredArgsConstructor
public class PermitAttachmentsExistValidator implements PermitContextValidator, PermitGrantedContextValidator {
    
    private final PermitReferenceService permitReferenceService;

    @Override
    public PermitValidationResult validate(@Valid PermitContainer permitContainer) {
        Optional<PermitViolation> optViolation = permitReferenceService
                .validateFilesExist(permitContainer.getPermit().getPermitSectionAttachmentIds(),
                        permitContainer.getPermitAttachments().keySet())
                .map(p -> {
                    final Object[] data = p.getRight() != null ? p.getRight().toArray() : new Object[] {};
                    return new PermitViolation(p.getLeft(), data);
                });
        
        return optViolation.isEmpty() ? PermitValidationResult.builder().valid(true).build()
                : PermitValidationResult.builder().valid(false).permitViolations(List.of(optViolation.get())).build();
    }

}
