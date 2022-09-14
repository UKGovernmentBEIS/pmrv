package uk.gov.pmrv.api.workflow.request.flow.permitvariation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.workflow.request.core.domain.RequestSequence;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestSequenceRepository;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestParams;

@ExtendWith(MockitoExtension.class)
class PermitVariationRequestIdGeneratorTest {

	@InjectMocks
    private PermitVariationRequestIdGenerator generator;
    
    @Mock
    private RequestSequenceRepository requestSequenceRepository;
    
    @Test
    void getType() {
    	assertThat(generator.getType()).isEqualTo(RequestType.PERMIT_VARIATION);
    }
    
    @Test
    void getPrefix() {
    	assertThat(generator.getPrefix()).isEqualTo("AEMV");
    }
    
    @Test
    void generate() {
    	Long accountId = 1L;
    	long currentSequence = 3;
    	RequestParams params = RequestParams.builder()
    			.accountId(accountId)
    			.type(RequestType.PERMIT_VARIATION)
    			.build();
    	
    	RequestSequence requestSequence = RequestSequence.builder()
    			.id(2L)
    			.accountId(accountId)
    			.sequence(currentSequence)
    			.build();
    	
    	when(requestSequenceRepository.findByAccountIdAndType(accountId, RequestType.PERMIT_VARIATION))
    		.thenReturn(Optional.of(requestSequence));
    	
    	//invoke
    	String result = generator.generate(params);
    	
    	assertThat(result).isEqualTo("AEMV" + accountId + "-" + (currentSequence + 1));
    	
    	ArgumentCaptor<RequestSequence> requestSequenceCaptor = ArgumentCaptor.forClass(RequestSequence.class);  
    	verify(requestSequenceRepository, times(1)).save(requestSequenceCaptor.capture());
    	RequestSequence requestSequenceCaptured = requestSequenceCaptor.getValue();
    	assertThat(requestSequenceCaptured.getSequence()).isEqualTo(currentSequence + 1);
    }
}
