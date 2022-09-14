package uk.gov.pmrv.api.web.controller.user;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.ENABLE_VERIFIER_USER_FROM_INVITATION_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;

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
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserEnableDTO;
import uk.gov.pmrv.api.user.core.domain.dto.InvitedUserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserAcceptInvitationService;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInvitationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

@RestController
@RequestMapping(path = "/v1.0/verifier-users/registration")
@Api(tags = "Verifier users registration")
@RequiredArgsConstructor
public class VerifierUserRegistrationController {

    private final VerifierUserInvitationService verifierUserInvitationService;
    private final VerifierUserAcceptInvitationService verifierUserAcceptInvitationService;

    @PostMapping(path = "/accept-invitation")
    @ApiOperation(value = "Accept invitation for verifier user")
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = InvitedUserInfoDTO.class),
        @ApiResponse(code = 400, message = SwaggerApiInfo.ACCEPT_VERIFIER_USER_INVITATION_BAD_REQUEST ,response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<InvitedUserInfoDTO> acceptVerifierInvitation(
        @RequestBody @Valid @ApiParam(value = "The invitation token", required = true) TokenDTO invitationTokenDTO) {
        return new ResponseEntity<>(verifierUserInvitationService.acceptInvitation(invitationTokenDTO.getToken()), HttpStatus.OK);
    }

    @PutMapping(path = "/enable-from-invitation")
    @ApiOperation(value = "Enable a new verifier user from invitation")
    @ApiResponses({
        @ApiResponse(code = 204, message = NO_CONTENT),
        @ApiResponse(code = 400, message = ENABLE_VERIFIER_USER_FROM_INVITATION_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Void> acceptAndEnableVerifierInvitedUser(
        @RequestBody @Valid @ApiParam(value = "The verifier user", required = true)
            InvitedUserEnableDTO invitedUserEnableDTO) {
        verifierUserAcceptInvitationService.acceptAndEnableVerifierInvitedUser(invitedUserEnableDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
