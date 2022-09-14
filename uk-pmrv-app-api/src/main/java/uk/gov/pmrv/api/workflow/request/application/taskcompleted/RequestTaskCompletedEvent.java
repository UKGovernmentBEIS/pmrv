package uk.gov.pmrv.api.workflow.request.application.taskcompleted;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class RequestTaskCompletedEvent {
    
    private Long requestTaskId;

}
