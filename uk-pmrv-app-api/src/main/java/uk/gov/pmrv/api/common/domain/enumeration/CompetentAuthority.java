package uk.gov.pmrv.api.common.domain.enumeration;

import java.io.File;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Competent authorities.
 */
@Getter
@AllArgsConstructor
public enum CompetentAuthority {

    ENGLAND("EA", 
            "E", 
            "Environment Agency", 
            "ethelp@environment-agency.gov.uk", 
            "01234 567 890"),
    NORTHERN_IRELAND("NIEA", 
            "N", 
            "Northern Ireland Environment Agency", 
            "emissions.trading@daera-ni.gov.uk", 
            "01234 567 890"),
    OPRED("DECC",
            "D", 
            "Offshore Petroleum Regulator for Environment & Decommissioning", 
            "emt@beis.gov.uk", 
            "01234 567 890"),
    SCOTLAND("SEPA", 
            "S",
            "Scottish Environment Protection Agency", 
            "emission.trading@sepa.org.uk", 
            "01234 567 890"),
    WALES("NRW",
            "W", 
            "Natural Resources Wales", 
            "GHGHelp@naturalresourceswales.gov.uk", 
            "01234 567 890"),
    ;

    private final String code;
    private final String oneLetterCode;
    private final String name;
    private final String email;
    private final String phone;
    
    public String getLogoPath() {
        return this.name().toLowerCase() + File.separator + "logo.jpg";
    }
    
}
