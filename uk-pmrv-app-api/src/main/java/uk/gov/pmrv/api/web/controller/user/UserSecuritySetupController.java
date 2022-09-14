package uk.gov.pmrv.api.web.controller.user;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.core.domain.dto.OneTimePasswordDTO;
import uk.gov.pmrv.api.user.core.domain.dto.TokenDTO;
import uk.gov.pmrv.api.user.core.service.UserSecuritySetupService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.PmrvSecurityComponent;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/v1.0/users/security-setup")
@Api(tags = "Users Security Setup")
@RequiredArgsConstructor
public class UserSecuritySetupController {

    private final UserSecuritySetupService userSecuritySetupService;
    private final PmrvSecurityComponent pmrvSecurityComponent;

    @PostMapping(path = "/2fa/request-change")
    @ApiOperation(value = "Requests the update of the two factor authentication", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 400, message = SwaggerApiInfo.REQUEST_TO_CHANGE_2FA_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Void> requestTwoFactorAuthChange
        (PmrvUser currentUser,
         @RequestBody @Valid @ApiParam(value = "The one time authenticator code", required = true)
             OneTimePasswordDTO oneTimePasswordDTO) {
        userSecuritySetupService.requestTwoFactorAuthChange(currentUser, pmrvSecurityComponent.getAccessToken(),
            oneTimePasswordDTO.getPassword());
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/2fa/delete")
    @ApiOperation(value = "Delete the two factor authentication")
    @ApiResponses({
            @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
            @ApiResponse(code = 400, message = SwaggerApiInfo.REMOVE_2FA_BAD_REQUEST,response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Void> deleteOtpCredentials(
            @RequestBody @Valid @ApiParam(value = "The change 2FA token", required = true) TokenDTO tokenDTO) {
        userSecuritySetupService.deleteOtpCredentials(tokenDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
