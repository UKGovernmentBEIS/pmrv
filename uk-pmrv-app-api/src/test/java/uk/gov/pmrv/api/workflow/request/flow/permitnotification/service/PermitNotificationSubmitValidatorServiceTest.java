package uk.gov.pmrv.api.workflow.request.flow.permitnotification.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.NonSignificantChange;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.NonSignificantChangeRelatedChangeType;
import uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain.PermitNotificationContainer;

@ExtendWith(MockitoExtension.class)
class PermitNotificationSubmitValidatorServiceTest {

    @InjectMocks
    private PermitNotificationSubmitValidatorService service;

    @Mock
    private PermitNotificationAttachmentsValidator permitNotificationAttachmentsValidator;

    @Test
    void validatePermitNotification() {
        UUID attachment1 = UUID.randomUUID();
        UUID attachment2 = UUID.randomUUID();

        PermitNotificationContainer permitNotificationContainer = PermitNotificationContainer.builder()
                .permitNotification(NonSignificantChange.builder()
                    .relatedChanges(Arrays.asList(
                            NonSignificantChangeRelatedChangeType.MONITORING_METHODOLOGY_PLAN,
                            NonSignificantChangeRelatedChangeType.MONITORING_PLAN
                        )
                    )
                    .documents(Set.of(attachment1))
                    .build()
                )
                .permitNotificationAttachments(Map.of(attachment1, "name1", attachment2, "name2"))
                .build();

        when(permitNotificationAttachmentsValidator.attachmentsExist(Set.of(attachment1))).thenReturn(true);
        when(permitNotificationAttachmentsValidator.sectionAttachmentsReferencedInPermitNotification(Set.of(attachment1), Set.of(attachment1, attachment2))).thenReturn(true);

        // Invoke
        service.validatePermitNotification(permitNotificationContainer);

        // Verify
        verify(permitNotificationAttachmentsValidator, times(1))
                .attachmentsExist(Set.of(attachment1));
        verify(permitNotificationAttachmentsValidator, times(1))
                .sectionAttachmentsReferencedInPermitNotification(Set.of(attachment1), Set.of(attachment1, attachment2));
    }

    @Test
    void validatePermitNotification_attachment_not_found_exception() {
        UUID attachment1 = UUID.randomUUID();
        UUID attachment2 = UUID.randomUUID();

        PermitNotificationContainer permitNotificationContainer = PermitNotificationContainer.builder()
                .permitNotification(
                    NonSignificantChange.builder()
                        .relatedChanges(Arrays.asList(
                                NonSignificantChangeRelatedChangeType.MONITORING_METHODOLOGY_PLAN,
                                NonSignificantChangeRelatedChangeType.MONITORING_PLAN
                            )
                        )
                        .documents(Set.of(attachment1))
                        .build()
                )
                .permitNotificationAttachments(Map.of(attachment1, "name1", attachment2, "name2"))
                .build();

        when(permitNotificationAttachmentsValidator.attachmentsExist(Set.of(attachment1))).thenReturn(false);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.validatePermitNotification(permitNotificationContainer));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_PERMIT_NOTIFICATION);
        assertThat(ex.getData()).isEqualTo(new Object[] {"Attachment not found"});

        verify(permitNotificationAttachmentsValidator, times(1))
                .attachmentsExist(Set.of(attachment1));
        verifyNoMoreInteractions(permitNotificationAttachmentsValidator);
    }

    @Test
    void validatePermitNotification_attachment_not_referenced_exception() {
        UUID attachment1 = UUID.randomUUID();
        UUID attachment2 = UUID.randomUUID();

        PermitNotificationContainer permitNotificationContainer = PermitNotificationContainer.builder()
                .permitNotification(NonSignificantChange.builder()
                    .relatedChanges(Arrays.asList(
                            NonSignificantChangeRelatedChangeType.MONITORING_METHODOLOGY_PLAN,
                            NonSignificantChangeRelatedChangeType.MONITORING_PLAN
                        )
                    )
                    .documents(Set.of(attachment1))
                    .build()
                )
                .permitNotificationAttachments(Map.of(attachment1, "name1", attachment2, "name2"))
                .build();

        when(permitNotificationAttachmentsValidator.attachmentsExist(Set.of(attachment1))).thenReturn(true);
        when(permitNotificationAttachmentsValidator.sectionAttachmentsReferencedInPermitNotification(Set.of(attachment1), Set.of(attachment1, attachment2))).thenReturn(false);

        // Invoke
        BusinessException ex = assertThrows(BusinessException.class,
                () -> service.validatePermitNotification(permitNotificationContainer));

        // Verify
        assertThat(ex.getErrorCode()).isEqualTo(ErrorCode.INVALID_PERMIT_NOTIFICATION);
        assertThat(ex.getData()).isEqualTo(new Object[] {"Attachment is not referenced in permit notification"});

        verify(permitNotificationAttachmentsValidator, times(1))
                .attachmentsExist(Set.of(attachment1));
        verify(permitNotificationAttachmentsValidator, times(1))
                .sectionAttachmentsReferencedInPermitNotification(Set.of(attachment1), Set.of(attachment1, attachment2));
    }
}
