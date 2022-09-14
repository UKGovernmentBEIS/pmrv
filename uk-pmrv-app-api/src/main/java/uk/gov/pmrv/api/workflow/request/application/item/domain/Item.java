package uk.gov.pmrv.api.workflow.request.application.item.domain;

import java.time.LocalDate;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Item {

    /** item fields **/
    private LocalDateTime creationDate;

    /** request fields **/
    private String requestId;

    private RequestType requestType;

    private Long accountId;

    /** request task fields **/
    private Long taskId;

    private RequestTaskType taskType;

    private String taskAssigneeId;

    private LocalDate taskDueDate;

    private LocalDate pauseDate;

    private boolean isNew;
}
