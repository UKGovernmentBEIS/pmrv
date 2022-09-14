package uk.gov.pmrv.api.account.repo;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.domain.LegalEntity;
import uk.gov.pmrv.api.account.domain.LocationOnShore;
import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.domain.enumeration.AccountType;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityStatus;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.repository.LegalEntityRepository;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class LegalEntityRepositoryIT extends AbstractContainerBaseTest {
	
	@Autowired
	private LegalEntityRepository repo;
	
	@Autowired
	private EntityManager entityManager;
	
	@Test
	void findActive() {
		final String leActiveName = "leActive";
		final String lePendingName = "lePending";
		createLegalEntity(leActiveName, LegalEntityStatus.ACTIVE);
		createLegalEntity(lePendingName, LegalEntityStatus.PENDING);
		
		//invoke
		List<LegalEntity> results = repo.findActive();
		
		flushAndClear();
		
		//assert
		assertThat(results).hasSize(1);
		assertThat(results.get(0).getName()).isEqualTo(leActiveName);
	}

	@Test
	void findActiveLegalEntitiesByAccounts() {
		final String le1Name = "le1";
		Account account1Active = createAccount(1L, "account1", le1Name, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
		
		final String le2Name = "le1_pending";
		Account account2Pending = createAccount(2L, "account1_pending", le2Name, AccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);
		
		createAccount(3L, "account3", "le3", AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
		
		flushAndClear();
		
		List<LegalEntity> list = repo.findActiveLegalEntitiesByAccounts(Set.of(account1Active.getId(), account2Pending.getId()));
		assertThat(list).hasSize(1);
		assertThat(list.get(0).getName()).isEqualTo(le1Name);
	}
	
	@Test
	void existsLegalEntityInAnyOfAccounts_false() {
		Account account = createAccount(1L, "account1", "le", AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
		
		flushAndClear();

		boolean result = repo.existsLegalEntityInAnyOfAccounts(account.getLegalEntity().getId(), Set.of(-1L)); 
		assertThat(result).isFalse();
	}
	
	@Test
    void existsLegalEntityInAnyOfAccounts_true() {
        Account account = createAccount(1L, "account1", "le", AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
        
        flushAndClear();
        
        boolean result = repo.existsLegalEntityInAnyOfAccounts(account.getLegalEntity().getId(), Set.of(-1L, account.getId()));
        assertThat(result).isTrue();
    }
	
	@Test
	void existsActiveLegalEntityNameInAnyOfAccounts_false() {
		final String leName = "le";
		createAccount(1L, "account1", leName, AccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);
		
		flushAndClear();
		
		boolean result = repo.existsActiveLegalEntityNameInAnyOfAccounts(leName, Set.of(-1L));
		assertThat(result).isFalse();
	}
	
	@Test
	void existsActiveLegalEntityNameInAnyOfAccounts_true() {
		final String leName = "le1";
		
		Account account = createAccount(1L, "account1", leName, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
		
		flushAndClear();
		
		boolean result = repo.existsActiveLegalEntityNameInAnyOfAccounts(leName, Set.of(-1L, account.getId())); 
		assertThat(result).isTrue();
	}
	
	@Test
	void existsActiveLegalEntity_false() {
		final String leName = "le1";
		
		createAccount(1L, "account1", leName, AccountStatus.UNAPPROVED, LegalEntityStatus.PENDING);
		
		flushAndClear();
		
		assertThat(repo.existsActiveLegalEntity(leName)).isFalse();
	}
	
	@Test
	void existsActiveLegalEntity_true() {
		//create authorities for user1
		final String leName = "le1";
		
		createAccount(1L, "account1", leName, AccountStatus.LIVE, LegalEntityStatus.ACTIVE);
		
		flushAndClear();
		
		assertThat(repo.existsActiveLegalEntity(leName)).isTrue();
	}

    @Test
    void existsByNameAndStatusAndIdNot_false() {
	    String leName1 = "leName1";
	    String leName2 = "leName2";

        LegalEntity le1 = createLegalEntity(leName1, LegalEntityStatus.ACTIVE);
        createLegalEntity(leName2, LegalEntityStatus.ACTIVE);

        assertThat(repo.existsByNameAndStatusAndIdNot(leName1, LegalEntityStatus.ACTIVE, le1.getId())).isFalse();
    }

    @Test
    void existsByNameAndStatusAndIdNot_false2() {
        String leName1 = "leName1";
        String leName2 = "leName2";

        LegalEntity le1 = createLegalEntity(leName1, LegalEntityStatus.ACTIVE);
        createLegalEntity(leName2, LegalEntityStatus.PENDING);

        assertThat(repo.existsByNameAndStatusAndIdNot(leName2, LegalEntityStatus.ACTIVE, le1.getId())).isFalse();
    }

    @Test
    void existsByNameAndStatusAndIdNot_true() {
        String leName1 = "leName1";
        String leName2 = "leName2";

        LegalEntity le1 = createLegalEntity(leName1, LegalEntityStatus.ACTIVE);
        createLegalEntity(leName2, LegalEntityStatus.ACTIVE);

        assertThat(repo.existsByNameAndStatusAndIdNot(leName2, LegalEntityStatus.ACTIVE, le1.getId())).isTrue();
    }
	
	private Account createAccount(Long id, String accountName, String leName, AccountStatus accountStatus, LegalEntityStatus leStatus) {
		LegalEntity le = createLegalEntity(leName, leStatus);
		
		Account account = Account.builder()
				.id(id)
				.accountType(AccountType.INSTALLATION)
				.status(accountStatus)
				.commencementDate(LocalDate.now())
				.competentAuthority(CompetentAuthority.ENGLAND)
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
				.emitterId("EM0000" + id.toString())
				.legalEntity(le)
				.build();
		entityManager.persist(account);
		return account;
	}
	
	private LegalEntity createLegalEntity(String name, LegalEntityStatus status) {
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
				.name(name)
				.status(status)
				.referenceNumber("regNumber")
				.type(LegalEntityType.LIMITED_COMPANY)
				.build();
		entityManager.persist(le);
		return le;
	}
	
	private void flushAndClear() {
		entityManager.flush();
		entityManager.clear();
	}
}
