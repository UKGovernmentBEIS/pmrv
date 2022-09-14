package uk.gov.pmrv.api.web.controller.user;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.ENABLE_OPERATOR_USER_FROM_INVITATION_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.REGISTER_OPERATOR_USER_FROM_INVITATION_WOUT_CREDENTIALS_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.USERS_INVITATION_TOKEN_VERIFICATION_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.USERS_TOKEN_VERIFICATION_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.VALIDATION_ERROR_BAD_REQUEST;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.user.core.domain.dto.EmailDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserEnableDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorInvitedUserInfoDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationDTO;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserRegistrationWithCredentialsDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserAcceptInvitationService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserActivationService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserRegistrationService;
import uk.gov.pmrv.api.user.operator.service.OperatorUserTokenVerificationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

@RestController
@RequestMapping(path = "/v1.0/operator-users/registration")
@Api(tags = "Operator users registration")
@RequiredArgsConstructor
public class OperatorUserRegistrationController {
	
	private final OperatorUserTokenVerificationService operatorUserTokenVerificationService;
	private final OperatorUserRegistrationService operatorUserRegistrationService;
    private final OperatorUserActivationService operatorUserActivationService;
	private final OperatorUserAcceptInvitationService operatorUserAcceptInvitationService;

	/**
     * Sends a verification email to the provided email.
     *
     * @param emailDTO {@link EmailDTO}
     */
    @PostMapping(path = "/verification-email")
    @ApiOperation(value = "Sends a verification email")
    @ApiResponses({
        @ApiResponse(code = 204, message = NO_CONTENT),
        @ApiResponse(code = 400, message = VALIDATION_ERROR_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Void> sendVerificationEmail(
        @RequestBody @Valid @ApiParam(value = "The user email", required = true) EmailDTO emailDTO) {
    	operatorUserRegistrationService.sendVerificationEmail(emailDTO.getEmail());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    /**
     * Verifies the JWT token.
     *
     * @param tokenDTO {@link TokenDTO}
     * @return {@link EmailDTO}
     */
    @PostMapping(path = "/token-verification")
    @ApiOperation(value = "Verifies the JWT token provided in the email")
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = EmailDTO.class),
        @ApiResponse(code = 400, message = USERS_TOKEN_VERIFICATION_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<EmailDTO> verifyUserRegistrationToken
    (@RequestBody @Valid @ApiParam(value = "The verification token", required = true) TokenDTO tokenDTO) {
        String email = operatorUserTokenVerificationService.verifyRegistrationToken(tokenDTO.getToken());
        return new ResponseEntity<>(new EmailDTO(email), HttpStatus.OK);
    }

    /**
     * Registers an operator user.
     * @param userRegistrationDTO a userRegistrationDTO.
     * @return {@link OperatorUserDTO}
     */
    @PostMapping(path = "/register")
    @ApiOperation(value = "Register a new operator user")
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = OperatorUserDTO.class),
        @ApiResponse(code = 400, message = VALIDATION_ERROR_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<OperatorUserDTO> registerUser(@RequestBody @Valid @ApiParam(value = "The userRegistrationDTO", required = true)
                                                            OperatorUserRegistrationWithCredentialsDTO userRegistrationDTO) {
        return new ResponseEntity<>(operatorUserRegistrationService.registerUser(userRegistrationDTO), HttpStatus.OK);
    }

    /**
     * Registers a new operator user from invitation token.
     *
     * @param operatorUserRegistrationWithCredentialsDTO {@link OperatorUserRegistrationWithCredentialsDTO}.
     */
    @PutMapping(path = "/register-from-invitation")
    @ApiOperation(value = "Registers a new operator user from invitation token")
    @ApiResponses({
            @ApiResponse(code = 200, message = OK, response = OperatorUserDTO.class),
            @ApiResponse(code = 400, message = USERS_INVITATION_TOKEN_VERIFICATION_BAD_REQUEST, response = ErrorResponse.class),
            @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<OperatorUserDTO> registerNewUserFromInvitationWithCredentials(
            @RequestBody @Valid @ApiParam(value = "The operator user", required = true)
                OperatorUserRegistrationWithCredentialsDTO operatorUserRegistrationWithCredentialsDTO) {
        return new ResponseEntity<>(operatorUserActivationService.activateAndEnableOperatorInvitedUser(
            operatorUserRegistrationWithCredentialsDTO), HttpStatus.OK);
    }

    @PutMapping(path = "/register-from-invitation-no-credentials")
    @ApiOperation(value = "Registers a new operator user from invitation token without credentials")
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = OperatorUserDTO.class),
        @ApiResponse(code = 400, message = REGISTER_OPERATOR_USER_FROM_INVITATION_WOUT_CREDENTIALS_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<OperatorUserDTO> registerNewUserFromInvitation(
        @RequestBody @Valid @ApiParam(value = "The operator user", required = true)
            OperatorUserRegistrationDTO operatorUserRegistrationDTO) {
        return new ResponseEntity<>(operatorUserActivationService
            .activateOperatorInvitedUser(operatorUserRegistrationDTO), HttpStatus.OK);
    }

    @PutMapping(path = "/enable-from-invitation")
    @ApiOperation(value = "Enables a new operator user from invitation")
    @ApiResponses({
        @ApiResponse(code = 204, message = NO_CONTENT, response = OperatorUserDTO.class),
        @ApiResponse(code = 400, message = ENABLE_OPERATOR_USER_FROM_INVITATION_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Void> enableOperatorInvitedUser(
        @RequestBody @Valid @ApiParam(value = "The operator user credentials", required = true)
            InvitedUserEnableDTO invitedUserEnableDTO) {
        operatorUserActivationService.enableOperatorInvitedUser(invitedUserEnableDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping(path = "/accept-invitation")
    @ApiOperation(value = "Accept invitation for operator user")
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = OperatorInvitedUserInfoDTO.class),
        @ApiResponse(code = 400, message = SwaggerApiInfo.ACCEPT_OPERATOR_INVITATION_TOKEN_BAD_REQUEST ,response = ErrorResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<OperatorInvitedUserInfoDTO> acceptOperatorInvitation(
        @RequestBody @Valid @ApiParam(value = "The invitation token", required = true) TokenDTO invitationTokenDTO) {
        OperatorInvitedUserInfoDTO operatorInvitedUserInfo =
            operatorUserAcceptInvitationService.acceptInvitation(invitationTokenDTO.getToken());
        return new ResponseEntity<>(operatorInvitedUserInfo, HttpStatus.OK);
    }
}
