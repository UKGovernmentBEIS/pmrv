package uk.gov.pmrv.api.web.controller.workflow;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.workflow.request.application.requestaction.RequestActionQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionDTO;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.RequestActionInfoDTO;

@Validated
@RestController
@RequestMapping(path = "/v1.0/request-actions")
@Api(tags = "Request actions")
@RequiredArgsConstructor
public class RequestActionController {

    private final RequestActionQueryService requestActionQueryService;
    
    @GetMapping(path = "/{id}")
    @ApiOperation(value = "Get request action by id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = RequestActionDTO.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#requestActionId")
    public ResponseEntity<RequestActionDTO> getRequestActionById(PmrvUser pmrvUser,
            @PathVariable("id") @ApiParam(value = "The request action id") Long requestActionId){
        final RequestActionDTO requestAction = requestActionQueryService.getRequestActionById(requestActionId, pmrvUser);
        return new ResponseEntity<>(requestAction, HttpStatus.OK);
    }
    
    @GetMapping(params = {"requestId"})
    @ApiOperation(value = "Retrieves the actions associated with the request", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = OK, response = RequestActionInfoDTO.class, responseContainer = "List"),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#requestId")
    public ResponseEntity<List<RequestActionInfoDTO>> getRequestActionsByRequestId(PmrvUser pmrvUser,
            @RequestParam("requestId") @ApiParam(value = "The request id") String requestId) {
        return new ResponseEntity<>(requestActionQueryService.getRequestActionsByRequestId(requestId, pmrvUser), HttpStatus.OK);
    }
}
