package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitNotificationSaveApplicationRequestTaskActionPayload extends RequestTaskActionPayload {

    @NotNull
    private PermitNotification permitNotification;

    @Builder.Default
    private Map<String, Boolean> sectionsCompleted = new HashMap<>();
}
