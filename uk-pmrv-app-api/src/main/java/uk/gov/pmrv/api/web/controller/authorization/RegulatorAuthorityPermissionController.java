package uk.gov.pmrv.api.web.controller.authorization;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.AUTHORITY_USER_NOT_RELATED_TO_CA;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.authorization.regulator.domain.AuthorityManagePermissionDTO;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionGroup;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorPermissionLevel;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorAuthorityQueryService;
import uk.gov.pmrv.api.authorization.regulator.transform.RegulatorPermissionsAdapter;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

@RestController
@RequestMapping(path = "/v1.0/regulator-authorities/permissions")
@Api(tags = "Regulator Authorities")
@RequiredArgsConstructor
public class RegulatorAuthorityPermissionController {

    private final RegulatorAuthorityQueryService regulatorAuthorityQueryService;

    @GetMapping
    @ApiOperation(value = "Retrieves the current regulator user's permissions", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = AuthorityManagePermissionDTO.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = RoleType.REGULATOR)
    public ResponseEntity<AuthorityManagePermissionDTO> getCurrentRegulatorUserPermissionsByCa(PmrvUser currentUser) {
        return new ResponseEntity<>(regulatorAuthorityQueryService.getCurrentRegulatorUserPermissions(currentUser),
            HttpStatus.OK);
    }

    @GetMapping(path = "/{userId}")
    @ApiOperation(value = "Retrieves the regulator user's permissions", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = AuthorityManagePermissionDTO.class),
        @ApiResponse(code = 400, message = AUTHORITY_USER_NOT_RELATED_TO_CA, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<AuthorityManagePermissionDTO> getRegulatorUserPermissionsByCaAndId(
        PmrvUser pmrvUser,
        @PathVariable("userId") @ApiParam(value = "The regulator user id") String userId) {
        return new ResponseEntity<>(regulatorAuthorityQueryService.getRegulatorUserPermissionsByUserId(pmrvUser, userId),
            HttpStatus.OK);
    }


    @GetMapping(path = "/group-levels")
    @ApiOperation(value = "Retrieves the regulator permissions group levels", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = AuthorityManagePermissionDTO.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<Map<RegulatorPermissionGroup, List<RegulatorPermissionLevel>>> getRegulatorPermissionGroupLevels() {
        return new ResponseEntity<>(RegulatorPermissionsAdapter.getPermissionGroupLevels(), HttpStatus.OK);
    }
}
