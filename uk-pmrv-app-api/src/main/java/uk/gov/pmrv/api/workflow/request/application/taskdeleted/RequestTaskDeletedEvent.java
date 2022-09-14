package uk.gov.pmrv.api.workflow.request.application.taskdeleted;

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
public class RequestTaskDeletedEvent {
    
    private Long requestTaskId;

}
