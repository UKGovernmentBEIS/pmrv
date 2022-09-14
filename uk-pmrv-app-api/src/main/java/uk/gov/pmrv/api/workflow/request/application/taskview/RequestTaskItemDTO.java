package uk.gov.pmrv.api.workflow.request.application.taskview;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestTaskItemDTO {

	private RequestTaskDTO requestTask;

	@Builder.Default
	private List<RequestTaskActionType> allowedRequestTaskActions = new ArrayList<>();

	private boolean userAssignCapable;

    private RequestInfoDTO requestInfo;
}
