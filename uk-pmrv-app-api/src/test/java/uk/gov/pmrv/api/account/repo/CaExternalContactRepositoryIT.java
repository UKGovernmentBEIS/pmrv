package uk.gov.pmrv.api.account.repo;

import static org.assertj.core.api.Assertions.assertThat;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.ENGLAND;
import static uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority.WALES;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;

import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.account.domain.CaExternalContact;
import uk.gov.pmrv.api.account.repository.CaExternalContactRepository;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import({ObjectMapper.class})
class CaExternalContactRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    CaExternalContactRepository repo;

    @Autowired
    EntityManager em;

    @Test
    void findByCompetentAuthorityOrderByLastUpdatedDate() {
        CaExternalContact c1 = CaExternalContact.builder()
            .competentAuthority(ENGLAND)
            .name("c1")
            .description("c1")
            .email("email1")
            .build();

        em.persist(c1);

        CaExternalContact c2 = CaExternalContact.builder()
            .competentAuthority(ENGLAND)
            .name("c2")
            .description("c2")
            .email("email2")
            .build();

        em.persist(c2);

        CaExternalContact c3 = CaExternalContact.builder()
            .competentAuthority(WALES)
            .name("c3")
            .description("c3")
            .email("email3")
            .build();

        em.persist(c3);

        List<CaExternalContact> result = repo.findByCompetentAuthority(ENGLAND);

        assertThat(result).containsExactlyInAnyOrder(c1, c2);
    }

    @Test
    void deleteById() {
        CaExternalContact c1 = CaExternalContact.builder()
            .competentAuthority(ENGLAND)
            .name("c1")
            .description("c1")
            .email("email1")
            .build();

        em.persist(c1);

        CaExternalContact c2 = CaExternalContact.builder()
            .competentAuthority(ENGLAND)
            .name("c2")
            .description("c2")
            .email("email2")
            .build();

        em.persist(c2);

        repo.deleteById(c1.getId());

        assertThat(em.find(CaExternalContact.class, c1.getId())).isNull();
        assertThat(em.find(CaExternalContact.class, c2.getId())).isNotNull();
    }

    @Test
    void findByIdAndCompetentAuthority() {
        CaExternalContact c1 = CaExternalContact.builder()
            .competentAuthority(ENGLAND)
            .name("c1")
            .description("c1")
            .email("email1")
            .build();

        em.persist(c1);

        CaExternalContact c2 = CaExternalContact.builder()
            .competentAuthority(WALES)
            .name("c2")
            .description("c2")
            .email("email2")
            .build();

        em.persist(c2);

        Optional<CaExternalContact> result = repo.findByIdAndCompetentAuthority(c1.getId(), ENGLAND);

        assertThat(result).hasValue(c1);
    }

    @Test
    void existsByCompetentAuthorityAndName_true() {

        CaExternalContact c1 = CaExternalContact.builder()
            .competentAuthority(ENGLAND)
            .name("c1")
            .description("c1")
            .email("email1")
            .build();

        em.persist(c1);

        assertThat(repo.existsByCompetentAuthorityAndName(ENGLAND, "c1")).isTrue();
    }

    @Test
    void existsByCompetentAuthorityAndName_false() {

        CaExternalContact c1 = CaExternalContact.builder()
            .competentAuthority(WALES)
            .name("c1")
            .description("c1")
            .email("email1")
            .build();

        em.persist(c1);

        assertThat(repo.existsByCompetentAuthorityAndName(ENGLAND, "c1")).isFalse();
    }

    @Test
    void existsByCompetentAuthorityAndEmail_true() {

        CaExternalContact c = CaExternalContact.builder()
            .competentAuthority(ENGLAND)
            .name("c1")
            .description("c1")
            .email("email1")
            .build();

        em.persist(c);

        assertThat(repo.existsByCompetentAuthorityAndEmail(ENGLAND, "email1")).isTrue();
    }

    @Test
    void existsByCompetentAuthorityAndEmail_false() {

        CaExternalContact c = CaExternalContact.builder()
            .competentAuthority(ENGLAND)
            .name("c1")
            .description("c1")
            .email("email2")
            .build();

        em.persist(c);

        assertThat(repo.existsByCompetentAuthorityAndEmail(ENGLAND, "email1")).isFalse();
    }

    @Test
    void existsByCompetentAuthorityAndNameAndIdNot_true() {

        CaExternalContact c = CaExternalContact.builder()
            .competentAuthority(ENGLAND)
            .name("c1")
            .description("c1")
            .email("email")
            .build();

        em.persist(c);

        assertThat(repo.existsByCompetentAuthorityAndNameAndIdNot(ENGLAND, "c1", 999999999999L)).isTrue();
    }

    @Test
    void existsByCompetentAuthorityAndNameAndIdNot_false() {

        CaExternalContact c = CaExternalContact.builder()
            .competentAuthority(ENGLAND)
            .name("c1")
            .description("c1")
            .email("email2")
            .build();

        em.persist(c);

        assertThat(repo.existsByCompetentAuthorityAndNameAndIdNot(ENGLAND, "c2", c.getId())).isFalse();
    }

    @Test
    void existsByCompetentAuthorityAndEmailAndIdNot_true() {

        CaExternalContact c = CaExternalContact.builder()
            .competentAuthority(ENGLAND)
            .name("c1")
            .description("c1")
            .email("email")
            .build();

        em.persist(c);

        assertThat(repo.existsByCompetentAuthorityAndEmailAndIdNot(ENGLAND, "email", 999999999999L)).isTrue();
    }

    @Test
    void existsByCompetentAuthorityAndEmailAndIdNot_false() {

        CaExternalContact c = CaExternalContact.builder()
            .competentAuthority(ENGLAND)
            .name("c1")
            .description("c1")
            .email("email1")
            .build();

        em.persist(c);

        assertThat(repo.existsByCompetentAuthorityAndEmailAndIdNot(ENGLAND, "email2", c.getId())).isFalse();
    }

    @Test
    void save() {
        CaExternalContact c = CaExternalContact.builder()
            .competentAuthority(ENGLAND)
            .name("c1")
            .description("c1")
            .email("email")
            .build();

        CaExternalContact savedContact = repo.save(c);

        assertThat(savedContact.getCompetentAuthority()).isEqualTo(ENGLAND);
        assertThat(savedContact.getName()).isEqualTo("c1");
        assertThat(savedContact.getDescription()).isEqualTo("c1");
        assertThat(savedContact.getEmail()).isEqualTo("email");
        assertThat(savedContact.getLastUpdatedDate()).isNotNull();
    }
}
