package uk.gov.pmrv.api.workflow.request.application.authorization;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestTaskAuthorityInfoDTO;
import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.ResourceAuthorityInfo;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;

@ExtendWith(MockitoExtension.class)
class RequestTaskAuthorityInfoQueryServiceTest {

    private static final String REQUEST_ID = "1";
    private static final Long REQUEST_TASK_ID = 1L;
    private static final String PROCESS_INSTANCE_ID = "process_instance_id";
    private static final String PROCESS_TASK_ID = "process_task_id";
    private static final Long ACCOUNT_ID = 1L;

    @InjectMocks
    private RequestTaskAuthorityInfoQueryService service;

    @Mock
    private RequestTaskService requestTaskService;

    @Test
    void getRequestTaskInfo() {
        Request request = createRequest();
        RequestTask requestTask = createRequestTask(request, PROCESS_TASK_ID, RequestTaskType.ACCOUNT_USERS_SETUP.name(), "assignee");

        when(requestTaskService.findTaskById(REQUEST_TASK_ID)).thenReturn(requestTask);

        RequestTaskAuthorityInfoDTO requestTaskInfoDTO = service.getRequestTaskInfo(REQUEST_TASK_ID);

        RequestTaskAuthorityInfoDTO expectedRequestTaskInfoDTO = RequestTaskAuthorityInfoDTO.builder()
                .type(requestTask.getType().name())
                .authorityInfo(ResourceAuthorityInfo.builder().accountId(ACCOUNT_ID).competentAuthority(ENGLAND).build())
                .assignee(requestTask.getAssignee())
                .requestType(RequestType.INSTALLATION_ACCOUNT_OPENING.name())
                .build();
        assertEquals(expectedRequestTaskInfoDTO, requestTaskInfoDTO);
    }

    @Test
    void getRequestTaskInfo_does_not_exist() {
        when(requestTaskService.findTaskById(REQUEST_TASK_ID)).thenThrow(new BusinessException(ErrorCode.RESOURCE_NOT_FOUND));
        BusinessException businessException = assertThrows(BusinessException.class,
                () -> service.getRequestTaskInfo(REQUEST_TASK_ID));

        assertEquals(ErrorCode.RESOURCE_NOT_FOUND, businessException.getErrorCode());
    }

    private Request createRequest() {
        Request request = new Request();
        request.setId(REQUEST_ID);
        request.setProcessInstanceId(PROCESS_INSTANCE_ID);
        request.setType(RequestType.INSTALLATION_ACCOUNT_OPENING);
        request.setAccountId(ACCOUNT_ID);
        request.setCompetentAuthority(ENGLAND);
        return request;
    }

    private RequestTask createRequestTask(Request request, String processTaskId, String taskDefinitionKey, String assignee) {
        return RequestTask.builder()
            .request(request)
            .processTaskId(processTaskId)
            .type(RequestTaskType.valueOf(taskDefinitionKey))
            .assignee(assignee)
            .build();
    }
}
