package uk.gov.pmrv.api.account.domain.dto;

import javax.validation.Valid;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import uk.gov.pmrv.api.account.domain.dto.validation.LegalEntity;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;

/**
 * The Legal Entity DTO for Account.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@LegalEntity
public class LegalEntityDTO {

    /** The id. */
    private Long id;

    /** The {@link LegalEntityType}. */
    private LegalEntityType type;

    /** The Legal Entity name. */
    private String name;

    /** The companies house reference number. */
    private String referenceNumber;

    /** The explanation of why organisation does not have a companies house reference number. */
    private String noReferenceNumberReason;

    /** The {@link AddressDTO}. */
    @Valid
    private AddressDTO address;
}
