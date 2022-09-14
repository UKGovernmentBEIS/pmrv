package uk.gov.pmrv.api.account.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import javax.persistence.EntityManager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.AccountInfoDTO;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchCriteria;
import uk.gov.pmrv.api.account.domain.dto.AccountSearchResults;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.repository.impl.AccountCustomRepositoryImpl;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class AccountCustomRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private AccountCustomRepositoryImpl repo;
    
    @Autowired
    private EntityManager entityManager;
    
    @Test
    void findByAccountIds_by_account_ids() {
        Account account1 = createAccount(1L, "account1", "leName1", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthority.WALES, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account3 = createAccount(3L, "account3", "leName3", CompetentAuthority.ENGLAND, AccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .page(0L).pageSize(10L).build();
        List<Long> accountIds = List.of(account1.getId(), account3.getId());
        
        AccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(2);
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getId).containsOnly(account1.getId(), account3.getId());
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getEmitterId).containsOnly(account1.getEmitterId(), account3.getEmitterId());
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getLegalEntityName).containsOnly(account1.getLegalEntity().getName(), account3.getLegalEntity().getName());
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getStatus).containsOnly(account1.getStatus(), account3.getStatus());
    }
    
    @Test
    void findByAccountIds_by_account_ids_with_term() {
        Account account1 = createAccount(1L, "account1", "leName1", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthority.WALES, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account3 = createAccount(3L, "account3", "leName3", CompetentAuthority.ENGLAND, AccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);
        createAccount(4L, "account4", "leName4", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("leName")
                .page(0L).pageSize(10L).build();
        List<Long> accountIds = List.of(account1.getId(), account3.getId());
        
        AccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(2);
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getId).containsOnly(account1.getId(), account3.getId());
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getEmitterId).containsOnly(account1.getEmitterId(), account3.getEmitterId());
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getLegalEntityName).containsOnly(account1.getLegalEntity().getName(), account3.getLegalEntity().getName());
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getStatus).containsOnly(account1.getStatus(), account3.getStatus());
    }
    
    @Test
    void findByAccountIds_by_account_ids_search_key_filter() {
        Account account1 = createAccount(1L, "account1", "leName1", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthority.WALES, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account3 = createAccount(3L, "account3", "leName3", CompetentAuthority.ENGLAND, AccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("leName1")
                .type(AccountType.INSTALLATION)
                .page(0L).pageSize(10L).build();
        List<Long> accountIds = List.of(account1.getId(), account3.getId());
        
        AccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(1);
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getId).containsOnly(account1.getId());
    }
    
    @Test
    void findByAccountIds_by_account_ids_search_key_not_present() {
        Account account1 = createAccount(1L, "account1", "leName1", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthority.WALES, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account3 = createAccount(3L, "account3", "leName3", CompetentAuthority.ENGLAND, AccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("notpresentkey")
                .page(0L).pageSize(10L).build();
        List<Long> accountIds = List.of(account1.getId(), account3.getId());
        
        AccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isZero();
    }
    
    @Test
    void findByAccountIds_by_account_ids_account_type_not_present() {
        Account account1 = createAccount(1L, "account1", "leName1", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthority.WALES, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account3 = createAccount(3L, "account3", "leName3", CompetentAuthority.ENGLAND, AccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .type(AccountType.AVIATION)
                .page(0L).pageSize(10L).build();
        List<Long> accountIds = List.of(account1.getId(), account3.getId());
        
        AccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isZero();
    }
    
    @Test
    void findByAccountIds_paging() {
        int totalAccounts = 35;
        long pageSize = 10;
        long page = 1;
        for(int i = 1; i <= totalAccounts; i++) {
            createAccount((long) i, "account" + i, "leName" + i, CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        }

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("account")
                .page(page).pageSize(pageSize).build();
        List<Long> accountIds = LongStream.rangeClosed(1L, totalAccounts).boxed().collect(Collectors.toList());
        
        List<Long> expectedAccountIds = LongStream.rangeClosed(11L, 20L).boxed().collect(Collectors.toList());
        
        AccountSearchResults results = repo.findByAccountIds(accountIds, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(totalAccounts);
        assertThat(results.getAccounts().size()).isEqualTo(10);
        assertTrue(results.getAccounts().stream().map(AccountInfoDTO::getId).allMatch(expectedAccountIds::contains));
    }
    
    @Test
    void findByCompAuth() {
        Account account1 = createAccount(1L, "account1", "leName1", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthority.WALES, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account3 = createAccount(3L, "account3", "leName3", CompetentAuthority.ENGLAND, AccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .type(AccountType.INSTALLATION)
                .page(0L).pageSize(10L).build();
        
        AccountSearchResults results = repo.findByCompAuth(CompetentAuthority.ENGLAND, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(2);
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getId).containsOnly(account1.getId(), account3.getId());
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getEmitterId).containsOnly(account1.getEmitterId(), account3.getEmitterId());
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getLegalEntityName).containsOnly(account1.getLegalEntity().getName(), account3.getLegalEntity().getName());
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getStatus).containsOnly(account1.getStatus(), account3.getStatus());
    }
    
    @Test
    void findByCompAuth_with_term() {
        Account account1 = createAccount(1L, "account1", "leName1", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthority.WALES, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account3 = createAccount(3L, "account3", "leName3", CompetentAuthority.ENGLAND, AccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);

        flushAndClear();
        
        final AccountSearchCriteria searchCriteria = AccountSearchCriteria.builder()
                .term("leName")
                .type(AccountType.INSTALLATION)
                .page(0L).pageSize(10L).build();
        
        AccountSearchResults results = repo.findByCompAuth(CompetentAuthority.ENGLAND, searchCriteria);
        assertThat(results.getTotal()).isEqualTo(2);
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getId).containsOnly(account1.getId(), account3.getId());
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getEmitterId).containsOnly(account1.getEmitterId(), account3.getEmitterId());
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getLegalEntityName).containsOnly(account1.getLegalEntity().getName(), account3.getLegalEntity().getName());
        assertThat(results.getAccounts()).extracting(AccountInfoDTO::getStatus).containsOnly(account1.getStatus(), account3.getStatus());
    }
    
    private Account createAccount(Long id, String accountName, String leName, CompetentAuthority ca,
            AccountStatus status, LegalEntityStatus leStatus) {
        return createAccount(id, accountName, leName, ca, null, status, leStatus);
    }

    private Account createAccount(Long id, String accountName, String leName, CompetentAuthority ca,
            Long verificationBodyId, 
            AccountStatus status, LegalEntityStatus leStatus) {
        LegalEntity le = LegalEntity.builder()
                .location(
                        LocationOnShore.builder()
                            .gridReference("grid")
                            .address(
                                    Address.builder()
                                        .city("city")
                                        .country("GR")
                                        .line1("line")
                                        .postcode("postcode")
                                        .build())
                            .build())
                .name(leName)
                .status(leStatus)
                .referenceNumber("regNumber")
                .type(LegalEntityType.LIMITED_COMPANY)
                .build();
        entityManager.persist(le);
        Account account = Account.builder()
                .id(id)
                .legalEntity(le)
                .accountType(AccountType.INSTALLATION)
                .commencementDate(LocalDate.now())
                .competentAuthority(ca)
                .verificationBodyId(verificationBodyId)
                .status(status)
                .location(
                        LocationOnShore.builder()
                            .gridReference("grid")
                            .address(
                                    Address.builder()
                                        .city("city")
                                        .country("GR")
                                        .line1("line")
                                        .postcode("postcode")
                                        .build())
                            .build())
                .name(accountName)
                .siteName(accountName)
                .emissionTradingScheme(EmissionTradingScheme.UK_ETS_INSTALLATIONS)
                .emitterId("EM" + String.format("%05d", id))
                .build();
        entityManager.persist(account);
        return account;
    }
    
    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }
}
