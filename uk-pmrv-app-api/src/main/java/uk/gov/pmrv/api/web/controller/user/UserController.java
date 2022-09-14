package uk.gov.pmrv.api.web.controller.user;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.feedback.domain.dto.UserFeedbackDto;
import uk.gov.pmrv.api.feedback.service.UserFeedbackService;
import uk.gov.pmrv.api.terms.domain.dto.UpdateTermsDTO;
import uk.gov.pmrv.api.user.application.UserService;
import uk.gov.pmrv.api.user.core.domain.dto.ApplicationUserDTO;
import uk.gov.pmrv.api.user.core.domain.dto.FileToken;
import uk.gov.pmrv.api.user.core.service.UserSignatureService;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.regulator.domain.RegulatorUserDTO;
import uk.gov.pmrv.api.user.verifier.domain.VerifierUserDTO;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

/**
 * Controller for users.
 */
@RestController
@RequestMapping(path = "/v1.0/users")
@Api(tags = "Users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserAuthService userAuthService;
    private final UserSignatureService userSignatureService;
    private final UserFeedbackService userFeedbackService;

    /**
     * Updates accepted terms and conditions of the logged in user.
     *
     * @param updateTermsDTO a terms transfer object.
     */
    @PatchMapping(path = "/terms-and-conditions")
    @ApiOperation(value = "Updates accepted terms and conditions of the logged in user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = UpdateTermsDTO.class),
        @ApiResponse(code = 400, message = BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<UpdateTermsDTO> editUserTerms(
        PmrvUser pmrvUser,
        @RequestBody @Valid @ApiParam(value = "The updateTermsDTO", required = true) UpdateTermsDTO updateTermsDTO) {
        userAuthService.updateUserTerms(pmrvUser.getUserId(), updateTermsDTO.getVersion());
        return new ResponseEntity<>(updateTermsDTO, HttpStatus.OK);
    }

    /**
     * Retrieves info of the logged in user.
     *
     * @return {@link ApplicationUserDTO}
     */
    @GetMapping
    @ApiOperation(value = "Retrieves info of the logged in user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = ApplicationUserDTO.class),
        @ApiResponse(code = 200, message = OK, response = OperatorUserDTO.class),
        @ApiResponse(code = 200, message = OK, response = RegulatorUserDTO.class),
        @ApiResponse(code = 200, message = OK, response = VerifierUserDTO.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<ApplicationUserDTO> getCurrentUser(PmrvUser pmrvUser) {
        return new ResponseEntity<>(userService.getUserById(pmrvUser.getUserId()), HttpStatus.OK);
    }

    @GetMapping(path = "/signature")
    @ApiOperation(value = "Generate the token to get the signature of the current user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = FileToken.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<FileToken> generateGetCurrentUserSignatureToken(
        PmrvUser pmrvUser,
        @RequestParam("signatureUuid") @ApiParam(value = "The signature uuid") @NotNull UUID signatureUuid) {
        FileToken getFileToken = userSignatureService.generateSignatureFileToken(pmrvUser.getUserId(), signatureUuid);
        return new ResponseEntity<>(getFileToken, HttpStatus.OK);
    }

    @PostMapping(path = "/feedback")
    @ApiOperation(value = "Provides the feedback about the service for the logged in user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 400, message = BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Void> provideUserFeedback(
        PmrvUser pmrvUser,
        @RequestBody @Valid @ApiParam(value = "The user feedback", required = true) UserFeedbackDto userFeedbackDto) {
        String domainUrl = ServletUriComponentsBuilder.fromCurrentContextPath().build().toUriString();
        userFeedbackService.sendFeedback(domainUrl, userFeedbackDto);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
