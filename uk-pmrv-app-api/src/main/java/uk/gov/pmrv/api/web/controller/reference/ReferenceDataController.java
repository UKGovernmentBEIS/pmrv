package uk.gov.pmrv.api.web.controller.reference;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import uk.gov.pmrv.api.referencedata.domain.dto.ReferenceDataDTO;
import uk.gov.pmrv.api.referencedata.domain.enumeration.ReferenceDataType;
import uk.gov.pmrv.api.referencedata.service.ReferenceDataService;
import uk.gov.pmrv.api.referencedata.service.ReferenceDataTypeServiceEnum;
import uk.gov.pmrv.api.web.controller.exception.ErrorResponse;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.INTERNAL_SERVER_ERROR;
import static uk.gov.pmrv.api.web.constants.SwaggerApiInfo.OK;

/**
 * Controller for reference data that are needed by the UI.
 */
@RestController
@RequestMapping(path = "/v1.0/data")
@Api(tags = "Reference Data")
@RequiredArgsConstructor
public class ReferenceDataController {
	
	private final ApplicationContext context;

    /**
     * Returns the reference data by type.
     *
     * @param types List of {@link ReferenceDataType}.
     * @return The map of reference data
     */
    @GetMapping
    @ApiOperation(value = "Retrieves reference data by type")
    @ApiResponses({
            @ApiResponse(code = 200, message = OK, response = Map.class),
            @ApiResponse(code = 500, message = INTERNAL_SERVER_ERROR, response = ErrorResponse.class)
    })
    public ResponseEntity<Map<ReferenceDataType, List<ReferenceDataDTO>>> getReferenceData(@ApiParam(value = "The reference data types.")
            @RequestParam List<ReferenceDataType> types) {

        Map<ReferenceDataType, List<ReferenceDataDTO>> referenceDataTypeListMap = new EnumMap<>(ReferenceDataType.class);

        types.forEach(referenceDataType -> {
        	ReferenceDataTypeServiceEnum typeService = ReferenceDataTypeServiceEnum.resolve(referenceDataType);
        	if(typeService == null) {
        		return;
        	}
        	ReferenceDataService referenceDataService = context.getBean(typeService.getReferenceDataService());
        	referenceDataTypeListMap.put(referenceDataType, typeService.getReferenceDataMapper().toDTOs(referenceDataService.getReferenceData()));
        });

        return new ResponseEntity<>(referenceDataTypeListMap, HttpStatus.OK);
    }
}
