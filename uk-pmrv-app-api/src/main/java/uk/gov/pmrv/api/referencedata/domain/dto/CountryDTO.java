package uk.gov.pmrv.api.referencedata.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The country DTO.
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CountryDTO implements ReferenceDataDTO {

    /** The country code. */
    private String code;

    /** The country name. */
    private String name;

    /** The country official name. */
    private String officialName;
}
