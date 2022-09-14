package uk.gov.pmrv.api.referencedata.service;

import java.util.List;

import uk.gov.pmrv.api.referencedata.domain.ReferenceData;

/**
 * The Reference Data Service.
 */
public interface ReferenceDataService<T extends ReferenceData> {
	/**
	 * Get reference data
	 * @return the list of the reference data
	 */
    List<T> getReferenceData();
    
}
