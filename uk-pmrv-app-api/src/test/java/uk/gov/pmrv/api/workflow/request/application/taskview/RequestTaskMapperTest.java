package uk.gov.pmrv.api.workflow.request.application.taskview;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestStatus;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;

class RequestTaskMapperTest {
	
	private RequestTaskMapper mapper;
	
	@BeforeEach
	public void init() {
		mapper = Mappers.getMapper(RequestTaskMapper.class);
	}
	
	@Test
	void toTaskDTO_no_assignee_user_assignable() {
		final String requestId = "1";
	    Long requestTaskId = 2L;
	    Request request = createRequest(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING);
		RequestTaskType requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;
		RequestTask requestTask = createRequestTask(requestTaskId, null, requestTaskType, request);
		
		//invoke
		RequestTaskDTO result = mapper.toTaskDTO(requestTask, null);
		
		//assert
		assertThat(result.getAssigneeFullName()).isNull();
		assertThat(result.getAssigneeUserId()).isNull();
		assertThat(result.getType()).isEqualTo(requestTaskType);
		assertThat(result.getId()).isEqualTo(requestTaskId);
		assertThat(result.isAssignable()).isTrue();
    }
	
	@Test
	void toTaskDTO_no_assignee_user_not_assignable() {
		final String requestId = "1";
        Long requestTaskId = 2L;
        Request request = createRequest(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING);
		RequestTaskType requestTaskType = RequestTaskType.ACCOUNT_USERS_SETUP;
		RequestTask requestTask = createRequestTask(requestTaskId, null, requestTaskType, request);
		
		//invoke
		RequestTaskDTO result = mapper.toTaskDTO(requestTask, null);
		
		//assert
		assertThat(result.getAssigneeFullName()).isNull();
		assertThat(result.getAssigneeUserId()).isNull();
		assertThat(result.getType()).isEqualTo(requestTaskType);
		assertThat(result.getId()).isEqualTo(requestTaskId);
		assertThat(result.isAssignable()).isFalse();
	}
	
	@Test
	void toTaskDTO_with_assignee_user() {
		final String requestId = "1";
        Long requestTaskId = 2L;
        String task_assignee = "task_assignee";
        Request request = createRequest(requestId, RequestType.INSTALLATION_ACCOUNT_OPENING);
		RequestTaskType requestTaskType = RequestTaskType.INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW;
		RequestTask requestTask = createRequestTask(requestTaskId, task_assignee, requestTaskType, request);
		final String fn = "fn";
		final String ln = "ln";
		ApplicationUserDTO assigneeUser = OperatorUserDTO.builder()
							.firstName(fn)
							.lastName(ln)
							.build();
		
		//invoke
		RequestTaskDTO result = mapper.toTaskDTO(requestTask, assigneeUser);
		
		//assert
		assertThat(result.getAssigneeFullName()).isEqualTo(fn + " " + ln);
		assertThat(result.getAssigneeUserId()).isEqualTo(task_assignee);
		assertThat(result.getType()).isEqualTo(requestTaskType);
		assertThat(result.getId()).isEqualTo(requestTaskId);
		assertThat(result.isAssignable()).isTrue();
	}

	private Request createRequest(String requestId, RequestType requestType) {
	    return Request.builder()
            .id(requestId)
            .type(requestType)
            .competentAuthority(CompetentAuthority.ENGLAND)
            .status(RequestStatus.IN_PROGRESS)
            .accountId(1L)
            .build();
    }
	
    private RequestTask createRequestTask(Long requestTaskId, String assignee, RequestTaskType requestTaskType,
            Request request) {
		return RequestTask.builder()
	            .id(requestTaskId)
	            .request(request)
	            .type(requestTaskType)
	            .assignee(assignee)
	            .build();
    }
}
