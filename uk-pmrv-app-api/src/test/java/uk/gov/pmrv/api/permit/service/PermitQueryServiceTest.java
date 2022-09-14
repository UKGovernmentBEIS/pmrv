package uk.gov.pmrv.api.permit.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitEntity;
import uk.gov.pmrv.api.permit.domain.dto.PermitEntityAccountDTO;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.permit.repository.PermitRepository;

@ExtendWith(MockitoExtension.class)
class PermitQueryServiceTest {

    @InjectMocks
    private PermitQueryService service;

    @Mock
    private PermitRepository permitRepository;

    @Test
    void getPermitContainerById() {
        String id = "1";
        PermitEntity permitEntity = PermitEntity.builder()
            .permitContainer(PermitContainer.builder()
                .permit(Permit.builder()
                    .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                        .quantity(BigDecimal.valueOf(1000))
                        .build())
                    .build())
                .build())
            .build();

        when(permitRepository.findById(id)).thenReturn(Optional.of(permitEntity));
        PermitContainer actual = service.getPermitContainerById(id);

        assertThat(actual).isEqualTo(permitEntity.getPermitContainer());
    }

    @Test
    void getPermitAccountById() {
        String id = "1";
        Long accountId = 1L;

        when(permitRepository.findPermitAccountById(id)).thenReturn(Optional.of(accountId));
        Long actual = service.getPermitAccountById(id);

        assertThat(actual).isEqualTo(accountId);
    }
    
    @Test
    void getPermitContainerByAccountId() {
    	Long accountId = 1L;
    	PermitContainer permitContainer = PermitContainer.builder()
                    .permit(Permit.builder()
                        .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                            .quantity(BigDecimal.valueOf(1000))
                            .build())
                        .build())
                    .build();
    	PermitEntity permitEntity = PermitEntity.builder()
                .permitContainer(permitContainer)
                .build();

    	
    	when(permitRepository.findByAccountId(accountId)).thenReturn(Optional.of(permitEntity));
    	PermitContainer result = service.getPermitContainerByAccountId(accountId);

        assertThat(result).isEqualTo(permitContainer);
        verify(permitRepository, times(1)).findByAccountId(accountId);
    }
    
    @Test
    void getPermitContainerByAccountId_not_found() {
    	Long accountId = 1L;
    	
    	when(permitRepository.findByAccountId(accountId)).thenReturn(Optional.empty());
    	BusinessException be = assertThrows(BusinessException.class, () -> service.getPermitContainerByAccountId(accountId));

    	assertThat(be.getErrorCode()).isEqualTo(ErrorCode.RESOURCE_NOT_FOUND);
        verify(permitRepository, times(1)).findByAccountId(accountId);
    }

    @Test
    void getPermitByAccountIds() {
        Long accountId = 1L;
        PermitEntityAccountDTO expectedPermitEntityAccountDTO = mock(PermitEntityAccountDTO.class);

        when(expectedPermitEntityAccountDTO.getAccountId()).thenReturn(accountId);
        when(permitRepository.findByAccountIdIn(List.of(accountId))).thenReturn(List.of(expectedPermitEntityAccountDTO));

        Map<Long, PermitEntityAccountDTO> actual = service.getPermitByAccountIds(Collections.singletonList(accountId));

        assertThat(actual.size()).isEqualTo(1);
        assertThat(actual.get(accountId)).isEqualTo(expectedPermitEntityAccountDTO);
    }

}
