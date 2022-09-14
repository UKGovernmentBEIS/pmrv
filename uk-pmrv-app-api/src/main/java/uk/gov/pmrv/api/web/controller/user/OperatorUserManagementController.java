package uk.gov.pmrv.api.web.controller.user;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserDTO;
import uk.gov.pmrv.api.user.operator.service.OperatorUserManagementService;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

@RestController
@RequestMapping(path = "/v1.0/operator-users")
@Api(tags = "Operator Users")
@RequiredArgsConstructor
public class OperatorUserManagementController {

    private final OperatorUserManagementService operatorUserManagementService;

    /**
     * Retrieves info of user by account and user id.
     *
     * @param accountId Account id
     * @param userId Keycloak user id
     * @return {@link OperatorUserDTO}
     */
    @GetMapping(path = "/account/{accountId}/{userId}")
    @ApiOperation(value = "Retrieves info of operator user by account and user id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = OperatorUserDTO.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<OperatorUserDTO> getOperatorUserById(
            @PathVariable("accountId") @ApiParam(value = "The account id") Long accountId,
            @PathVariable("userId") @ApiParam(value = "The operator user id") String userId) {
        return new ResponseEntity<>(operatorUserManagementService.getOperatorUserByAccountAndId(accountId, userId),
                HttpStatus.OK);
    }

    /**
     * Updates logged in operator user.
     *
     * @param pmrvUser {@link PmrvUser}
     * @param operatorUserDTO {@link OperatorUserDTO}
     * @return {@link OperatorUserDTO}
     */
    @PatchMapping(path = "/operator")
    @ApiOperation(value = "Updates logged in operator user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = OperatorUserDTO.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = OPERATOR)
    public ResponseEntity<OperatorUserDTO> updateCurrentOperatorUser(
            PmrvUser pmrvUser,
            @RequestBody @Valid @ApiParam(value = "The modified operator user", required = true) OperatorUserDTO operatorUserDTO) {
        operatorUserManagementService.updateOperatorUser(pmrvUser, operatorUserDTO);
        return new ResponseEntity<>(operatorUserDTO, HttpStatus.OK);
    }

    /**
     * Updates operator user by account and user id.
     *
     * @param accountId Account id
     * @param userId Keycloak user id
     * @param operatorUserDTO {@link OperatorUserDTO}
     * @return {@link OperatorUserDTO}
     */
    @PatchMapping(path = "/account/{accountId}/{userId}")
    @ApiOperation(value = "Updates operator user by account and user id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
            @ApiResponse(code = 200, message = SwaggerApiInfo.OK, response = OperatorUserDTO.class),
            @ApiResponse(code = 403, message = SwaggerApiInfo.FORBIDDEN, response = ErrorResponse.class),
            @ApiResponse(code = 404, message = SwaggerApiInfo.NOT_FOUND, response = ErrorResponse.class),
            @ApiResponse(code = 500, message = SwaggerApiInfo.INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @Authorized(resourceId = "#accountId")
    public ResponseEntity<OperatorUserDTO> updateOperatorUserById(
            @PathVariable("accountId") @ApiParam(value = "The account id") Long accountId,
            @PathVariable("userId") @ApiParam(value = "The operator user id") String userId,
            @RequestBody @Valid @ApiParam(value = "The modified operator user", required = true) OperatorUserDTO operatorUserDTO) {
        operatorUserManagementService.updateOperatorUserByAccountAndId(accountId, userId, operatorUserDTO);
        return new ResponseEntity<>(operatorUserDTO, HttpStatus.OK);
    }
}
