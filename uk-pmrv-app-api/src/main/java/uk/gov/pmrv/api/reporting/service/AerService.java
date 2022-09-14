package uk.gov.pmrv.api.reporting.service;

import java.time.Year;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.reporting.domain.AerContainer;
import uk.gov.pmrv.api.reporting.domain.AerEntity;
import uk.gov.pmrv.api.reporting.repository.AerRepository;
import uk.gov.pmrv.api.reporting.util.AerIdentifierGenerator;
import uk.gov.pmrv.api.reporting.validation.AerValidatorService;

@Service
@RequiredArgsConstructor
public class AerService {

    private final AerRepository aerRepository;
    private final AerValidatorService aerValidatorService;

    @Transactional
    public void submitAer(AerContainer aerContainer, Long accountId, Year reportingYear) {
        aerValidatorService.validate(aerContainer);

        AerEntity aerEntity = AerEntity.builder()
            .id(AerIdentifierGenerator.generate(accountId, reportingYear.getValue()))
            .aerContainer(aerContainer)
            .accountId(accountId)
            .year(reportingYear)
            .build();

        aerRepository.save(aerEntity);
    }
}
