package uk.gov.pmrv.api.web.controller.authorization;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.authorization.core.domain.dto.RoleDTO;
import uk.gov.pmrv.api.authorization.core.service.RoleService;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorRolePermissionsDTO;
import uk.gov.pmrv.api.authorization.regulator.service.RegulatorRoleService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;

@RestController
@RequestMapping(path = "/v1.0/authorities")
@Api(tags = "Authorities")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;
    private final RegulatorRoleService regulatorRoleService;

    /**
     * Returns all operator roles.
     *
     * @param user {@link PmrvUser}
     * @return List of {@link RoleDTO}
     */
    @GetMapping(path = "/account/{accountId}/operator-role-codes")
    @ApiOperation(value = "Retrieves the operator roles", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = RoleDTO.class, responseContainer = "List"),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<List<RoleDTO>> getOperatorRoleCodes(
        @PathVariable("accountId") @ApiParam(value = "The account id") Long accountId) {
        List<RoleDTO> roles = roleService.getOperatorRoles();
        return new ResponseEntity<>(roles, HttpStatus.OK);
    }

    /**
     * Returns all regulator roles.
     * @return List of {@link RegulatorRolePermissionsDTO}
     */
    @GetMapping(path = "/regulator-roles")
    @ApiOperation(value = "Returns all regulator roles", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = RegulatorRolePermissionsDTO.class, responseContainer = "List"),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<List<RegulatorRolePermissionsDTO>> getRegulatorRoles() {
        return new ResponseEntity<>(regulatorRoleService.getRegulatorRoles(), HttpStatus.OK);
    }

    @GetMapping(path = "/verifier-role-codes")
    @ApiOperation(value = "Retrieves the verifier role codes", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = RoleDTO.class, responseContainer = "List"),
        @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized
    public ResponseEntity<List<RoleDTO>> getVerifierRoleCodes() {
        return new ResponseEntity<>(roleService.getVerifierRoleCodes(), HttpStatus.OK);
    }

}
