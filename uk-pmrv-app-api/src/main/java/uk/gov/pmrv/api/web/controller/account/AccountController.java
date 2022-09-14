package uk.gov.pmrv.api.web.controller.account;

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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchResults;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@RestController
@Validated
@RequestMapping(path = "/v1.0/accounts")
@RequiredArgsConstructor
@Api(tags = "Accounts")
public class AccountController {

    private final AccountQueryService accountQueryService;

    @GetMapping
    @ApiOperation(value = "Retrieves the current user associated accounts", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = AccountSearchResults.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR})
    public ResponseEntity<AccountSearchResults> getCurrentUserAccounts(
            PmrvUser pmrvUser,
            @RequestParam(value = "term", required = false) @Size(min = 3, max = 256) @ApiParam(value = "The term to search") String term,
            @RequestParam(value = "type", required = false)  @ApiParam(value = "The account type") AccountType type,
            @RequestParam(value = "page") @NotNull @ApiParam(value = "The page number starting from zero") @Min(value = 0, message = "{parameter.page.typeMismatch}") Long page,
            @RequestParam(value = "size") @NotNull @ApiParam(value = "The page size") @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")  Long pageSize
            ) {
        return new ResponseEntity<>(
            accountQueryService.getAccountsByUserAndSearchCriteria(pmrvUser, 
                    AccountSearchCriteria.builder()
                        .term(term)
                        .type(type)
                        .page(page)
                        .pageSize(pageSize)
                        .build()),
            HttpStatus.OK);
    }

    @GetMapping("/name")
    @ApiOperation(value = "Checks if account name exists", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Boolean> isExistingAccountName(
        @ApiParam(value = "The account name") @RequestParam("name") String accountName) {
    	boolean exists = accountQueryService.isExistingActiveAccountName(accountName);
    	return new ResponseEntity<>(exists, HttpStatus.OK);
    }
}
