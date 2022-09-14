package uk.gov.pmrv.api.web.controller.workflow;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskReleaseService;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REQUEST_TASK_ASSIGNMENT_BAD_REQUEST;

@RestController
@RequestMapping(path = "/v1.0/tasks-assignment/release")
@Api(tags = "Tasks Release")
@RequiredArgsConstructor
public class RequestTaskReleaseController {

    private final RequestTaskReleaseService requestTaskReleaseService;

    /**
     * Releases a task.
     * @param taskId the task id
     */
    @DeleteMapping(path = "/{taskId}")
    @ApiOperation(value = "Releases a task", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 204, message = NO_CONTENT),
            @ApiResponse(code = 400, message = REQUEST_TASK_ASSIGNMENT_BAD_REQUEST, response = ErrorResponse.class),
            @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<Void> releaseTask(
            @ApiParam(value = "The task id to be released") @PathVariable("taskId") Long taskId) {
        requestTaskReleaseService.releaseTaskById(taskId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
