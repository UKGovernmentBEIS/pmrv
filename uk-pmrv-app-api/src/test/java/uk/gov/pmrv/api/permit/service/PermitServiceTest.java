package uk.gov.pmrv.api.permit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.service.AccountSearchAdditionalKeywordService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.domain.envpermitandlicences.EnvironmentalPermitsAndLicences;
import uk.gov.pmrv.api.permit.repository.PermitRepository;
import uk.gov.pmrv.api.permit.validation.PermitGrantedValidatorService;

@ExtendWith(MockitoExtension.class)
class PermitServiceTest {

    public static final String TEST_PERMIT_ID = "UK-E-IN-00001";
    @InjectMocks
    private PermitService service;

    @Mock
    private PermitRepository permitRepository;

    @Mock
    private PermitGrantedValidatorService permitGrantedValidatorService;

    @Mock
    private PermitIdentifierGenerator generator;

    @Mock
    private AccountSearchAdditionalKeywordService accountSearchAdditionalKeywordService;

    @Test
    void submitPermit() {
        Long accountId = 1L;
        Permit permit = Permit.builder()
            .environmentalPermitsAndLicences(EnvironmentalPermitsAndLicences.builder().exist(false).build())
            .build();
        PermitContainer permitContainer = PermitContainer.builder()
            .permit(permit)
            .build();

        PermitEntity permitEntity = PermitEntity.builder().permitContainer(permitContainer).build();
        String permitEntityId = "1";
        PermitEntity submittedPermitEntity =
            PermitEntity.builder().id(permitEntityId).permitContainer(permitContainer).build();

        when(permitRepository.save(permitEntity))
            .thenReturn(submittedPermitEntity);

        when(generator.generate(accountId)).thenReturn(TEST_PERMIT_ID);

        String submittedPermitId = service.submitPermit(permitContainer, accountId);

        verify(permitGrantedValidatorService, times(1)).validatePermit(permitContainer);
        verify(permitRepository, times(1)).save(permitEntity);
        verify(accountSearchAdditionalKeywordService, times(1)).storeKeywordForAccount(TEST_PERMIT_ID, accountId);

        assertThat(submittedPermitId).isEqualTo("1");
    }

}
