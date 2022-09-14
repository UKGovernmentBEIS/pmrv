package uk.gov.pmrv.api.web.controller.user;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OPERATOR_USER_ACCOUNT_REGISTRATION_BAD_REQUEST;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserInvitationDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserInvitationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;

/**
 * Controller for adding operator users.
 */
@RestController
@RequestMapping(path = "/v1.0/operator-users/invite")
@Api(tags = "Operator Users invitation")
@RequiredArgsConstructor
public class OperatorUserInvitationController {

    private final OperatorUserInvitationService operatorUserInvitationService;

    @PostMapping(path = "/account/{accountId}")
    @ApiOperation(value = "Adds a new operator user to an account with a specified role.")
    @ApiResponses({
        @ApiResponse(code = 204, message = NO_CONTENT),
        @ApiResponse(code = 400, message = OPERATOR_USER_ACCOUNT_REGISTRATION_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<Void> inviteOperatorUserToAccount(
        PmrvUser currentUser,
        @PathVariable("accountId") @ApiParam(value = "The account id") Long accountId,
        @RequestBody @Valid @ApiParam(value = "The operator user account registration info", required = true)
            OperatorUserInvitationDTO operatorUserInvitationDTO) {
        operatorUserInvitationService.inviteUserToAccount(accountId, operatorUserInvitationDTO, currentUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
