package uk.gov.pmrv.api.account.domain.dto;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import uk.gov.pmrv.api.account.domain.dto.validation.Latitude;
import uk.gov.pmrv.api.account.domain.dto.validation.Longitude;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

/**
 * The Location OffShore DTO type of address.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class LocationOffShoreDTO extends LocationDTO {

    /** The latitude {@link CoordinatesDTO}. */
    @NotNull(message = "{locationOffShoreDTO.latitude.notEmpty}")
    @Latitude
    @Valid
    private CoordinatesDTO latitude;

    /** The longitude {@link CoordinatesDTO}. */
    @NotNull(message = "{locationOffShoreDTO.longitude.notEmpty}")
    @Longitude
    @Valid
    private CoordinatesDTO longitude;

    @Override
    public String toString() {
        return latitude.toString() + " / " + longitude.toString();
    }
    
}
