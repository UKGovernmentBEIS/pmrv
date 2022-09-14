package uk.gov.pmrv.api.workflow.request.flow.permitvariation.service;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.enumeration.AccountStatus;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestCreateActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestQueryService;

@ExtendWith(MockitoExtension.class)
class PermitVariationCreateValidatorTest {

	@InjectMocks
    private PermitVariationCreateValidator validator;
	
	@Mock
    private AccountQueryService accountQueryService;
    
    @Mock
    private RequestQueryService requestQueryService;
    
    @Test
    void getType() {
        assertThat(validator.getType()).isEqualTo(RequestCreateActionType.PERMIT_VARIATION);
    }
    
    @Test
    void getApplicableAccountStatuses() {
        assertThat(validator.getApplicableAccountStatuses()).isEqualTo(Set.of(AccountStatus.LIVE));
    }
    
    @Test
    void getMutuallyExclusiveRequests() {
		assertThat(validator.getMutuallyExclusiveRequests()).isEqualTo(
				Set.of(RequestType.PERMIT_VARIATION, RequestType.PERMIT_TRANSFER, RequestType.PERMIT_SURRENDER));
    }
}
