package uk.gov.pmrv.api.workflow.request.flow.permitnotification.domain;

import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Data
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class PermitNotificationFollowUpApplicationAmendsSubmitRequestTaskPayload
    extends PermitNotificationFollowUpApplicationReviewRequestTaskPayload {

    @Builder.Default
    private Map<String, Boolean> followUpSectionsCompleted = new HashMap<>();
}
