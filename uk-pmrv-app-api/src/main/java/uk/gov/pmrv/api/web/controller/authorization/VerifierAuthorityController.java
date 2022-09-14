package uk.gov.pmrv.api.web.controller.authorization;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.pmrv.api.authorization.verifier.domain.VerifierAuthorityUpdateDTO;
import uk.gov.pmrv.api.authorization.verifier.service.VerifierAuthorityDeletionService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.VerifierUserAuthorityQueryOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.VerifierUserAuthorityUpdateOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.dto.UsersAuthoritiesInfoDTO;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/v1.0/verifier-authorities")
@Api(tags = "Verifier authorities")
@RequiredArgsConstructor
public class VerifierAuthorityController {
	
	private final VerifierUserAuthorityQueryOrchestrator verifierUserAuthorityQueryOrchestrator;
	private final VerifierUserAuthorityUpdateOrchestrator verifierUserAuthorityUpdateOrchestrator;
	private final VerifierAuthorityDeletionService verifierAuthorityDeletionService;
	
	@GetMapping
    @ApiOperation(value = "Retrieves the list of verifier authorities related to the verification body to which I have been assigned to", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = UsersAuthoritiesInfoDTO.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = RoleType.VERIFIER)
    public ResponseEntity<UsersAuthoritiesInfoDTO> getVerifierAuthorities(PmrvUser currentUser) {
        return new ResponseEntity<>(
            verifierUserAuthorityQueryOrchestrator.getVerifierUsersAuthoritiesInfo(currentUser),
        		HttpStatus.OK);
    }

	@GetMapping("/vb/{id}")
	@ApiOperation(value = "Retrieves the list of verifier authorities related to the verification body", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
	@ApiResponses({
			@ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = UsersAuthoritiesInfoDTO.class),
			@ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
			@ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
	})
	@Authorized
	public ResponseEntity<UsersAuthoritiesInfoDTO> getVerifierAuthoritiesByVerificationBodyId(
			@PathVariable("id") @ApiParam(value = "The verification body id") Long verificationBodyId) {
		return new ResponseEntity<>(verifierUserAuthorityQueryOrchestrator.getVerifierAuthoritiesByVerificationBodyId(verificationBodyId),
				HttpStatus.OK);
	}
	
	@PostMapping
    @ApiOperation(value = "Updates the verifier authorities", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 400, message = SwaggerApiInfo.UPDATE_VERIFIER_AUTHORITY_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Void> updateVerifierAuthorities(
    		PmrvUser currentUser,
    		@RequestBody @Valid @NotEmpty @ApiParam(value = "The verifier authorities to update", required = true)
				List<VerifierAuthorityUpdateDTO> verifierAuthorities){
    	Long verificationBodyId = currentUser.getVerificationBodyId();
		verifierUserAuthorityUpdateOrchestrator.updateVerifierAuthorities(verifierAuthorities, verificationBodyId);
    	return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping("/vb/{id}")
    @ApiOperation(value = "Updates the verifier authorities of the specified verification body", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 400, message = SwaggerApiInfo.UPDATE_VERIFIER_AUTHORITY_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Void> updateVerifierAuthoritiesByVerificationBodyId(
        @RequestBody @Valid @NotEmpty @ApiParam(value = "The verifier authorities to update", required = true)
            List<VerifierAuthorityUpdateDTO> verifierAuthorities,
        @PathVariable("id") @ApiParam(value = "The verification body id") Long verificationBodyId){
		verifierUserAuthorityUpdateOrchestrator.updateVerifierAuthorities(verifierAuthorities, verificationBodyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

	@DeleteMapping(path = "/{userId}")
	@ApiOperation(value = "Delete the verifier user that corresponds to the provided user id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
	@ApiResponses({
		@ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 400, message = SwaggerApiInfo.DELETE_VERIFIER_AUTHORITY_BAD_REQUEST, response = ErrorResponse.class),
		@ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
		@ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
	})
	@Authorized
	public ResponseEntity<Void> deleteVerifierAuthority(
		PmrvUser authUser,
		@PathVariable("userId") @ApiParam(value = "The regulator to be deleted") String userId) {
		verifierAuthorityDeletionService.deleteVerifierAuthority(userId, authUser);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}

	@DeleteMapping
	@ApiOperation(value = "Delete the current verifier user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
	@ApiResponses({
		@ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 400, message = SwaggerApiInfo.DELETE_CURRENT_VERIFIER_AUTHORITY_BAD_REQUEST, response = ErrorResponse.class),
		@ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
		@ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
	})
	@Authorized
	public ResponseEntity<Void> deleteCurrentVerifierAuthority(PmrvUser authUser) {
		verifierAuthorityDeletionService.deleteVerifierAuthority(authUser.getUserId(), authUser.getVerificationBodyId());
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
}
