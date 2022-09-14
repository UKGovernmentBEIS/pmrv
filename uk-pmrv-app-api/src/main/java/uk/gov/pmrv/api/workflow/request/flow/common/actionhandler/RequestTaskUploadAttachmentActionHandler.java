package uk.gov.pmrv.api.workflow.request.flow.common.actionhandler;

import lombok.extern.log4j.Log4j2;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

@Log4j2
public abstract class RequestTaskUploadAttachmentActionHandler {

    @Retryable(value = OptimisticLockingFailureException.class,
            maxAttemptsExpression = "${attachment-upload.retry.max-attempts}",
            backoff = @Backoff(delayExpression = "${attachment-upload.retry.min-delay}", maxDelayExpression="${attachment-upload.retry.max-delay}"))
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public abstract void uploadAttachment(Long requestTaskId, String attachmentUuid, String filename);

    public abstract RequestTaskActionType getType();

    @Recover
    public void recover(OptimisticLockingFailureException e, Long requestTaskId, String attachmentUuid, String filename) {
        log.error(String.format("Concurrent error during upload file: %s. max retries reached", filename));
        throw new BusinessException(ErrorCode.UPLOAD_FILE_FAILED_ERROR);
    }
}
