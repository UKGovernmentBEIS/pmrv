package uk.gov.pmrv.api.account.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;

/**
 * The LegalEntity Repository.
 */
@Repository
public interface LegalEntityRepository extends JpaRepository<LegalEntity, Long> {

	@Override
    @EntityGraph(value = "fetchLocation", type = EntityGraph.EntityGraphType.FETCH)
    Optional<LegalEntity> findById(Long id);

    @EntityGraph(value = "fetchLocation", type = EntityGraph.EntityGraphType.FETCH)
    Optional<LegalEntity> findByNameAndStatus(String name, LegalEntityStatus status);
    
    @Transactional(readOnly = true)
    @Query(name =  LegalEntity.NAMED_QUERY_FIND_ACTIVE)
    List<LegalEntity> findActive();
    
    @Transactional(readOnly = true)
   	@Query(name =  LegalEntity.NAMED_QUERY_FIND_ACTIVE_LEGAL_ENTITIES_BY_ACCOUNTS)
   	List<LegalEntity> findActiveLegalEntitiesByAccounts(@Param("accountIds") Set<Long> accountIds);
    
    @Transactional(readOnly = true)
	@Query(name =  LegalEntity.NAMED_QUERY_EXISTS_LEGAL_ENTITY_IN_ANY_OF_ACCOUNTS)
	boolean existsLegalEntityInAnyOfAccounts(@Param("leId") Long leId, @Param("accountIds") Set<Long> accountIds);

    @Transactional(readOnly = true)
    @Query(name =  LegalEntity.NAMED_QUERY_EXISTS_ACTIVE_LEGAL_ENTITY_NAME_IN_ANY_OF_ACCOUNTS)
    boolean existsActiveLegalEntityNameInAnyOfAccounts(@Param("leName") String leName, @Param("accountIds") Set<Long> accountIds);

    @Transactional(readOnly = true)
    @Query(name =  LegalEntity.NAMED_QUERY_EXISTS_ACTIVE_LEGAL_ENTITY)
    boolean existsActiveLegalEntity(@Param("leName") String leName);

    @Transactional(readOnly = true)
    boolean existsByNameAndStatusAndIdNot(String name, LegalEntityStatus status, Long id);

}
