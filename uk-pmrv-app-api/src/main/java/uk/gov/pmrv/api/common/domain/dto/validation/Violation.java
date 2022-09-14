package uk.gov.pmrv.api.common.domain.dto.validation;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * A violation.
 */
@Getter
@Setter
@AllArgsConstructor
public class Violation {

    /** The field name */
    private  String fieldName;

    /** The violation message */
    private  String message;
}
