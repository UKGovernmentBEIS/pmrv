package uk.gov.pmrv.api.account.domain;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;

/**
 * The Location OnShore Entity.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = false)
@Entity
@DiscriminatorValue(value = LocationType.Values.ONSHORE)
public class LocationOnShore extends Location {

    /** The UK Ordnance survey grid reference. */
    @Column(name = "grid_reference")
    private String gridReference;

    @Embedded
    @Valid
    private Address address;
}
