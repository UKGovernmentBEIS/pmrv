package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskActionPayload;

@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class PermitNotificationFollowUpSubmitApplicationAmendRequestTaskActionPayload extends RequestTaskActionPayload {

    /**
     * When the operator re-submits the amended follow-up, followUpSectionsCompleted map
     * should not include statuses of the amend tasks.
     * This has to be done in order to have the correct statuses of the amend tasks in the operator task list
     * in case that the regulator asks for an amend in the same section for a second time.
     */
    @Builder.Default
    private Map<String, Boolean> followUpSectionsCompleted = new HashMap<>();
}
