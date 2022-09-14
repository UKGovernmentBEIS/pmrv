package uk.gov.pmrv.api.web.controller.account;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityDTO;
import uk.gov.pmrv.api.account.domain.dto.LegalEntityInfoDTO;
import uk.gov.pmrv.api.account.service.LegalEntityService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

import java.util.List;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.OPERATOR;
import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.ACCOUNT_LEGAL_ENTITY_BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

/**
 * Controller for legal entities.
 */
@RestController
@RequestMapping(path = "/v1.0/legal-entities")
@Api(tags = "Legal Entities")
@RequiredArgsConstructor
public class LegalEntityController {

    private final LegalEntityService legalEntityService;

    /**
     * Returns all legal entities available for the current user.
     *
     * @return List of {@link LegalEntityDTO}
     */
    @GetMapping
    @ApiOperation(value = "Retrieves all legal entities associated with the user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = LegalEntityDTO.class, responseContainer = "List"),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<List<LegalEntityInfoDTO>> getCurrentUserLegalEntities(PmrvUser pmrvUser) {
        return new ResponseEntity<>(legalEntityService.getUserLegalEntities(pmrvUser),
                HttpStatus.OK);
    }

    /**
     * Returns legal entity with specified id that is associated with the user.
     *
     * @return List of {@link LegalEntityDTO}
     */
    @GetMapping("/{id}")
    @ApiOperation(value = "Returns legal entity with the specified id that is associated with the user", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = LegalEntityDTO.class),
        @ApiResponse(code = 400, message = ACCOUNT_LEGAL_ENTITY_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    @AuthorizedRole(roleType = {OPERATOR, REGULATOR})
    public ResponseEntity<LegalEntityDTO> getLegalEntityById(PmrvUser pmrvUser,
                                                             @ApiParam(value = "The legal entity id") @PathVariable("id") Long id) {
        return new ResponseEntity<>(legalEntityService.getUserLegalEntityDTOById(id, pmrvUser),
                HttpStatus.OK);
    }
    
    @GetMapping("/name")
    @ApiOperation(value = "Checks if legal entity name exists", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK),
        @ApiResponse(code = 400, message = ACCOUNT_LEGAL_ENTITY_BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Boolean> isExistingLegalEntityName(PmrvUser pmrvUser,
                                                             @ApiParam(value = "The legal entity name to check") @RequestParam("name") String legalEntityName) {
        boolean exists = legalEntityService.isExistingActiveLegalEntityName(legalEntityName, pmrvUser);
        return new ResponseEntity<>(exists, HttpStatus.OK);
    }
}
