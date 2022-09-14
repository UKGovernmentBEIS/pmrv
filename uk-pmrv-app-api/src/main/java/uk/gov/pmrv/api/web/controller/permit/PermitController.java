package uk.gov.pmrv.api.web.controller.permit;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.service.PermitAttachmentService;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;

@Validated
@RestController
@RequestMapping(path = "/v1.0/permits")
@RequiredArgsConstructor
@Api(tags = "Permits")
public class PermitController {

    private final PermitQueryService permitQueryService;
    private final PermitAttachmentService permitAttachmentService;

    @GetMapping("/{id}")
    @ApiOperation(value = "Retrieves permit by id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK),
        @ApiResponse(code = 404, message = NOT_FOUND),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#id")
    public ResponseEntity<PermitContainer> getPermitById(
        @ApiParam(value = "The permit id") @PathVariable("id") String id) {
        return new ResponseEntity<>(permitQueryService.getPermitContainerById(id), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}/attachments")
    @ApiOperation(value = "Generate the token to get the file that belongs to the provided permit id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = FileToken.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#permitId")
    public ResponseEntity<FileToken> generateGetPermitAttachmentToken(
        @PathVariable("id") @ApiParam(value = "The permit id") @NotNull String permitId,
        @RequestParam("uuid") @ApiParam(value = "The attachment uuid") @NotNull UUID attachmentUuid) {
        FileToken getFileAttachmentToken =
            permitAttachmentService.generateGetFileAttachmentToken(permitId, attachmentUuid);
        return new ResponseEntity<>(getFileAttachmentToken, HttpStatus.OK);
    }
}
