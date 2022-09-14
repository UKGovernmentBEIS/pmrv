package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationContainer;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationViolation;

import javax.validation.Valid;

@Validated
@Service
@RequiredArgsConstructor
public class PermitNotificationSubmitValidatorService {

    private final PermitNotificationAttachmentsValidator permitNotificationAttachmentsValidator;

    public void validatePermitNotification(@Valid PermitNotificationContainer permitNotificationContainer) {
        if (!permitNotificationAttachmentsValidator.attachmentsExist(permitNotificationContainer.getPermitNotification().getAttachmentIds())) {
            throw new BusinessException(ErrorCode.INVALID_PERMIT_NOTIFICATION,
                    PermitNotificationViolation.ATTACHMENT_NOT_FOUND.getMessage());
        }

        if (!permitNotificationAttachmentsValidator.sectionAttachmentsReferencedInPermitNotification(
                permitNotificationContainer.getPermitNotification().getAttachmentIds(),
                permitNotificationContainer.getPermitNotificationAttachments().keySet())) {
            throw new BusinessException(ErrorCode.INVALID_PERMIT_NOTIFICATION,
                    PermitNotificationViolation.ATTACHMENT_NOT_REFERENCED.getMessage());
        }
    }
}
