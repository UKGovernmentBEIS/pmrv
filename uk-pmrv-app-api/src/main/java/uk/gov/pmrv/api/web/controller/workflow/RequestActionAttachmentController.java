package uk.gov.pmrv.api.web.controller.workflow;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.UUID;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.workflow.request.application.attachment.requestaction.RequestActionAttachmentService;

@RestController
@RequestMapping(path = "/v1.0/request-action-attachments")
@Api(tags = "Request action attachments handling")
@RequiredArgsConstructor
public class RequestActionAttachmentController {

    private final RequestActionAttachmentService requestActionAttachmentService;

    @GetMapping(path = "/{id}")
    @ApiOperation(value = "Generate the token to get the file with the provided uuid that belongs to the provided request action", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = FileToken.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#requestActionId")
    public ResponseEntity<FileToken> generateRequestActionGetFileAttachmentToken(
        @PathVariable("id") @ApiParam(value = "The request action id") Long requestActionId,
        @RequestParam("attachmentUuid") @ApiParam(value = "The attachment uuid") @NotNull UUID attachmentUuid) {
        FileToken getFileAttachmentToken =
            requestActionAttachmentService.generateGetFileAttachmentToken(requestActionId, attachmentUuid);
        return new ResponseEntity<>(getFileAttachmentToken, HttpStatus.OK);
    }
}
