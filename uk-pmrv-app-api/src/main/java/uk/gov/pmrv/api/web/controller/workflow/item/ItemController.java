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
import org.springframework.web.bind.annotation.RestController;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.service.ItemService;

import java.util.List;
import java.util.Optional;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@RequestMapping(path = "/v1.0/items")
@Api(tags = "Request items")
@Validated
@RequiredArgsConstructor
public class ItemController {

    private final List<ItemService> itemServices;

    @GetMapping(path = "/{request-id}")
    @ApiOperation(value = "Retrieves the items by request", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = OK, response = ItemDTOResponse.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#requestId")
    public ResponseEntity<ItemDTOResponse> getItemsByRequest(
            PmrvUser user,
            @PathVariable("request-id") @ApiParam(value = "The request id") String requestId) {

        Optional<ItemService> itemsService = itemServices.stream()
                .filter(service -> service.getRoleType().equals(user.getRoleType()))
                .findFirst();

        return itemsService.map(service -> new ResponseEntity<>(service.getItemsByRequest(
                user, requestId), HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(ItemDTOResponse.emptyItemDTOResponse(), HttpStatus.OK));
    }
}
