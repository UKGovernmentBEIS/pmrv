package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service;

import javax.validation.Valid;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderContainer;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderViolation;

@Validated
@Service
@RequiredArgsConstructor
public class PermitSurrenderSubmitValidatorService {
    
    private final PermitSurrenderAttachmentsValidator permitSurrenderAttachmentsValidator;
    
    public void validatePermitSurrender(@Valid PermitSurrenderContainer permitSurrenderContainer) {
        if (!permitSurrenderAttachmentsValidator.attachmentsExist(permitSurrenderContainer.getPermitSurrender().getAttachmentIds())) {
            throw new BusinessException(ErrorCode.INVALID_PERMIT_SURRENDER,
                    PermitSurrenderViolation.ATTACHMENT_NOT_FOUND.getMessage());
        }
        
        if (!permitSurrenderAttachmentsValidator.sectionAttachmentsReferencedInPermitSurrender(
                permitSurrenderContainer.getPermitSurrender().getAttachmentIds(),
                permitSurrenderContainer.getPermitSurrenderAttachments().keySet())) {
            throw new BusinessException(ErrorCode.INVALID_PERMIT_SURRENDER,
                    PermitSurrenderViolation.ATTACHMENT_NOT_REFERENCED.getMessage());
        }
    }
}
