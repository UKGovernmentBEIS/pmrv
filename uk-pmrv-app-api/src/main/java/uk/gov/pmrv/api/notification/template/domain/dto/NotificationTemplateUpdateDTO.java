package uk.gov.pmrv.api.notification.template.domain.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationTemplateUpdateDTO {

    @NotBlank(message = "{notificationTemplate.subject.notEmpty}")
    @Size(max = 255, message = "{notificationTemplate.subject.typeMismatch}")
    private String subject;

    @NotBlank(message = "{notificationTemplate.text.notEmpty}")
    @Size(max=10000, message = "{notificationTemplate.text.typeMismatch}")
    private String text;
}
