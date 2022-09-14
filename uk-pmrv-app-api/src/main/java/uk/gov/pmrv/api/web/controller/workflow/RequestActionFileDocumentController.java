package uk.gov.pmrv.api.web.controller.workflow;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.workflow.request.application.filedocument.requestaction.RequestActionFileDocumentService;

@RestController
@RequestMapping(path = "/v1.0/request-action-file-documents")
@Api(tags = "Request action file documents handling")
@RequiredArgsConstructor
public class RequestActionFileDocumentController {

    private final RequestActionFileDocumentService requestActionFileDocumentService;

    @GetMapping(path = "/{id}")
    @ApiOperation(value = "Generate the token to get the file document with the provided uuid that belongs to the provided request action", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = FileToken.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#requestActionId")
    public ResponseEntity<FileToken> generateRequestActionGetFileDocumentToken(
        @PathVariable("id") @ApiParam(value = "The request action id") Long requestActionId,
        @RequestParam("fileDocumentUuid") @ApiParam(value = "The file document uuid") @NotNull UUID fileDocumentUuid) {
        FileToken getFileDocumentToken =
                requestActionFileDocumentService.generateGetFileDocumentToken(requestActionId, fileDocumentUuid);
        return new ResponseEntity<>(getFileDocumentToken, HttpStatus.OK);
    }
}
