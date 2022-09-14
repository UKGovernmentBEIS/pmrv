package uk.gov.pmrv.api.workflow.request.core.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.SYSTEM_MESSAGE_NOTIFICATION_PAYLOAD;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.workflow.request.flow.accountinstallationopening.domain.InstallationAccountOpeningApplicationRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationPayload;
import uk.gov.pmrv.api.workflow.request.flow.notificationsystemmessage.domain.SystemMessageNotificationRequestTaskPayload;

class RequestTaskValidationServiceTest {

    private RequestTaskValidationService requestTaskValidationService;

    private Validator validator;

    @BeforeEach
    public void setUp() {
        ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
        validator = factory.getValidator();
        requestTaskValidationService = new RequestTaskValidationService(validator);
    }

    @Test
    void validateRequestTaskPayload_is_valid() {
        SystemMessageNotificationRequestTaskPayload requestTaskPayload = SystemMessageNotificationRequestTaskPayload.builder()
            .payloadType(SYSTEM_MESSAGE_NOTIFICATION_PAYLOAD)
            .messagePayload(SystemMessageNotificationPayload.builder().subject("subject").text("text").build())
            .build();

        requestTaskValidationService.validateRequestTaskPayload(requestTaskPayload);
    }

    @Test
    void validateRequestTaskPayload_is_invalid() {
        InstallationAccountOpeningApplicationRequestTaskPayload requestTaskPayload = InstallationAccountOpeningApplicationRequestTaskPayload.builder()
            .payloadType(INSTALLATION_ACCOUNT_OPENING_APPLICATION_PAYLOAD)
            .build();

        assertThrows(ConstraintViolationException.class, () -> requestTaskValidationService.validateRequestTaskPayload(requestTaskPayload));
    }
}