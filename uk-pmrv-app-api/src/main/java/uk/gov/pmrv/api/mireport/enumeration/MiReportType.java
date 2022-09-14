package uk.gov.pmrv.api.mireport.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Competent authorities.
 */
@Getter
@AllArgsConstructor
public enum MiReportType {

    LIST_OF_ACCOUNTS_USERS_CONTACTS("List of Accounts, Users and Contacts"),
    LIST_OF_ACCOUNTS_REGULATORS("List of Accounts and assigned Regulator site contacts"),
    LIST_OF_VERIFICATION_BODIES_AND_USERS("List of Verification bodies and Users"),
    LIST_OF_ACCOUNTS("List of Accounts"),
    COMPLETED_WORK("Completed work");

    private final String description;

    public String getName() {
        return this.name();
    }
}
