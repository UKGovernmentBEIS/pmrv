package uk.gov.pmrv.api.account.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Account statuses.
 *
 */
@Getter
@AllArgsConstructor
public enum AccountStatus {

    UNAPPROVED,
    DENIED,
    NEW,
    LIVE,
    DEEMED_WITHDRAWN,
    PERMIT_REFUSED,
    AWAITING_SURRENDER,
    SURRENDERED,
    AWAITING_REVOCATION,
    REVOKED,
    AWAITING_TRANSFER,
    TRANSFERRED,
    RATIONALISED,
    AWAITING_RATIONALISATION,

    // Exclusive Aviation account statuses
    CEASED_OPERATIONS,
    COMMISSION_LIST,
    EXEMPT,
    EXEMPT_COMMERCIAL,
    EXEMPT_NON_COMMERCIAL,
    PRIOR_COMPLIANCE_LIST,
    REMOVED_FROM_COMMISSION_LIST
}
