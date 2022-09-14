package uk.gov.pmrv.api.web.controller.file;

import java.io.IOException;

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
import uk.gov.pmrv.api.files.attachments.service.FileAttachmentTokenService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

@RestController
@RequestMapping(path = "/v1.0/file-attachments")
@RequiredArgsConstructor
@Api(tags = "File Attachments")
public class FileAttachmentController {
    
    private final FileAttachmentTokenService fileAttachmentTokenService;

    @GetMapping(path = "/{token}")
    @ApiOperation(value = "Get the file attachment resource for the provided file attachment token")
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = Resource.class),
        @ApiResponse(code = 400, message = SwaggerApiInfo.BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Resource> getFileAttachment(
            @PathVariable("token") @ApiParam(value = "The file attachment token", required = true) @NotEmpty String token) throws IOException {
        FileDTO file = fileAttachmentTokenService.getFileDTOByToken(token);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION,
                ContentDisposition.builder("attachment").filename(file.getFileName()).build().toString())
            .contentType(MediaType.parseMediaType(file.getFileType()))
            .body(new ByteArrayResource(file.getFileContent()));
    }
}
