package uk.gov.pmrv.api.referencedata.transform;

import java.util.List;

import uk.gov.pmrv.api.referencedata.domain.dto.ReferenceDataDTO;
import uk.gov.pmrv.api.referencedata.domain.ReferenceData;

/**
 * Reference data mapper interface
 *
 * @param <E> the reference data entity
 * @param <D> the reference data DTO
 */
public interface ReferenceDataMapper<E extends ReferenceData, D extends ReferenceDataDTO> {
	
	/**
	 * Converts the given entity to the respective DTO object
	 * @param entity
	 * @return
	 */
	D toDTO(E entity);

	/**
	 * Converts the given list of entities to the list of dto objects
	 * @param entities
	 * @return
	 */
    List<D> toDTOs(List<E> entities);
}
