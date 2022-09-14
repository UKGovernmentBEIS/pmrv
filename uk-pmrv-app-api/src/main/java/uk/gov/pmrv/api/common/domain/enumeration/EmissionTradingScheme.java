package uk.gov.pmrv.api.common.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum EmissionTradingScheme {

    UK_ETS_INSTALLATIONS("UK ETS Installations"),
    EU_ETS_INSTALLATIONS("EU ETS Installations"),
    UK_ETS_AVIATION("UK ETS Aviation"),
    CORSIA("CORSIA");

    /** The description */
    private final String description;
    
}
