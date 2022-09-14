package uk.gov.pmrv.api.web.controller.workflow;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.io.IOException;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;

import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.files.common.domain.dto.FileUuidDTO;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.util.FileDtoMapper;
import uk.gov.pmrv.api.workflow.request.application.attachment.task.RequestTaskAttachmentActionProcessDTO;
import uk.gov.pmrv.api.workflow.request.application.attachment.task.RequestTaskAttachmentService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestTaskAttachmentUploadService;

@RestController
@RequestMapping(path = "/v1.0/task-attachments")
@Api(tags = "Request task attachments handling")
@RequiredArgsConstructor
public class RequestTaskAttachmentController {
    
    private final RequestTaskAttachmentUploadService requestTaskAttachmentUploadService;
    private final RequestTaskAttachmentService requestTaskAttachmentService;
    private final FileDtoMapper fileDtoMapper = Mappers.getMapper(FileDtoMapper.class);

    @PostMapping(path = "/upload", consumes = {"multipart/form-data"})
    @ApiOperation(value = "Upload a request task attachment", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = FileUuidDTO.class),
        @ApiResponse(code = 400, message = SwaggerApiInfo.REQUEST_TASK_UPLOAD_ATTACHMENT_ACTION_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#requestTaskAttachmentActionProcessDTO.requestTaskId")
    public ResponseEntity<FileUuidDTO> uploadRequestTaskAttachment(
            PmrvUser authUser,
            @RequestPart("requestTaskActionDetails") @Valid @ApiParam(value = "The request task attachment properties", required = true) 
                RequestTaskAttachmentActionProcessDTO requestTaskAttachmentActionProcessDTO,
            @RequestPart("attachment") @Valid @NotBlank @ApiParam(value = "The request task source file attachment", required = true) 
                MultipartFile file) throws IOException {
        FileDTO attachment = fileDtoMapper.toFileDTO(file);
        RequestTaskActionType requestTaskActionType = requestTaskAttachmentActionProcessDTO.getRequestTaskActionType();

        FileUuidDTO fileUuidDTO = requestTaskAttachmentUploadService
            .uploadAttachment(requestTaskAttachmentActionProcessDTO.getRequestTaskId(), requestTaskActionType, authUser, attachment);
                
        return new ResponseEntity<>(fileUuidDTO, HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @ApiOperation(value = "Generate the token to get the file with the provided uuid that belongs to the provided task", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = FileToken.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#requestTaskId")
    public ResponseEntity<FileToken> generateRequestTaskGetFileAttachmentToken(
        @PathVariable("id") @ApiParam(value = "The request task id") Long requestTaskId,
        @RequestParam("attachmentUuid") @ApiParam(value = "The attachment uuid") @NotNull UUID attachmentUuid) {
        FileToken getFileAttachmentToken =
            requestTaskAttachmentService.generateGetFileAttachmentToken(requestTaskId, attachmentUuid);
        return new ResponseEntity<>(getFileAttachmentToken, HttpStatus.OK);
    }
}
