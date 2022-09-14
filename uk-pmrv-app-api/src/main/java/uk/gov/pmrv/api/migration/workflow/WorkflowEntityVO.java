package uk.gov.pmrv.api.migration.workflow;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkflowEntityVO {

    private String emitterId;
    private String workflowId;
    private LocalDateTime creationDate;
    private RequestType type;
    private RequestStatus status;
}
