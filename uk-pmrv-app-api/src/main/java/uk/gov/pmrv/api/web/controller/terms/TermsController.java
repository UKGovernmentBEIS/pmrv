package uk.gov.pmrv.api.web.controller.terms;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.NOT_FOUND;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import uk.gov.pmrv.api.web.constants.SwaggerApiInfo;
import uk.gov.pmrv.api.terms.domain.Terms;
import uk.gov.pmrv.api.terms.domain.dto.TermsDTO;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.terms.service.TermsService;
import uk.gov.pmrv.api.terms.transform.TermsMapper;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

/**
 * Controller for terms and conditions.
 */
@RestController
@RequestMapping(path = "/v1.0/terms")
@Api(tags = "Terms and conditions")
@RequiredArgsConstructor
public class TermsController {

    private final TermsService termsService;

    private final TermsMapper termsMapper;

    /**
     * Retrieves the latest version of terms and conditions
     */
    @GetMapping
    @ApiOperation(value = "Retrieves the latest version of terms and conditions", tags = SwaggerApiInfo.TAG_AUTHENTICATED)
    @ApiResponses({
        @ApiResponse(code = 200, message = OK, response = TermsDTO.class),
        @ApiResponse(code = 404, message = NOT_FOUND, response = ErrorResponse.class),
        @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<TermsDTO> getLatestTerms() {

        Terms latestTerms = termsService.getLatestTerms();

        if (latestTerms == null) {
            throw new BusinessException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        TermsDTO termsDTO = termsMapper.transformToTermsDTO(latestTerms);

        return new ResponseEntity<>(termsDTO, HttpStatus.OK);

    }

}
