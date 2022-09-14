package uk.gov.pmrv.api.web.controller.verificationbody;

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
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyInfoResponseDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyUpdateDTO;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyUpdateStatusDTO;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyDeletionService;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyQueryService;
import uk.gov.pmrv.api.verificationbody.service.VerificationBodyUpdateService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.VerificationBodyAndUserOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.dto.VerificationBodyCreationDTO;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

import javax.validation.Valid;
import java.util.List;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;

@RestController
@RequestMapping(path = "/v1.0/verification-bodies")
@Api(tags = "Verification Bodies")
@RequiredArgsConstructor
@Validated
public class VerificationBodyController {

    private final VerificationBodyAndUserOrchestrator verificationBodyAndUserOrchestrator;
    private final VerificationBodyQueryService verificationBodyQueryService;
    private final VerificationBodyUpdateService verificationBodyUpdateService;
    private final VerificationBodyDeletionService verificationBodyDeletionService;

    /**
     * Retrieves all verification bodies.
     *
     * @param pmrvUser {@link PmrvUser}
     * @return List of {@link VerificationBodyInfoResponseDTO}
     */
    @GetMapping
    @ApiOperation(value = "Retrieves all verification bodies", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = VerificationBodyInfoResponseDTO.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = REGULATOR)
    public ResponseEntity<VerificationBodyInfoResponseDTO> getVerificationBodies(PmrvUser pmrvUser) {
        return new ResponseEntity<>(verificationBodyQueryService.getVerificationBodies(pmrvUser),
                HttpStatus.OK);
    }
    
    @GetMapping("/{id}")
    @ApiOperation(value = "Get the verification body with the provided id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = VerificationBodyDTO.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<VerificationBodyDTO> getVerificationBodyById(
        @ApiParam(value = "The verification body id") @PathVariable("id") Long verificationBodyId) {
        return new ResponseEntity<>(
                verificationBodyQueryService.getVerificationBodyById(verificationBodyId), 
                HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @ApiOperation(value = "Delete the verification body with the provided id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.NO_CONTENT),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Void> deleteVerificationBodyById(
            @ApiParam(value = "The verification body id") @PathVariable("id") Long verificationBodyId) {
        verificationBodyDeletionService.deleteVerificationBodyById(verificationBodyId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    @ApiOperation(value = "Creates a verification body", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = VerificationBodyInfoDTO.class),
        @ApiResponse(code = 400, message = SwaggerApiInfo.CREATE_VERIFICATION_BODY_BAD_REQUEST ,response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<VerificationBodyInfoDTO> createVerificationBody(
        PmrvUser pmrvUser,
        @RequestBody @Valid @ApiParam(value = "The verification body creation dto", required = true)
            VerificationBodyCreationDTO verificationBodyCreationDTO) {
        VerificationBodyInfoDTO vbInfo =
            verificationBodyAndUserOrchestrator.createVerificationBody(pmrvUser, verificationBodyCreationDTO);
        return new ResponseEntity<>(vbInfo, HttpStatus.OK);
    }
    
    @PutMapping
    @ApiOperation(value = "Update the verification body", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK),
        @ApiResponse(code = 400, message = SwaggerApiInfo.UPDATE_VERIFICATION_BODY_BAD_REQUEST ,response = ErrorResponse.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Void> updateVerificationBody(
        @RequestBody @Valid @ApiParam(value = "The verification body dto to update", required = true)
            VerificationBodyUpdateDTO verificationBodyUpdateDTO) {
        verificationBodyUpdateService.updateVerificationBody(verificationBodyUpdateDTO);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping
    @ApiOperation(value = "Update the verification bodies status", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK),
            @ApiResponse(code = 400, message = SwaggerApiInfo.UPDATE_VERIFICATION_BODY_STATUS_BAD_REQUEST ,response = ErrorResponse.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Void> updateVerificationBodiesStatus(
            @RequestBody @Valid @ApiParam(value = "The verification bodies status dto to update", required = true)
                    List<VerificationBodyUpdateStatusDTO> verificationBodyUpdateStatusList) {
        verificationBodyUpdateService.updateVerificationBodiesStatus(verificationBodyUpdateStatusList);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
