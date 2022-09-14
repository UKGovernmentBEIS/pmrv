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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.pmrv.api.account.domain.dto.AccountContactDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactVbInfoResponse;
import uk.gov.pmrv.api.account.service.AccountVbSiteContactService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import java.util.List;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.VERIFIER;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

@Validated
@RestController
@RequestMapping(path = "/v1.0/vb-site-contacts")
@RequiredArgsConstructor
@Api(tags = "VB site contacts")
public class VbSiteContactController {

    private final AccountVbSiteContactService accountVbSiteContactService;

    /**
     * Retrieves the accounts verification body site contacts in which Verifier user has access.
     *
     * @param user {@link PmrvUser}
     * @param page Page number
     * @param pageSize Page size number
     * @return {@link AccountContactVbInfoResponse}
     */
    @AuthorizedRole(roleType = VERIFIER)
    @GetMapping
    @ApiOperation(value = "Retrieves the accounts and verification body site contact of the accounts", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = OK, response = AccountContactVbInfoResponse.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<AccountContactVbInfoResponse> getVbSiteContacts(
            PmrvUser user,
            @RequestParam("page") @ApiParam(value = "The page number starting from zero")
            @Min(value = 0, message = "{parameter.page.typeMismatch}")
            @NotNull(message = "{parameter.page.typeMismatch}") Integer page,
            @RequestParam("size") @ApiParam(value = "The page size")
            @Min(value = 1, message = "{parameter.pageSize.typeMismatch}")
            @NotNull(message = "{parameter.pageSize.typeMismatch}") Integer pageSize) {

        return new ResponseEntity<>(accountVbSiteContactService.getAccountsAndVbSiteContacts(user, page, pageSize), HttpStatus.OK);
    }

    /**
     * Updates accounts verification body site contact.
     *
     * @param user {@link PmrvUser}
     * @param vbSiteContacts List of {@link AccountContactDTO}
     * @return Empty response
     */
    @PostMapping
    @ApiOperation(value = "Updates verification body site contacts", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
            @ApiResponse(code = 400, message = SwaggerApiInfo.UPDATE_VB_SITE_CONTACTS_BAD_REQUEST, response = ErrorResponse.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Void> updateVbSiteContacts(
            PmrvUser user,
            @RequestBody @Valid @NotEmpty @ApiParam(value = "The accounts with updated verification body site contacts", required = true)
                    List<AccountContactDTO> vbSiteContacts) {
        accountVbSiteContactService.updateVbSiteContacts(user, vbSiteContacts);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
