package uk.gov.pmrv.api.web.controller.account;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.account.domain.dto.AccountUpdateLegalEntityNameDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountUpdateRegistryIdDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountUpdateSiteNameDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountUpdateSopIdDTO;
import uk.gov.pmrv.api.account.domain.dto.LocationDTO;
import uk.gov.pmrv.api.account.service.AccountUpdateService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;

@RestController
@RequestMapping(path = "/v1.0/accounts/{id}")
@RequiredArgsConstructor
@Validated
@Api(tags = "Account update")
public class AccountUpdateController {
    
    private final AccountUpdateService accountUpdateService;

    @PostMapping("/site-name")
    @ApiOperation(value = "Update the site name of the account", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateAccountSiteName(
            @PathVariable("id") @ApiParam(value = "The account id", required = true) Long accountId,
            @RequestBody @Valid @ApiParam(value = "The site name", required = true) AccountUpdateSiteNameDTO siteNameDTO) {
        accountUpdateService.updateAccountSiteName(accountId, siteNameDTO.getSiteName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
    
    @PostMapping("/registry-id")
    @ApiOperation(value = "Update the uk ets registry id of the account", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateAccountRegistryId(
            @PathVariable("id") @ApiParam(value = "The account id", required = true) Long accountId,
            @RequestBody @Valid @ApiParam(value = "The registry id", required = true) AccountUpdateRegistryIdDTO registryIdDTO) {
        accountUpdateService.updateAccountRegistryId(accountId, registryIdDTO.getRegistryId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/sop-id")
    @ApiOperation(value = "Update the sop id of the account", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateAccountSopId(
            @PathVariable("id") @ApiParam(value = "The account id", required = true) Long accountId,
            @RequestBody @Valid @ApiParam(value = "The sop id", required = true) AccountUpdateSopIdDTO sopIdDTO) {
        accountUpdateService.updateAccountSopId(accountId, sopIdDTO.getSopId());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/address")
    @ApiOperation(value = "Updates the address of the account", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateAccountAddress(
            @PathVariable("id") @ApiParam(value = "The account id", required = true) Long accountId,
            @RequestBody @Valid @ApiParam(value = "The address", required = true) LocationDTO address) {
        accountUpdateService.updateAccountAddress(accountId, address);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/legal-entity/address")
    @ApiOperation(value = "Updates the address of the Legal Entity", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateLegalEntityAddress(
        @PathVariable("id") @ApiParam(value = "The account id", required = true) Long accountId,
        @RequestBody @Valid @ApiParam(value = "The address", required = true) AddressDTO addressDTO) {
        accountUpdateService.updateLegalEntityAddress(accountId, addressDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PostMapping("/legal-entity/name")
    @ApiOperation(value = "Updates the name of the legal entity", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK),
        @ApiResponse(code = 400, message = SwaggerApiInfo.UPDATE_LEGAL_ENTITY_NAME_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateLegalEntityName(
        @PathVariable("id") @ApiParam(value = "The account id", required = true) Long accountId,
        @RequestBody @Valid @ApiParam(value = "The legal entity name", required = true) AccountUpdateLegalEntityNameDTO name) {
        accountUpdateService.updateLegalEntityName(accountId, name.getLegalEntityName());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
