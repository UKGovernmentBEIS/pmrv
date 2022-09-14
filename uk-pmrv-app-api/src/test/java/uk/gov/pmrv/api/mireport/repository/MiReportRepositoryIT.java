package uk.gov.pmrv.api.mireport.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.DirtiesContext;
import org.testcontainers.junit.jupiter.Testcontainers;
import uk.gov.pmrv.api.AbstractContainerBaseTest;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.mireport.domain.MiReportEntity;
import uk.gov.pmrv.api.mireport.domain.dto.MiReportSearchResult;
import uk.gov.pmrv.api.mireport.enumeration.MiReportType;

@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_CLASS)
@Testcontainers
@DataJpaTest
@Import(ObjectMapper.class)
class MiReportRepositoryIT extends AbstractContainerBaseTest {

    @Autowired
    private MiReportRepository miReportRepository;

    @Test
    void findReportTypes() {
        CompetentAuthority[] competentAuthorities = CompetentAuthority.values();
        MiReportType[] miReportTypes = MiReportType.values();
        Set<String> reportNames = Arrays.stream(miReportTypes).map(r -> r.getName()).collect(Collectors.toSet());

        int index = 1;
        for (CompetentAuthority authority : competentAuthorities) {
            for (MiReportType miReportType : miReportTypes) {
                MiReportEntity entity = MiReportEntity.builder()
                    .id(index++)
                    .competentAuthority(authority)
                    .miReportType(miReportType)
                    .build();
                miReportRepository.save(entity);
            }
        }
        miReportRepository.flush();


        for (CompetentAuthority value : competentAuthorities) {
            List<MiReportSearchResult> result = miReportRepository.findByCompetentAuthority(value);
            assertThat(result).hasSize(miReportTypes.length);
            assertThat(
                result.stream().map(m -> m.getMiReportType().getName()).allMatch(reportNames::contains)
            ).isTrue();
        }
    }

}