package uk.gov.pmrv.api.verificationbody.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import javax.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.common.domain.Address;
import uk.gov.pmrv.api.common.domain.enumeration.EmissionTradingScheme;
import uk.gov.pmrv.api.verificationbody.domain.VerificationBody;
import uk.gov.pmrv.api.verificationbody.domain.dto.VerificationBodyNameInfoDTO;
import uk.gov.pmrv.api.verificationbody.enumeration.VerificationBodyStatus;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class VerificationBodyRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private VerificationBodyRepository repo;

    @Autowired
    EntityManager entityManager;

    @Test
    void findActiveVerificationBodiesAccreditedToType() {
        VerificationBody vb1 = createVerificationBody("vb1", "accredRefNum1",
            VerificationBodyStatus.ACTIVE, Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS));

        VerificationBody vb2 = createVerificationBody("vb2", "accredRefNum2",
            VerificationBodyStatus.ACTIVE, Set.of(EmissionTradingScheme.UK_ETS_AVIATION));

        VerificationBody vb3 = createVerificationBody("vb3", "accredRefNum3",
            VerificationBodyStatus.DISABLED, Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS));

        flushAndClear();

        EmissionTradingScheme vbType = EmissionTradingScheme.UK_ETS_INSTALLATIONS;

        //invoke
        List<VerificationBodyNameInfoDTO> result = repo.findActiveVerificationBodiesAccreditedToEmissionTradingScheme(vbType);

        //assert
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getName()).isEqualTo("vb1");
    }

    @Test
    void findByIdEagerEmissionTradingSchemes() {
        Set<EmissionTradingScheme> emissionTradingSchemes = Set.of(
            EmissionTradingScheme.UK_ETS_INSTALLATIONS,
            EmissionTradingScheme.UK_ETS_AVIATION
        );
        VerificationBody vb = createVerificationBody(
            "vb1",
            "accredRefNum1",
            VerificationBodyStatus.ACTIVE,
            emissionTradingSchemes
        );

        flushAndClear();

        //invoke
        Optional<VerificationBody> result = repo.findByIdEagerEmissionTradingSchemes(vb.getId());

        //assert
        assertThat(result)
                .isNotEmpty()
                .contains(vb);
        assertThat(result.get().getEmissionTradingSchemes()).
            containsExactlyInAnyOrder(EmissionTradingScheme.UK_ETS_INSTALLATIONS, EmissionTradingScheme.UK_ETS_AVIATION);
    }

    @Test
    void existsByIdAndStatus_true() {
        VerificationBody vb = createVerificationBody("vb1", "accredRefNum1", VerificationBodyStatus.ACTIVE, Set.of());
        entityManager.persist(vb);
        flushAndClear();

        assertThat(repo.existsByIdAndStatus(vb.getId(), VerificationBodyStatus.ACTIVE)).isTrue();
    }

    @Test
    void existsByIdAndStatus_false() {
        VerificationBody vb = createVerificationBody("vb1", "accredRefNum1", VerificationBodyStatus.DISABLED, Set.of());
        entityManager.persist(vb);
        flushAndClear();

        assertThat(repo.existsByIdAndStatus(vb.getId(), VerificationBodyStatus.ACTIVE)).isFalse();
    }

    @Test
    void existsByIdAndStatusNot_false() {
        VerificationBody vb = createVerificationBody("vb1", "accredRefNum1", VerificationBodyStatus.DISABLED, Set.of());
        entityManager.persist(vb);
        flushAndClear();

        assertThat(repo.existsByIdAndStatusNot(vb.getId(), VerificationBodyStatus.DISABLED)).isFalse();
    }

    @Test
    void existsByIdAndStatusNot_true() {
        VerificationBody vb = createVerificationBody("vb1", "accredRefNum1", VerificationBodyStatus.ACTIVE, Set.of());
        entityManager.persist(vb);
        flushAndClear();

        assertThat(repo.existsByIdAndStatusNot(vb.getId(), VerificationBodyStatus.DISABLED)).isTrue();
    }

    @Test
    void isVerificationBodyAccreditedToEmissionTradingScheme_return_true() {
        VerificationBody vb = createVerificationBody("vb1", "accredRefNum1",
            VerificationBodyStatus.ACTIVE, Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS));

        flushAndClear();

        assertThat(repo.isVerificationBodyAccreditedToEmissionTradingScheme(vb.getId(), EmissionTradingScheme.UK_ETS_INSTALLATIONS)).isTrue();
    }

    @Test
    void isVerificationBodyAccreditedToEmissionTradingScheme_return_false() {
        VerificationBody vb = createVerificationBody("vb1", "accredRefNum1",
            VerificationBodyStatus.ACTIVE, Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS));

        flushAndClear();

        assertThat(repo.isVerificationBodyAccreditedToEmissionTradingScheme(vb.getId(), EmissionTradingScheme.UK_ETS_AVIATION)).isFalse();
    }

    @Test
    void isVerificationBodyAccreditedToEmissionTradingScheme_not_active_return_false() {
        VerificationBody vb = createVerificationBody("vb1", "accredRefNum1",
            VerificationBodyStatus.DISABLED, Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS));

        flushAndClear();

        assertThat(repo.isVerificationBodyAccreditedToEmissionTradingScheme(vb.getId(), EmissionTradingScheme.UK_ETS_INSTALLATIONS)).isFalse();
    }

    @Test
    void findByIdNot(){
        VerificationBody vb1 = createVerificationBody("vb1", "accredRefNum1",
            VerificationBodyStatus.ACTIVE, Set.of(EmissionTradingScheme.UK_ETS_INSTALLATIONS));

        VerificationBody vb2 = createVerificationBody("vb2", "accredRefNum2",
            VerificationBodyStatus.ACTIVE, Set.of(EmissionTradingScheme.UK_ETS_AVIATION));

        flushAndClear();

        //invoke
        List<VerificationBody> result = repo.findByIdNot(vb1.getId());

        //assert
        assertThat(result).hasSize(1);
        assertThat(result).containsExactly(vb2);
    }

    private void flushAndClear() {
        entityManager.flush();
        entityManager.clear();
    }

    private VerificationBody createVerificationBody(String name, String accreditationRefNum, VerificationBodyStatus status,
                                                    Set<EmissionTradingScheme> emissionTradingSchemes) {
        VerificationBody vb =
                VerificationBody.builder()
                    .name(name)
                    .status(status)
                    .address(Address.builder().city("city").country("GR").line1("line1").postcode("postcode").build())
                    .createdDate(LocalDateTime.now())
                    .accreditationReferenceNumber(accreditationRefNum)
                    .emissionTradingSchemes(emissionTradingSchemes)
                    .build();
        entityManager.persist(vb);
        return vb;
    }
}
