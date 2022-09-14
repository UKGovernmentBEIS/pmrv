package uk.gov.pmrv.api.account.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Account types.
 */
@Getter
@AllArgsConstructor
public enum AccountType {

    INSTALLATION("Installation", "IN"),
    AVIATION("Aviation", "AV");

    /**
     * The name
     */
    private final String name;
    /**
     * The code used to generate the permit id.
     */
    private final String code;
}
