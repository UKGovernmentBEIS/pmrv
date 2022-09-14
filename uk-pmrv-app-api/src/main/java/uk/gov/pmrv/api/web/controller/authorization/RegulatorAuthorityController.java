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
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorUserUpdateStatusDTO;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorAuthorityDeletionService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.orchestrator.RegulatorUserAuthorityQueryOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.RegulatorUserAuthorityUpdateOrchestrator;
import uk.gov.pmrv.api.web.orchestrator.dto.RegulatorUsersAuthoritiesInfoDTO;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/v1.0/regulator-authorities")
@Api(tags = "Regulator Authorities")
@RequiredArgsConstructor
public class RegulatorAuthorityController {

    private final RegulatorAuthorityDeletionService regulatorAuthorityDeletionService;
    private final RegulatorUserAuthorityQueryOrchestrator regulatorUserAuthorityQueryOrchestrator;
    private final RegulatorUserAuthorityUpdateOrchestrator regulatorUserAuthorityUpdateOrchestrator;
    
    @PostMapping
    @ApiOperation(value = "Updates regulator users status", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Void> updateCompetentAuthorityRegulatorUsersStatus(
        PmrvUser pmrvUser,
        @RequestBody @Valid @NotEmpty @ApiParam(value = "The regulator users to update", required = true)
            List<RegulatorUserUpdateStatusDTO> regulatorUsers) {
        regulatorUserAuthorityUpdateOrchestrator.updateRegulatorUsersStatus(regulatorUsers, pmrvUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping(path = "/{userId}")
    @ApiOperation(value = "Delete the regulator user that corresponds to the provided user id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Void> deleteRegulatorUserByCompetentAuthority(
        PmrvUser pmrvUser,
        @PathVariable("userId") @ApiParam(value = "The regulator to be deleted") String userId) {
        regulatorAuthorityDeletionService.deleteRegulatorUser(userId, pmrvUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @DeleteMapping
    @ApiOperation(value = "Delete the current regulator user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = SwaggerApiInfo.NO_CONTENT),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Void> deleteCurrentRegulatorUserByCompetentAuthority(PmrvUser currentUser) {
        regulatorAuthorityDeletionService.deleteCurrentRegulatorUser(currentUser);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @GetMapping
    @ApiOperation(value = "Retrieves the users of type REGULATOR", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = RegulatorUsersAuthoritiesInfoDTO.class),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = RoleType.REGULATOR)
    public ResponseEntity<RegulatorUsersAuthoritiesInfoDTO> getCaRegulators(PmrvUser pmrvUser) {

        return new ResponseEntity<>(regulatorUserAuthorityQueryOrchestrator.getCaUsersAuthoritiesInfo(pmrvUser),
            HttpStatus.OK);
    }
}
