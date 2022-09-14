package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermitNotificationContainer {

    @Valid
    @NotNull
    private PermitNotification permitNotification;

    @Builder.Default
    private Map<UUID, String> permitNotificationAttachments = new HashMap<>();
}
