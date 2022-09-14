package uk.gov.pmrv.api.web.controller.file;

import javax.validation.constraints.NotEmpty;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.files.documents.service.FileDocumentTokenService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

@RestController
@RequestMapping(path = "/v1.0/file-documents")
@RequiredArgsConstructor
@Api(tags = "File Documents")
public class FileDocumentController {

    private final FileDocumentTokenService fileDocumentTokenService;

    @GetMapping(path = "/{token}")
    @ApiOperation(value = "Get the file document resource for the provided file token")
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = Resource.class),
        @ApiResponse(code = 400, message = SwaggerApiInfo.BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Resource> getFileDocument(
        @PathVariable("token") @ApiParam(value = "The file document token", required = true) @NotEmpty String token) {
        FileDTO file = fileDocumentTokenService.getFileDTOByToken(token);
        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.builder("document").filename(file.getFileName()).build().toString())
            .contentType(MediaType.parseMediaType(file.getFileType()))
            .body(new ByteArrayResource(file.getFileContent()));
    }
}
