package uk.gov.pmrv.api.web.controller.notification;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateSearchCriteria;
import uk.gov.pmrv.api.notification.template.domain.dto.NotificationTemplateUpdateDTO;
import uk.gov.pmrv.api.notification.template.domain.dto.TemplateSearchResults;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateQueryService;
import uk.gov.pmrv.api.notification.template.service.NotificationTemplateUpdateService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

@RestController
@Validated
@RequestMapping(path = "/v1.0/notification-templates")
@RequiredArgsConstructor
@Api(tags = "Notification Templates")
public class NotificationTemplateController {

    private final NotificationTemplateQueryService notificationTemplateQueryService;
    private final NotificationTemplateUpdateService notificationTemplateUpdateService;

    @GetMapping
    @ApiOperation(value = "Retrieves the notification templates associated with current user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = TemplateSearchResults.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<TemplateSearchResults> getCurrentUserNotificationTemplates(
        PmrvUser pmrvUser,
        @RequestParam(value = "role")  @NotNull @ApiParam(value = "The role type") RoleType roleType,
        @RequestParam(value = "term", required = false) @Size(min = 3, max=256) @ApiParam(value = "The term to search") String term,
        @RequestParam(value = "page") @NotNull @ApiParam(value = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") Long page,
        @RequestParam(value = "size") @NotNull @ApiParam(value = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")  Long pageSize
    ) {
        return new ResponseEntity<>(
            notificationTemplateQueryService.getNotificationTemplatesByCaAndSearchCriteria(
                pmrvUser.getCompetentAuthority(),
                NotificationTemplateSearchCriteria.builder()
                    .term(term)
                    .roleType(roleType)
                    .page(page)
                    .pageSize(pageSize)
                    .build()
            ),
            HttpStatus.OK);
    }

    @GetMapping("/{id}")
    @ApiOperation(value = "Retrieves the notification template with the provided id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = NotificationTemplateDTO.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#id")
    public ResponseEntity<NotificationTemplateDTO> getNotificationTemplateById(
        @ApiParam(value = "The notification template id") @PathVariable("id") Long id) {
        return new ResponseEntity<>(notificationTemplateQueryService.getManagedNotificationTemplateById(id), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    @ApiOperation(value = "Updates the notification template with the provided id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#id")
    public ResponseEntity<Void> updateNotificationTemplate(
        @PathVariable("id") @ApiParam(value = "The notification template id") Long id,
        @RequestBody @Valid @ApiParam(value = "The data to update the notification template", required = true)
            NotificationTemplateUpdateDTO templateUpdateDTO) {
        notificationTemplateUpdateService.updateNotificationTemplate(id, templateUpdateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
