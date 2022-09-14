package uk.gov.pmrv.api.web.controller.account;

import static uk.gov.pmrv.api.common.domain.enumeration.RoleType.REGULATOR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.BAD_REQUEST;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.FORBIDDEN;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NO_CONTENT;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactDTO;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactRegistrationDTO;
import uk.gov.pmrv.api.account.domain.dto.CaExternalContactsDTO;
import uk.gov.pmrv.api.account.service.CaExternalContactService;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;
import uk.gov.pmrv.api.web.security.Authorized;
import uk.gov.pmrv.api.web.security.AuthorizedRole;

@RestController
@RequestMapping(path = "/v1.0/ca-external-contacts")
@RequiredArgsConstructor
@Api(tags = "Ca external contacts")
public class CaExternalContactController {

    private final CaExternalContactService caExternalContactService;

    @GetMapping
    @AuthorizedRole(roleType = REGULATOR)
    @ApiOperation(value = "Retrieves the current regulator external contacts", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = CaExternalContactsDTO.class, responseContainer = "List"),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<CaExternalContactsDTO> getCaExternalContacts(PmrvUser pmrvUser) {
        return new ResponseEntity<>(caExternalContactService.getCaExternalContacts(pmrvUser), HttpStatus.OK);
    }

    @GetMapping(path = "/{id}")
    @Authorized
    @ApiOperation(value = "Returns the ca external contact with specified id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK),
        @ApiResponse(code = 400, message = BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<CaExternalContactDTO> getCaExternalContactById(PmrvUser authUser, @PathVariable("id")
    @ApiParam(value = "The ca external contact id") Long id) {
        return new ResponseEntity<>(caExternalContactService.getCaExternalContactById(authUser, id), HttpStatus.OK);
    }

    @DeleteMapping(path = "/{id}")
    @Authorized
    @ApiOperation(value = "Deletes the ca external contact with specified id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = NO_CONTENT),
        @ApiResponse(code = 400, message = BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Void> deleteCaExternalContactById(PmrvUser authUser, @PathVariable("id")
    @ApiParam(value = "The ca external contact id") Long id) {
        caExternalContactService.deleteCaExternalContactById(authUser, id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PostMapping
    @Authorized
    @ApiOperation(value = "Creates a new ca external contact", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = NO_CONTENT),
        @ApiResponse(code = 400, message = BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Void> createCaExternalContact(PmrvUser authUser,
                                                        @RequestBody @Valid
                                                        @ApiParam(value = "The ca external information", required = true)
                                                            CaExternalContactRegistrationDTO caExternalContactRegistration) {
        caExternalContactService.createCaExternalContact(authUser, caExternalContactRegistration);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    @PatchMapping(path = "/{id}")
    @Authorized
    @ApiOperation(value = "Edits the ca external contact with specified id", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 204, message = NO_CONTENT),
        @ApiResponse(code = 400, message = BAD_REQUEST, response = ErrorResponse.class),
        @ApiResponse(code = 403, message = FORBIDDEN, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Void> editCaExternalContact(PmrvUser authUser,
                                                      @PathVariable("id")
                                                      @ApiParam(value = "The ca external contact id") Long id,
                                                      @RequestBody @Valid
                                                      @ApiParam(value = "The ca external information", required = true)
                                                          CaExternalContactRegistrationDTO caExternalContactRegistration) {
        caExternalContactService.editCaExternalContact(authUser, id, caExternalContactRegistration);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
