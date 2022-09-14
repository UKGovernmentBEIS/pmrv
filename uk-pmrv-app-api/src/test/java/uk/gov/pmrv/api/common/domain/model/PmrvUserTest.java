package uk.gov.pmrv.api.common.domain.model;

import org.junit.jupiter.api.Test;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;

import java.util.List;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

class PmrvUserTest {

    @Test
    void getAccounts() {
        Long accountId = 1L;
        PmrvUser pmrvUser = createOperatorUser("user");
        List<PmrvAuthority> authorities = List.of(
            PmrvAuthority.builder().accountId(accountId).build()
        );
        pmrvUser.setAuthorities(authorities);

        Set<Long> accounts = pmrvUser.getAccounts();

        assertThat(accounts).containsOnly(accountId);
    }

    @Test
    void getAccounts_no_authorities() {
        PmrvUser pmrvUser = createOperatorUser("user");

        Set<Long> accounts = pmrvUser.getAccounts();

        assertThat(accounts).isEmpty();
    }

    @Test
    void getAccounts_no_account_authorities() {
        PmrvUser pmrvUser = createRegulatorUser("user");
        List<PmrvAuthority> authorities = List.of(
            PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build()
        );
        pmrvUser.setAuthorities(authorities);

        Set<Long> accounts = pmrvUser.getAccounts();

        assertThat(accounts).isEmpty();
    }

    @Test
    void getVerificationBodyId() {
        Long vbId = 1L;
        PmrvUser pmrvUser = createVerifierUser("user");
        List<PmrvAuthority> authorities = List.of(
            PmrvAuthority.builder().verificationBodyId(vbId).build()
        );
        pmrvUser.setAuthorities(authorities);

        Long optionalVbId = pmrvUser.getVerificationBodyId();

        assertThat(optionalVbId).isNotNull();
        assertEquals(vbId, optionalVbId);
    }

    @Test
    void getVerificationBodyId_no_authorities() {
        PmrvUser pmrvUser = createVerifierUser("user");

        Long optionalVbId = pmrvUser.getVerificationBodyId();

        assertThat(optionalVbId).isNull();
    }

    @Test
    void getVerificationBodyId_no__verifier_authorities() {
        PmrvUser pmrvUser = createVerifierUser("user");
        List<PmrvAuthority> authorities = List.of(
            PmrvAuthority.builder().competentAuthority(CompetentAuthority.ENGLAND).build()
        );
        pmrvUser.setAuthorities(authorities);

        Long optionalVbId = pmrvUser.getVerificationBodyId();

        assertThat(optionalVbId).isNull();
    }

    @Test
    void getCompetentAuthority() {
        CompetentAuthority competentAuthority = CompetentAuthority.ENGLAND;
        PmrvUser pmrvUser = createRegulatorUser("user");
        List<PmrvAuthority> authorities = List.of(
            PmrvAuthority.builder().competentAuthority(competentAuthority).build()
        );
        pmrvUser.setAuthorities(authorities);

        CompetentAuthority optionalCompetentAuthority = pmrvUser.getCompetentAuthority();

        assertThat(optionalCompetentAuthority).isNotNull();
        assertEquals(competentAuthority, optionalCompetentAuthority);
    }

    @Test
    void getCompetentAuthority_no_authorities() {
        PmrvUser pmrvUser = createRegulatorUser("user");

        CompetentAuthority optionalCompetentAuthority = pmrvUser.getCompetentAuthority();

        assertThat(optionalCompetentAuthority).isNull();
    }

    @Test
    void getVerificationBodyId_no__regulator_authorities() {
        PmrvUser pmrvUser = createVerifierUser("user");
        List<PmrvAuthority> authorities = List.of(
            PmrvAuthority.builder().accountId(1L).build()
        );
        pmrvUser.setAuthorities(authorities);

        CompetentAuthority optionalCompetentAuthority = pmrvUser.getCompetentAuthority();

        assertThat(optionalCompetentAuthority).isNull();
    }

	private PmrvUser createRegulatorUser(String userId) {
    	return createUser(userId, RoleType.REGULATOR);
    }
	
	private PmrvUser createOperatorUser(String userId) {
    	return createUser(userId, RoleType.OPERATOR);
    }

    private PmrvUser createVerifierUser(String userId) {
        return createUser(userId, RoleType.VERIFIER);
    }
	
	private PmrvUser createUser(String userId, RoleType roleType) {
    	return PmrvUser.builder()
    				.userId(userId)
    				.email("email@email")
    				.firstName("fn")
    				.lastName("ln")
    				.roleType(roleType)
    				.build();
    }
	
}
