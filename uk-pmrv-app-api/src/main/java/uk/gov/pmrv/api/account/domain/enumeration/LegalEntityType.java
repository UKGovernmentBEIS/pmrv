package uk.gov.pmrv.api.account.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Enumerates the Legal Entity type.
 */
@Getter
@AllArgsConstructor
public enum LegalEntityType {

    LIMITED_COMPANY("Limited Company"),
    PARTNERSHIP("Partnership"),
    SOLE_TRADER("Sole Trader"),
    OTHER("None of the above");

    /** The name */
    private final String name;
}
