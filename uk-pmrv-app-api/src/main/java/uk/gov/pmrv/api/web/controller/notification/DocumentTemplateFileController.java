package uk.gov.pmrv.api.web.controller.notification;

import java.util.UUID;

import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
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
import uk.gov.pmrv.api.notification.template.service.DocumentTemplateFileService;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;

@RestController
@Validated
@RequestMapping(path = "/v1.0/document-template-files")
@RequiredArgsConstructor
@Api(tags = "Document Template Files")
public class DocumentTemplateFileController {

    private final DocumentTemplateFileService documentTemplateFileService;

    @GetMapping("/{id}")
    @ApiOperation(value = "Generates the token to get the file with the provided uuid that belongs to the provided document template", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = FileToken.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#documentTemplateId")
    public ResponseEntity<FileToken> generateGetDocumentTemplateFileToken(
        @ApiParam(value = "The document template id") @PathVariable("id") Long documentTemplateId,
        @RequestParam("fileUuid") @ApiParam(value = "The file uuid") @NotNull UUID fileUuid) {
        FileToken token = documentTemplateFileService.generateGetFileDocumentTemplateToken(documentTemplateId, fileUuid);
        return new ResponseEntity<>(token, HttpStatus.OK);
    }
}
