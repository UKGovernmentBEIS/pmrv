package uk.gov.pmrv.api.account.repo;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.dto.AccountContactInfoDTO;
import uk.gov.pmrv.api.account.domain.enumeration.AccountContactType;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

import javax.persistence.EntityManager;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class AccountRepositoryIT extends AbstractContainerBaseTest {

	@Autowired
	private AccountRepository repo;
	
	@Autowired
	private EntityManager entityManager;

	
	@Test
    void findByAccountIdsAndCompAuth() {
        //prepare data
        final String accountName1 = "account1";
        final String accountName2 = "account2";
        final String accountName3 = "account3";
        final String accountName4 = "account4";

        Account account1 = createAccount(1L, accountName1, "leName1", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account2 = createAccount(2L, accountName2, "leName2", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account3 = createAccount(3L, accountName3, "leName3", CompetentAuthority.WALES, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account4 = createAccount(4L, accountName4, "leName4", CompetentAuthority.ENGLAND, AccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);
        
        flushAndClear();
        
        List<Long> accounts = List.of(account1.getId(), account2.getId(), account3.getId(), account4.getId());
        
        //invoke
        List<Account> result = repo.findByAccountIdsAndCompAuth(accounts, CompetentAuthority.ENGLAND);
        
        //verify
        assertThat(result).extracting(Account::getName)
                            .containsOnly(accountName1, accountName2, accountName4);
    }
	
	@Test
    void findByAccountIdsAndVerBody() {
        //prepare data
        final String accountName1 = "account1";
        final String accountName2 = "account2";
        final String accountName3 = "account3";
        final String accountName4 = "account4";
        
        Account account1 = createAccount(1L, accountName1, "leName1", CompetentAuthority.ENGLAND, 1L, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account2 = createAccount(2L, accountName2, "leName2", CompetentAuthority.NORTHERN_IRELAND, 1L, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account3 = createAccount(3L, accountName3, "leName3", CompetentAuthority.ENGLAND, 1L, AccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);
        Account account4 = createAccount(4L, accountName4, "leName4", CompetentAuthority.ENGLAND, 2L, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        
        flushAndClear();
        
        List<Long> accounts = List.of(account1.getId(), account2.getId(), account3.getId(), account4.getId());
        
        //invoke
        List<Account> result = repo.findByAccountIdsAndVerBody(accounts, 1L);
        
        //verify
        assertThat(result).extracting(Account::getName)
                            .containsOnly(accountName1, accountName2, accountName3);
    }
	
	@Test
    void findAccountContactsByCaAndContactType() {
        //prepare data
        Account account1 = createAccount(1L, "account1", "leName1", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        account1.getContacts().put(AccountContactType.CA_SITE, "test1");
        account1.getContacts().put(AccountContactType.PRIMARY, "primary1");
        repo.save(account1);
        
        Account account2 = createAccount(2L, "account2", "leName2", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        account2.getContacts().put(AccountContactType.PRIMARY, "primary2");
        repo.save(account2);
        
        flushAndClear();
        
        //invoke
        Page<AccountContactInfoDTO> result = repo.findApprovedAccountContactsByCaAndContactType(PageRequest.of(0, 10), CompetentAuthority.ENGLAND, AccountContactType.CA_SITE);
        
        //verify
        assertThat(result.getContent()).hasSize(2);
        assertThat(result.getContent().get(0).getAccountId()).isEqualTo(account1.getId());
        assertThat(result.getContent().get(0).getUserId()).isEqualTo("test1");
        assertThat(result.getContent().get(1).getAccountId()).isEqualTo(account2.getId());
        assertThat(result.getContent().get(1).getUserId()).isNull();
    }
	
	@Test
	void findAccountContactsByAccountIdsAndContactType() {
	    Account account1 = createAccount(1L, "account1", "leName1", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        account1.getContacts().put(AccountContactType.CA_SITE, "test1");
        account1.getContacts().put(AccountContactType.PRIMARY, "primary1");
        repo.save(account1);
        
        Account account2 = createAccount(2L, "account2", "leName2", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        account2.getContacts().put(AccountContactType.PRIMARY, "primary2");
        repo.save(account2);
        
        Account account3 = createAccount(3L, "account3", "leName3", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        repo.save(account3);
        
        List<Long> accountIds = List.of(account1.getId(), account2.getId(), account3.getId());
        
        flushAndClear();
        
        //invoke
        List<AccountContactInfoDTO> result = repo.findAccountContactsByAccountIdsAndContactType(accountIds, AccountContactType.CA_SITE);
        
        //verify
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getAccountId()).isEqualTo(account1.getId());
	}
	
	@Test
	void findAccountsByContactTypeAndUserId() {
	    Account account1 = createAccount(1L, "account1", "leName1", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        account1.getContacts().put(AccountContactType.CA_SITE, "test1");
        account1.getContacts().put(AccountContactType.PRIMARY, "primary1");
        repo.save(account1);
        
        Account account2 = createAccount(2L, "account2", "leName2", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        account2.getContacts().put(AccountContactType.PRIMARY, "test1");
        repo.save(account2);
        
        Account account3 = createAccount(3L, "account3", "leName3", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        repo.save(account3);
        
        flushAndClear();
        
        List<Account> result = repo.findAccountsByContactTypeAndUserId(AccountContactType.CA_SITE, "test1");
        
        //verify
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getId()).isEqualTo(account1.getId());
	}
	
	@Test
	void findAllActiveIdsByCA() {
        Account account1 = createAccount(1L, "account1", "leName1", CompetentAuthority.ENGLAND, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthority.WALES, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(3L, "account3", "leName3", CompetentAuthority.ENGLAND, AccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);

        flushAndClear();

        List<Long> result = repo.findAllApprovedIdsByCA(CompetentAuthority.ENGLAND);

        // verify
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(account1.getId());
	}
	
	@Test
	void findAllByVerificationBodyIn() {
	    Account account1 = createAccount(1L, "account1", "leName1", CompetentAuthority.ENGLAND, 1L, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        createAccount(2L, "account2", "leName2", CompetentAuthority.ENGLAND, 2L, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account3 = createAccount(3L, "account3", "leName3", CompetentAuthority.ENGLAND, 3L, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        Account account4 = createAccount(4L, "account4", "leName4", CompetentAuthority.ENGLAND, 3L, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        
        Set<Account> result = repo.findAllByVerificationBodyIn(Set.of(1L, 3L));
        
        assertThat(result).extracting(Account::getId)
                .containsExactlyInAnyOrder(account1.getId(), account3.getId(), account4.getId());
	}
	
	@Test
	void findAllByVerificationBodyAndEmissionTradingSchemeIn() {
	    Long vbId = 1L;
	    Long anotherVbId = 2L;
	    Account account1 = createAccount(1L, "account1", "leName1", CompetentAuthority.ENGLAND, vbId, AccountStatus.LIVE, LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_INSTALLATIONS);
	    Account account2 = createAccount(2L, "account2", "leName2", CompetentAuthority.ENGLAND, vbId, AccountStatus.LIVE, LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_AVIATION);
        createAccount(3L, "account3", "leName3", CompetentAuthority.ENGLAND, vbId, AccountStatus.LIVE, LegalEntityStatus.ACTIVE, EmissionTradingScheme.CORSIA);
        createAccount(4L, "account4", "leName4", CompetentAuthority.ENGLAND, anotherVbId, AccountStatus.LIVE, LegalEntityStatus.ACTIVE, EmissionTradingScheme.UK_ETS_INSTALLATIONS);
        
        Set<Account> result = repo.findAllByVerificationBodyAndEmissionTradingSchemeIn(vbId, Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS, EmissionTradingScheme.UK_ETS_AVIATION));
        
        assertThat(result).extracting(Account::getId)
                .containsExactlyInAnyOrder(account1.getId(), account2.getId());
	}
	
	private Account createAccount(Long id, String accountName, String leName, CompetentAuthority ca, AccountStatus status, LegalEntityStatus leStatus) {
	    return createAccount(id, accountName, leName, ca, null, status, leStatus);
	}
	
	private Account createAccount(Long id, String accountName, String leName, CompetentAuthority ca, Long verificationBodyId, AccountStatus status, LegalEntityStatus leStatus) {
	    return createAccount(id, accountName, leName, ca, verificationBodyId, status, leStatus, EmissionTradingScheme.UK_ETS_INSTALLATIONS);
	}
	
	private Account createAccount(Long id, String accountName, String leName, CompetentAuthority ca, Long verificationBodyId, AccountStatus status, LegalEntityStatus leStatus, EmissionTradingScheme emissionTradingScheme) {
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
				.emissionTradingScheme(emissionTradingScheme)
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
