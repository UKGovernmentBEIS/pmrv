package uk.gov.pmrv.api.web.controller.authorization;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.authorization.operator.service.OperatorAuthorityDeletionService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.AccountOperatorUserAuthorityQueryOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.AccountOperatorUserAuthorityUpdateOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.dto.AccountOperatorAuthorityUpdateWrapperDTO;
import uk.gov.pmrv.api.web.orchestrator.dto.AccountOperatorsUsersAuthoritiesInfoDTO;
import uk.gov.pmrv.api.web.security.Authorized;

import javax.validation.Valid;

@Validated
@RestController
@RequestMapping(path = "/v1.0/operator-authorities")
@Api(tags = "Operator Authorities")
@RequiredArgsConstructor
public class OperatorAuthorityController {

	private final AccountOperatorUserAuthorityQueryOrchestrator accountOperatorUserAuthorityQueryOrchestrator;
	private final AccountOperatorUserAuthorityUpdateOrchestrator accountOperatorUserAuthorityUpdateOrchestrator;
    private final OperatorAuthorityDeletionService operatorAuthorityDeletionService;

    @GetMapping(path = "/account/{accountId}")
    @ApiOperation(value = "Retrieves the authorities of type OPERATOR for the given account id along with the account contact types", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = AccountOperatorsUsersAuthoritiesInfoDTO.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<AccountOperatorsUsersAuthoritiesInfoDTO> getAccountOperatorAuthorities(
            PmrvUser currentUser,
            @PathVariable("accountId") @ApiParam(value = "The account id") Long accountId) {
        return new ResponseEntity<>(
                accountOperatorUserAuthorityQueryOrchestrator.getAccountOperatorsUsersAuthoritiesInfo(currentUser, accountId),
                HttpStatus.OK);
    }

    @PostMapping(path = "/account/{accountId}")
    @ApiOperation(value = "Updates authorities for users of type OPERATOR for the given account id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 400, message = SwaggerApiInfo.UPDATE_ACCOUNT_OPERATOR_AUTHORITY_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> updateAccountOperatorAuthorities(
        @PathVariable("accountId") @ApiParam(value = "The account id")
            Long accountId,
        @RequestBody @Valid @ApiParam(value = "The account operator authorities to update", required = true)
        AccountOperatorAuthorityUpdateWrapperDTO accountOperatorAuthorityUpdateWrapper) {
        accountOperatorUserAuthorityUpdateOrchestrator.updateAccountOperatorAuthorities(
    	        accountOperatorAuthorityUpdateWrapper.getAccountOperatorAuthorityUpdateList(), 
    	        accountOperatorAuthorityUpdateWrapper.getContactTypes(), 
    	        accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/account/{accountId}/{userId}")
    @ApiOperation(value = "Deletes authority from the account", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 400, message = SwaggerApiInfo.DELETE_ACCOUNT_OPERATOR_AUTHORITY_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> deleteAccountOperatorAuthority(
        @PathVariable("accountId") @ApiParam(value = "The account id") Long accountId,
        @PathVariable("userId") @ApiParam(value = "The user id") String userId) {
        operatorAuthorityDeletionService.deleteAccountOperatorAuthority(userId, accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/account/{accountId}")
    @ApiOperation(value = "Deletes logged in authority from the account", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 400, message = SwaggerApiInfo.DELETE_ACCOUNT_OPERATOR_AUTHORITY_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> deleteCurrentUserAccountOperatorAuthority(
        PmrvUser pmrvUser,
        @PathVariable("accountId") @ApiParam(value = "The account id") Long accountId) {
        operatorAuthorityDeletionService.deleteAccountOperatorAuthority(pmrvUser.getUserId(), accountId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
