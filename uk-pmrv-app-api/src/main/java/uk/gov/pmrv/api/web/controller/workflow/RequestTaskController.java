package uk.gov.pmrv.api.web.controller.workflow;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestTaskItemDTO;
import uk.gov.pmrv.api.workflow.request.application.taskview.RequestTaskViewService;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestTaskActionProcessDTO;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandlerMapper;

@RestController
@RequestMapping(path = "/v1.0/tasks")
@RequiredArgsConstructor
@Api(tags = "Tasks")
public class RequestTaskController {
    private final RequestTaskViewService requestTaskViewService;
    private final RequestTaskActionHandlerMapper requestTaskActionHandlerMapper;

    @GetMapping(path = "/{id}")
    @ApiOperation(value = "Get task item info by id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
    	@ApiResponse(code = 200, message = OK, response = RequestTaskItemDTO.class),
    	@ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
    	@ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<RequestTaskItemDTO> getTaskItemInfoById(PmrvUser pmrvUser,
    		@PathVariable("id") @ApiParam(value = "The task id") Long taskId){
    	final RequestTaskItemDTO taskItem = requestTaskViewService.getTaskItemInfo(taskId, pmrvUser);
    	return new ResponseEntity<>(taskItem, HttpStatus.OK);
    }

    @SuppressWarnings("unchecked")
    @PostMapping(path = "/actions")
    @ApiOperation(value = "Processes a request task action", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = OK),
            @ApiResponse(code = 400, message = SwaggerApiInfo.REQUEST_TASK_ACTION_BAD_REQUEST, response = ErrorResponse.class),
            @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#requestTaskActionProcessDTO.requestTaskId")
    public ResponseEntity<Void> processRequestTaskAction(PmrvUser pmrvUser,
            @RequestBody @Valid @ApiParam(value = "The request task action body", required = true) RequestTaskActionProcessDTO requestTaskActionProcessDTO) {
        requestTaskActionHandlerMapper
            .get(requestTaskActionProcessDTO.getRequestTaskActionType())
            .process(requestTaskActionProcessDTO.getRequestTaskId(),
                     requestTaskActionProcessDTO.getRequestTaskActionType(),
                     pmrvUser,
                     requestTaskActionProcessDTO.getRequestTaskActionPayload());
        
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
