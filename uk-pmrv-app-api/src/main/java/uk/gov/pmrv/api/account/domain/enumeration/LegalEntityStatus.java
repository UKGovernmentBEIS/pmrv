package uk.gov.pmrv.api.account.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Legal entity statuses.
 *
 */
@Getter
@AllArgsConstructor
public enum LegalEntityStatus {
	PENDING,
	ACTIVE,
	DENIED
}
