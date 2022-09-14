package uk.gov.pmrv.api.account.repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountContactVbInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

/**
 * The Account Repository.
 */
@Repository
public interface AccountRepository extends JpaRepository<Account, Long>, AccountCustomRepository {

    @EntityGraph(value = "location-and-le-with-location-graph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Account> findByIdAndStatus(Long id, AccountStatus status);

    Optional<Account> findByIdAndStatusNotIn(Long id, List<AccountStatus> statuses);

    @EntityGraph(value = "location-and-le-with-location-graph", type = EntityGraph.EntityGraphType.LOAD)
    Optional<Account> findAccountById(Long id);

    @Transactional(readOnly = true)
    List<Account> findAllByStatusIs(AccountStatus status);

    @Transactional(readOnly = true)
    List<Account> findAllByIdIn(List<Long> ids);

    @Transactional(readOnly = true)
    @EntityGraph(value = "location-and-le-with-location-graph", type = EntityGraph.EntityGraphType.LOAD)
    List<Account> findAllEagerLeByIdIn(List<Long> ids);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_BY_ACCOUNT_IDS_AND_COMP_AUTH)
    @EntityGraph(value = "location-and-le-with-location-graph", type = EntityGraph.EntityGraphType.LOAD)
    List<Account> findByAccountIdsAndCompAuth(@Param("accountIds") List<Long> accountIds,
                                              @Param("compAuth") CompetentAuthority compAuth);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_BY_ACCOUNT_IDS_AND_VER_BODY)
    @EntityGraph(value = "location-and-le-with-location-graph", type = EntityGraph.EntityGraphType.LOAD)
    List<Account> findByAccountIdsAndVerBody(@Param("accountIds") List<Long> accountIds,
                                             @Param("verBodyId") Long verificationBodyId);

    @Transactional(readOnly = true)
    long countByLegalEntityId(Long legalEntityId);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_CA_AND_CONTACT_TYPE)
    Page<AccountContactInfoDTO> findApprovedAccountContactsByCaAndContactType(
        Pageable pageable, CompetentAuthority ca, AccountContactType contactType);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_VB_AND_CONTACT_TYPE)
    Page<AccountContactVbInfoDTO> findAccountContactsByVbAndContactType(
        Pageable pageable, Long vbId, AccountContactType contactType);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_ACCOUNT_CONTACTS_BY_ACCOUNT_IDS_AND_CONTACT_TYPE)
    List<AccountContactInfoDTO> findAccountContactsByAccountIdsAndContactType(
        List<Long> accountIds, AccountContactType contactType);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_ACCOUNTS_BY_CONTACT_TYPE_AND_USER_ID)
    List<Account> findAccountsByContactTypeAndUserId(
        AccountContactType contactType, String userId);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_APPROVED_IDS_BY_CA)
    List<Long> findAllApprovedIdsByCA(CompetentAuthority ca);

    @Transactional(readOnly = true)
    @Query(name = Account.NAMED_QUERY_FIND_IDS_BY_VB)
    List<Long> findAllIdsByVB(Long vbId);

    @Transactional(readOnly = true)
    @EntityGraph(value = "contacts-graph", type = EntityGraph.EntityGraphType.LOAD)
    @Query(name = Account.NAMED_QUERY_FIND_ACCOUNTS_BY_VB_IN_LIST)
    Set<Account> findAllByVerificationBodyIn(Set<Long> vbIds);

    @Transactional(readOnly = true)
    @EntityGraph(value = "contacts-graph", type = EntityGraph.EntityGraphType.LOAD)
    @Query(name = Account.NAMED_QUERY_FIND_ACCOUNTS_BY_VB_AND_EMISSION_TRADING_SCHEME_IN_LIST)
    Set<Account> findAllByVerificationBodyAndEmissionTradingSchemeIn(Long vbId, Set<EmissionTradingScheme> emissionTradingSchemes);

    @Transactional(readOnly = true)
    Optional<Account> findByMigratedAccountId(String migratedAccountId);

    @Transactional(readOnly = true)
    @Query("SELECT a FROM Account a LEFT JOIN FETCH a.contacts WHERE a.migratedAccountId = :migratedAccountId")
    Account findByMigratedAccount(@Param("migratedAccountId") String migratedAccountId);

    @Transactional(readOnly = true)
    @EntityGraph(value = "location-and-le-with-location-graph", type = EntityGraph.EntityGraphType.LOAD)
    List<Account> findByMigratedAccountIdIsNotNull();

    /**
     * Retrieves account using a Pessimistic write lock.
     */
    @Query(name = Account.NAMED_QUERY_FIND_BY_ID_FOR_UPDATE)
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Account> findByIdForUpdate(final Long id);

    @Transactional(readOnly = true)
    List<Account> findAllByCompetentAuthority(CompetentAuthority competentAuthority);
}
