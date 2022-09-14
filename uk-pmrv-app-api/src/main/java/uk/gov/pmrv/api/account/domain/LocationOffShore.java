package uk.gov.pmrv.api.account.domain;

import lombok.*;

import uk.gov.pmrv.api.account.domain.enumeration.LocationType;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;

/**
 * The Location OffShore Entity.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Entity
@DiscriminatorValue(value = LocationType.Values.OFFSHORE)
public class LocationOffShore extends Location {

    /** The latitude. */
    @Column(name = "latitude")
    @NotBlank
    private String latitude;

    /** The longitude. */
    @Column(name = "longitude")
    @NotBlank
    private String longitude;
}
