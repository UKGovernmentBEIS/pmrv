package uk.gov.pmrv.api.web.controller.account;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.domain.dto.AccountContactDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoResponse;
import uk.gov.pmrv.api.account.service.AccountCaSiteContactService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

@Validated
@RestController
@RequestMapping(path = "/v1.0/ca-site-contacts")
@RequiredArgsConstructor
@Api(tags = "Ca site contacts")
public class CaSiteContactController {

    private final AccountCaSiteContactService accountCaSiteContactService;

    /**
     * Retrieves the accounts competent authority site contacts in which Regulator user has access.
     *
     * @param user {@link PmrvUser}
     * @param page Page number
     * @param pageSize Page size number
     * @return {@link AccountContactInfoResponse}
     */
    @AuthorizedRole(roleType = REGULATOR)
    @GetMapping
    @ApiOperation(value = "Retrieves the accounts and competent authority site contact of the accounts", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = OK, response = AccountContactInfoResponse.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<AccountContactInfoResponse> getCaSiteContacts(
            PmrvUser user,
            @RequestParam("page") @ApiParam(value = "The page number starting from zero")
            @Min(value = 0, message = "{parameter.page.typeMismatch}")
            @NotNull(message = "{parameter.page.typeMismatch}") Integer page,
            @RequestParam("size") @ApiParam(value = "The page size")
            @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")
            @NotNull(message = "{parameter.pageSize.typeMismatch}") Integer pageSize) {

        return new ResponseEntity<>(accountCaSiteContactService.getAccountsAndCaSiteContacts(user, page, pageSize), HttpStatus.OK);
    }

    /**
     * Updates accounts competent authority site contact.
     *
     * @param user {@link PmrvUser}
     * @param caSiteContacts List of {@link AccountContactDTO}
     * @return Empty response
     */
    @PostMapping
    @ApiOperation(value = "Updates competent authority site contacts", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
            @ApiResponse(code = 400, message = SwaggerApiInfo.UPDATE_CA_SITE_CONTACTS_BAD_REQUEST, response = ErrorResponse.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Void> updateCaSiteContacts(
            PmrvUser user,
            @RequestBody @Valid @NotEmpty @ApiParam(value = "The accounts with updated competent authority site contacts", required = true)
                    List<AccountContactDTO> caSiteContacts) {
        accountCaSiteContactService.updateCaSiteContacts(user, caSiteContacts);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
