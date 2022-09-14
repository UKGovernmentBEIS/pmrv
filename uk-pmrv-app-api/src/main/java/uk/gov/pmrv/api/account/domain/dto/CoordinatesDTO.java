package uk.gov.pmrv.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.constants.LocationConstants;
import uk.gov.pmrv.api.account.domain.dto.validation.MaxSeconds;
import uk.gov.pmrv.api.account.domain.enumeration.CardinalDirection;

import java.util.Locale;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * The Geographical Coordinates as DMS.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@MaxSeconds(seconds = 3600, message = "{coordinates.degree.typeMismatch}")
public class CoordinatesDTO {

    /**
     * The degree.
     */
    @NotNull(message = "{coordinates.degree.notEmpty}")
    @Min(value = 0, message = "{coordinates.degree.typeMismatch}")
    private Integer degree;

    /**
     * The minutes.
     */
    @NotNull(message = "{coordinates.minute.notEmpty}")
    @Min(value = 0, message = "{coordinates.minute.typeMismatch}")
    @Max(value = 60, message = "{coordinates.minute.typeMismatch}")
    private Integer minute;

    /**
     * The seconds.
     */
    @NotNull(message = "{coordinates.second.notEmpty}")
    @Min(value = 0, message = "{coordinates.second.typeMismatch}")
    @Max(value = 60, message = "{coordinates.second.typeMismatch}")
    private Double second;

    /**
     * The {@link CardinalDirection}.
     */
    @NotNull(message = "{coordinates.cardinalDirection.notEmpty}")
    private CardinalDirection cardinalDirection;
    
    @Override
    public String toString() {
        return String.format(Locale.ROOT, LocationConstants.COORDINATES_FORMAT, degree, minute, second, cardinalDirection.getName().charAt(0));
    }
    
}
