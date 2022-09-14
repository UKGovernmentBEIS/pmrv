package uk.gov.pmrv.api.web.controller.user;

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
import uk.gov.pmrv.api.user.verifier.domain.AdminVerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserInvitationDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserInvitationService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;

/**
 * Controller for adding verifier users.
 */
@RestController
@RequestMapping(path = "/v1.0/verifier-users/invite")
@Api(tags = "Verifier Users Invitation")
@RequiredArgsConstructor
public class VerifierUserInvitationController {

    private final VerifierUserInvitationService verifierUserInvitationService;

    @PostMapping
    @ApiOperation(value = "Invite new verifier user to the verification body", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Void> inviteVerifierUser(
        PmrvUser currentUser,
        @RequestBody @Valid @ApiParam(value = "The verifier user information", required = true)
            VerifierUserInvitationDTO verifierUserInvitation) {
        verifierUserInvitationService.inviteVerifierUser(currentUser, verifierUserInvitation);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/vb/{id}")
    @ApiOperation(value = "Invite new admin verifier user to the verification body", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 404, message = SwaggerApiInfo.INVITE_ADMIN_VERIFIER_TO_VB_BAD_REQUEST, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Void> inviteVerifierAdminUserByVerificationBodyId(
            @PathVariable("id") @ApiParam(value = "The verification body id") Long verificationBodyId,
            PmrvUser currentUser,
            @RequestBody @Valid @ApiParam(value = "The verifier user information", required = true)
                    AdminVerifierUserInvitationDTO adminVerifierUserInvitationDTO) {
        verifierUserInvitationService.inviteVerifierAdminUser(currentUser, adminVerifierUserInvitationDTO, verificationBodyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
