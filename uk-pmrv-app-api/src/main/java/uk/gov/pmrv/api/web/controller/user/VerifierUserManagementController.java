package uk.gov.pmrv.api.web.controller.user;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.user.verifier.service.VerifierUserManagementService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

import javax.validation.Valid;

/**
 * Controller for managing verifier users.
 */
@RestController
@RequestMapping(path = "/v1.0/verifier-users")
@Api(tags = "Verifier Users")
@RequiredArgsConstructor
public class VerifierUserManagementController {

    private final VerifierUserManagementService verifierUserManagementService;

    /**
     * Retrieves info of verifier user by user id.
     *
     * @param userId Keycloak user id
     * @return {@link VerifierUserDTO}
     */
    @GetMapping(path = "/{userId}")
    @ApiOperation(value = "Retrieves the user of type VERIFIER for the given user id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = VerifierUserDTO.class),
            @ApiResponse(code = 400, message = SwaggerApiInfo.GET_VERIFIER_USER_BY_ID_BAD_REQUEST, response = ErrorResponse.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<VerifierUserDTO> getVerifierUserById(
            PmrvUser pmrvUser,
            @PathVariable("userId") @ApiParam(value = "The verifier user id") String userId) {
        return new ResponseEntity<>(verifierUserManagementService.getVerifierUserById(pmrvUser, userId),
                HttpStatus.OK);
    }

    /**
     * Updates verifier user by user id.
     *
     * @param pmrvUser {@link PmrvUser}
     * @param verifierUserDTO {@link VerifierUserDTO}
     * @return {@link VerifierUserDTO}
     */
    @PatchMapping(path = "/{userId}")
    @ApiOperation(value = "Updates verifier user by user id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = VerifierUserDTO.class),
            @ApiResponse(code = 400, message = SwaggerApiInfo.UPDATE_VERIFIER_USER_BY_ID_BAD_REQUEST, response = ErrorResponse.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<VerifierUserDTO> updateVerifierUserById(
            PmrvUser pmrvUser,
            @PathVariable("userId") @ApiParam(value = "The verifier user id") String userId,
            @RequestBody @Valid @ApiParam(value = "The modified verifier user", required = true) VerifierUserDTO verifierUserDTO) {
        verifierUserManagementService.updateVerifierUserById(pmrvUser, userId, verifierUserDTO);
        return new ResponseEntity<>(verifierUserDTO, HttpStatus.OK);
    }

    /**
     * Updates logged in verifier user.
     *
     * @param pmrvUser {@link PmrvUser}
     * @param verifierUserDTO {@link VerifierUserDTO}
     * @return {@link VerifierUserDTO}
     */
    @PatchMapping(path = "/verifier")
    @ApiOperation(value = "Updates logged in verifier user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = VerifierUserDTO.class),
            @ApiResponse(code = 400, message = SwaggerApiInfo.AUTHORITY_USER_NOT_RELATED_TO_VERIFICATION_BODY, response = ErrorResponse.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = RoleType.VERIFIER)
    public ResponseEntity<VerifierUserDTO> updateCurrentVerifierUser(
            PmrvUser pmrvUser,
            @RequestBody @Valid @ApiParam(value = "The modified verifier user", required = true) VerifierUserDTO verifierUserDTO) {
        verifierUserManagementService.updateCurrentVerifierUser(pmrvUser, verifierUserDTO);
        return new ResponseEntity<>(verifierUserDTO, HttpStatus.OK);
    }
}
