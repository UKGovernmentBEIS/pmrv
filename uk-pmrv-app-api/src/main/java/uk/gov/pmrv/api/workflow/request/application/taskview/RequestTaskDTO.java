package uk.gov.pmrv.api.workflow.request.application.taskview;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestTaskDTO {
	
	private Long id;
	private RequestTaskType type;
	private RequestTaskPayload payload;
	private boolean assignable;
	private String assigneeUserId;
	private String assigneeFullName;
	private Long daysRemaining;
	private LocalDateTime startDate;
	private LocalDateTime endDate;
	
}
