package uk.gov.pmrv.api.web.controller.notification;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

import java.io.IOException;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.mapstruct.factory.Mappers;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.files.common.domain.dto.FileDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.DocumentTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.service.DocumentTemplateQueryService;
import uk.gov.pmrv.api.notification.template.service.DocumentTemplateUpdateService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;
import uk.gov.pmrv.api.web.util.FileDtoMapper;

@RestController
@Validated
@RequestMapping(path = "/v1.0/document-templates")
@RequiredArgsConstructor
@Api(tags = "Document Templates")
public class DocumentTemplateController {

    private final DocumentTemplateQueryService documentTemplateQueryService;
    private final DocumentTemplateUpdateService documentTemplateUpdateService;
    private final FileDtoMapper fileDtoMapper = Mappers.getMapper(FileDtoMapper.class);

    @GetMapping
    @ApiOperation(value = "Retrieves the document templates associated with current user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = TemplateSearchResults.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<TemplateSearchResults> getCurrentUserDocumentTemplates(
        PmrvUser pmrvUser,
        @RequestParam(value = "term", required = false) @Size(min = 3, max=256) @ApiParam(value = "The term to search") String term,
        @RequestParam(value = "page") @NotNull @ApiParam(value = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") Long page,
        @RequestParam(value = "size") @NotNull @ApiParam(value = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")  Long pageSize
    ) {
        return new ResponseEntity<>(
            documentTemplateQueryService.getDocumentTemplatesByCaAndSearchCriteria(
                pmrvUser.getCompetentAuthority(),
                DocumentTemplateSearchCriteria.builder()
                    .term(term)
                    .page(page)
                    .pageSize(pageSize)
                    .build()
            ),
            HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Retrieves the document template with the provided id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = DocumentTemplateDTO.class),
        @ApiResponse(code = 400, message = SwaggerApiInfo.GET_DOCUMENT_TEMPLATE_BY_ID_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#id")
    public ResponseEntity<DocumentTemplateDTO> getDocumentTemplateById(
        @ApiParam(value = "The document template id") @PathVariable("id") Long id) {
        return new ResponseEntity<>(documentTemplateQueryService.getDocumentTemplateDTOById(id), HttpStatus.OK);
    }

    @PostMapping(path = "/{id}", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiOperation(value = "Updates the document template with the provided id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 400, message = SwaggerApiInfo.UPDATE_DOCUMENT_TEMPLATE_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#id")
    public ResponseEntity<Void> updateDocumentTemplate(
        PmrvUser authUser,
        @PathVariable("id") @ApiParam(value = "The document template id") Long id,
        @RequestPart("file") @ApiParam(value = "The document template source file", required = true) MultipartFile documentFile) throws IOException {
        FileDTO documentFileDTO = fileDtoMapper.toFileDTO(documentFile);
        documentTemplateUpdateService.updateDocumentTemplateFile(id, documentFileDTO, authUser.getUserId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
