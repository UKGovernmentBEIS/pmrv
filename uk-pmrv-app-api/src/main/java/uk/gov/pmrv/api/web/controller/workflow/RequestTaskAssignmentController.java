package uk.gov.pmrv.api.web.controller.workflow;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REQUEST_TASK_ASSIGNMENT_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REQUEST_TASK_CANDIDATE_ASSIGNEES_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REQUEST_TASK_TYPE_CANDIDATE_ASSIGNEES_BAD_REQUEST;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
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
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.dto.AssigneeUserInfoDTO;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.dto.RequestTaskAssignmentDTO;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.RequestTaskAssignmentQueryService;
import uk.gov.pmrv.api.workflow.request.core.assignment.taskassign.service.UserRequestTaskAssignmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

@RestController
@RequestMapping(path = "/v1.0/tasks-assignment")
@Api(tags = "Tasks Assignment")
@RequiredArgsConstructor
public class RequestTaskAssignmentController {

    private final UserRequestTaskAssignmentService userRequestTaskAssignmentService;
    private final RequestTaskAssignmentQueryService requestTaskAssignmentQueryService;

    /**
     * Assigns a task to a user.
     * @param requestTaskAssignmentDTO the {@link RequestTaskAssignmentDTO}
     */
    @PostMapping(path = "/assign")
    @ApiOperation(value = "Assigns a task to a user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = NO_CONTENT),
        @ApiResponse(code = 400, message = REQUEST_TASK_ASSIGNMENT_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#requestTaskAssignmentDTO.taskId")
    public ResponseEntity<Void> assignTask(
        @RequestBody @Valid @ApiParam(value = "The request task assignment body", required = true)
            RequestTaskAssignmentDTO requestTaskAssignmentDTO) {
        userRequestTaskAssignmentService.assignTask(requestTaskAssignmentDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Retrieves a list of users that can be assigned to the provided task id.
     * @param taskId the task id
     * @return {@link List} of {@link UserInfo}
     */
    @GetMapping(path = "/{taskId}/candidate-assignees")
    @ApiOperation(value = "Returns all users to whom can be assigned the provided task ", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = AssigneeUserInfoDTO.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = REQUEST_TASK_CANDIDATE_ASSIGNEES_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<List<AssigneeUserInfoDTO>> getCandidateAssigneesByTaskId(
            PmrvUser user,
            @ApiParam(value = "The task id") @PathVariable("taskId") Long taskId) {
        return new ResponseEntity<>(
                requestTaskAssignmentQueryService.getCandidateAssigneesByTaskId(taskId, user), HttpStatus.OK);
    }

    @GetMapping(path = "/{taskId}/candidate-assignees/{taskType}")
    @ApiOperation(value = "Returns all users to whom can be assigned the provided task type", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = AssigneeUserInfoDTO.class, responseContainer = "List"),
        @ApiResponse(code = 400, message = REQUEST_TASK_TYPE_CANDIDATE_ASSIGNEES_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#taskId")
    public ResponseEntity<List<AssigneeUserInfoDTO>> getCandidateAssigneesByTaskType(
        PmrvUser user,
        @ApiParam(value = "The current task id that user works on. Not related to the task type for which we search candidate assignees")
        @PathVariable("taskId") Long taskId,
        @ApiParam(value = "The task type for which you need to retrieve candidate assignees")
        @PathVariable("taskType") RequestTaskType taskType) {
        return new ResponseEntity<>(
            requestTaskAssignmentQueryService.getCandidateAssigneesByTaskType(taskType, user), HttpStatus.OK);
    }
}
