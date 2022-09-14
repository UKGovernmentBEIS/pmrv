package uk.gov.pmrv.api.web.controller.workflow;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.Map;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestCreateActionProcessDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestCreateActionProcessResponseDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestDetailsSearchResults;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestSearchCriteria;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCategory;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestCreateActionHandlerMapper;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestCreateValidationResult;

@Validated
@RestController
@RequestMapping(path = "/v1.0/requests")
@Api(tags = "Requests")
@RequiredArgsConstructor
public class RequestController {

    private final RequestCreateActionHandlerMapper requestCreateActionHandlerMapper;
    private final RequestQueryService requestQueryService;
    private final RequestService requestService;
    
    @PostMapping
    @SuppressWarnings("unchecked")
    @ApiOperation(value = "Processes a request create action", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = RequestCreateActionProcessResponseDTO.class),
            @ApiResponse(code = 400, message = SwaggerApiInfo.REQUEST_ACTION_BAD_REQUEST, response = ErrorResponse.class),
            @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId", resourceSubType = "#requestCreateActionProcess.requestCreateActionType")
    public ResponseEntity<RequestCreateActionProcessResponseDTO> processRequestCreateAction(PmrvUser pmrvUser,
            @RequestParam(required = false) Long accountId,
            @RequestBody @Valid @ApiParam(value = "The request create action body", required = true) RequestCreateActionProcessDTO requestCreateActionProcess) {
        String requestId = requestCreateActionHandlerMapper
            .get(requestCreateActionProcess.getRequestCreateActionType())
            .process(accountId, requestCreateActionProcess.getRequestCreateActionType(),
                requestCreateActionProcess.getRequestCreateActionPayload(), pmrvUser);
        return ResponseEntity.ok(new RequestCreateActionProcessResponseDTO(requestId));
    }

    @GetMapping(path = "/{id}")
    @ApiOperation(value = "Get request details by id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = RequestDetailsDTO.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#id")
    public ResponseEntity<RequestDetailsDTO> getRequestDetailsById(
            @PathVariable("id") @ApiParam(value = "The request id") String id){
        return new ResponseEntity<>(requestQueryService.findRequestDetailsById(id), HttpStatus.OK);
    }

    @PostMapping("/workflows") //workaround: post instead of get, in order to support posting collection of params (request types)
    @ApiOperation(value = "Get the workflows for the given search criteria", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = RequestDetailsSearchResults.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#criteria.accountId")
    public ResponseEntity<RequestDetailsSearchResults> getRequestDetailsByAccountId(
            @RequestBody @Valid @ApiParam(value = "The search criteria", required = true) RequestSearchCriteria criteria){
        return new ResponseEntity<>(requestQueryService.findRequestDetailsBySearchCriteria(criteria), HttpStatus.OK);
    }

    @GetMapping("/available-workflows/{accountId}")
    @ApiOperation(value = "Get workflows to start a task", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = Map.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Map<RequestCreateActionType, RequestCreateValidationResult>> getAvailableWorkflows(
        PmrvUser pmrvUser, @PathVariable("accountId") @ApiParam(value = "The account id", required = true) Long accountId,
        @RequestParam("category") @ApiParam(value = "The request category") @NotNull RequestCategory category) {

        return new ResponseEntity<>(requestService.getAvailableWorkflows(accountId, pmrvUser, category), HttpStatus.OK);
    }
}
