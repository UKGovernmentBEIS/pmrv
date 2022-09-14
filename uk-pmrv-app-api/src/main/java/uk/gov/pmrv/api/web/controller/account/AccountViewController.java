package uk.gov.pmrv.api.web.controller.account;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.account.domain.dto.AccountDetailsDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountHeaderInfoDTO;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.AccountPermitQueryOrchestrator;
import uk.gov.pmrv.api.web.security.Authorized;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

/**
 * Rest controller that provides info about an account. <br/>
 */
@RestController
@RequestMapping(path = "/v1.0/account")
@RequiredArgsConstructor
@Api(tags = "Account view")
public class AccountViewController {

    private final AccountPermitQueryOrchestrator orchestrator;

    @GetMapping("/{id}")
    @ApiOperation(value = "Get the account with the provided id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<AccountDetailsDTO> getAccountById(
        @ApiParam(value = "The account id") @PathVariable("id") Long accountId) {
        return new ResponseEntity<>(orchestrator.getAccountDetailsDtoWithPermit(accountId), HttpStatus.OK);
    }

    @GetMapping("/{id}/header-info")
    @ApiOperation(value = "Get the account header info for the provided account", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = AccountHeaderInfoDTO.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<AccountHeaderInfoDTO> getAccountHeaderInfoById(
        @ApiParam(value = "The account id") @PathVariable("id") Long accountId) {
        return new ResponseEntity<>(orchestrator.getAccountHeaderInfoWithPermitId(accountId).orElse(null), HttpStatus.OK);
    }

}
