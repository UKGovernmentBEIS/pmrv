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
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemAssignedToOthersService;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.VERIFIER;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/items/assigned-to-others")
@Api(tags = "Items Assigned To Others")
@Validated
@RequiredArgsConstructor
public class ItemAssignedToOthersController {

    private final List<ItemAssignedToOthersService> services;

    /**
     * Retrieves the items assigned to users different than the logged-in user of the same role type who participate in the same accounts
     * @param page Page number
     * @param pageSize Page size number
     * @return {@link ItemDTOResponse}
     */
    @GetMapping
    @ApiOperation(value = "Retrieves the items assigned to users different than the logged-in user of the same role type who participate in the same accounts", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = ItemDTOResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR, VERIFIER})
    public ResponseEntity<ItemDTOResponse> getAssignedToOthersItems(
            PmrvUser user,
            @RequestParam("page") @ApiParam(value = "The page number starting from zero")
            @Min(value = 0, message = "{parameter.page.typeMismatch}")
            @NotNull(message = "{parameter.page.typeMismatch}") Long page,
            @RequestParam("size") @ApiParam(value = "The page size")
            @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")
            @NotNull(message = "{parameter.pageSize.typeMismatch}") Long pageSize) {

        Optional<ItemAssignedToOthersService> itemService = services.stream()
                .filter(itemAssignedToOthersService -> itemAssignedToOthersService.getRoleType().equals(user.getRoleType()))
                .findFirst();

        return itemService.map(itemAssignedToOthersService -> new ResponseEntity<>(itemAssignedToOthersService.getItemsAssignedToOthers(
                user, page, pageSize), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ItemDTOResponse.emptyItemDTOResponse(), HttpStatus.OK));
    }

    /**
     * Retrieves the items assigned to users different than the logged-in user of the same role type who participate in the same account id
     * @param page Page number
     * @param pageSize Page size number
     * @return {@link ItemDTOResponse}
     */
    @GetMapping(path = "/account/{accountId}")
    @ApiOperation(value = "Retrieves the items assigned to users different than the logged-in user of the same role type who participate in the same account id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = ItemDTOResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<ItemDTOResponse> getItemsAssignedToOthersByAccount(
            @PathVariable("accountId") @ApiParam(value = "The account id") Long accountId,
            @RequestParam("page") @ApiParam(value = "The page number starting from zero")
            @Min(value = 0, message = "{parameter.page.typeMismatch}")
            @NotNull(message = "{parameter.page.typeMismatch}") Long page,
            @RequestParam("size") @ApiParam(value = "The page size")
            @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")
            @NotNull(message = "{parameter.pageSize.typeMismatch}") Long pageSize,
            PmrvUser user) {

        Optional<ItemAssignedToOthersService> itemService = services.stream()
                .filter(itemAssignedToOthersService -> itemAssignedToOthersService.getRoleType().equals(user.getRoleType()))
                .findFirst();

        return itemService.map(itemAssignedToOthersService -> new ResponseEntity<>(itemAssignedToOthersService.getItemsAssignedToOthersByAccount(
                user, accountId, page, pageSize), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ItemDTOResponse.emptyItemDTOResponse(), HttpStatus.OK));
    }

}
