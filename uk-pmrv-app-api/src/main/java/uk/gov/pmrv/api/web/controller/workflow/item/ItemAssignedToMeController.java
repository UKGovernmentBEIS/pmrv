package uk.gov.pmrv.api.web.controller.workflow.item;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
import uk.gov.pmrv.api.web.security.AuthorizedRole;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemAssignedToMeService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.VERIFIER;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.VALIDATION_PARAMETER_ERROR_BAD_REQUEST;

@RestController
@RequestMapping(path = "/v1.0/items/assigned-to-me")
@Api(tags = "Items Assigned To Me")
@Validated
@RequiredArgsConstructor
public class ItemAssignedToMeController {

    private final List<ItemAssignedToMeService> services;

    /**
     * Retrieves the items assigned to the logged-in user
     * @param page Page number
     * @param pageSize Page size number
     * @return {@link ItemDTOResponse}
     */
    @GetMapping
    @ApiOperation(value = "Retrieves the items assigned to the logged-in user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = ItemDTOResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<ItemDTOResponse> getAssignedItems(PmrvUser user,
        @RequestParam("page") @ApiParam(value = "The page number starting from zero")
        @Min(value = 0, message = "{parameter.page.typeMismatch}")
        @NotNull(message = "{parameter.page.typeMismatch}") Long page,
        @RequestParam("size") @ApiParam(value = "The page size")
        @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")
        @NotNull(message = "{parameter.pageSize.typeMismatch}") Long pageSize) {

        Optional<ItemAssignedToMeService> itemService = services.stream()
                .filter(itemAssignedToMeService -> itemAssignedToMeService.getRoleType().equals(user.getRoleType()))
                .findFirst();

        return itemService.map(itemAssignedToMeService -> new ResponseEntity<>(itemAssignedToMeService.getItemsAssignedToMe(
                    user, page, pageSize), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ItemDTOResponse.emptyItemDTOResponse(), HttpStatus.OK));
    }

    /**
     * Retrieves the items assigned to the logged-in user per account.
     *
     * @param id Account id
     * @param page Page number
     * @param pageSize Page size number
     * @return {@link ItemDTOResponse}
     */
    @GetMapping(path = "/account/{id}")
    @ApiOperation(value = "Retrieves the items assigned to the logged-in user per account", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = OK, response = ItemDTOResponse.class),
            @ApiResponse(code = 400, message = VALIDATION_PARAMETER_ERROR_BAD_REQUEST, response = ErrorResponse.class),
            @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#id")
    public ResponseEntity<ItemDTOResponse> getAssignedItemsByAccount(PmrvUser user,
            @PathVariable("id") @ApiParam(value = "The account id") Long id,
            @RequestParam("page") @ApiParam(value = "The page number starting from zero")
            @Min(value = 0, message = "{parameter.page.typeMismatch}")
            @NotNull(message = "{parameter.page.typeMismatch}") Long page,
            @RequestParam("size") @ApiParam(value = "The page size")
            @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")
            @NotNull(message = "{parameter.pageSize.typeMismatch}") Long pageSize) {

        Optional<ItemAssignedToMeService> itemService = services.stream()
                .filter(itemAssignedToMeService -> itemAssignedToMeService.getRoleType().equals(user.getRoleType()))
                .findFirst();

        return itemService.map(itemAssignedToMeService -> new ResponseEntity<>(itemAssignedToMeService.getItemsAssignedToMeByAccount(
                    user, id, page, pageSize), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ItemDTOResponse.emptyItemDTOResponse(), HttpStatus.OK));
    }
}
