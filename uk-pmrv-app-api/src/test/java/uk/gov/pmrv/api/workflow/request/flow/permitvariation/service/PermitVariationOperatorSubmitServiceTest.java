package uk.gov.pmrv.api.workflow.request.flow.permitvariation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationModification;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationModificationType;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationOperatorSaveApplicationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.mapper.PermitVariationMapper;

@ExtendWith(MockitoExtension.class)
class PermitVariationOperatorSubmitServiceTest {

	@InjectMocks
    private PermitVariationOperatorSubmitService service;
	
	@Mock
    private RequestService requestService;
    
    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private PermitValidatorService permitValidatorService;
    
    @Mock 
    private PermitVariationDetailsValidator permitVariationDetailsValidator;
    
    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Test
    void savePermitVariation() {
    	PermitVariationDetails permitVariationDetails = PermitVariationDetails.builder()
				.reason("reason")
				.modifications(List.of(
						PermitVariationModification.builder().type(PermitVariationModificationType.CALCULATION_TO_MEASUREMENT_METHODOLOGIES).build(),
						PermitVariationModification.builder().type(PermitVariationModificationType.OTHER_MONITORING_PLAN).otherSummary("summ").build()
						))
				.build();
    	PermitVariationOperatorSaveApplicationRequestTaskActionPayload taskActionPayload = PermitVariationOperatorSaveApplicationRequestTaskActionPayload.builder()
    			.permitVariationDetails(permitVariationDetails)
    			.permitVariationDetailsCompleted(true)
    			.permit(Permit.builder().abbreviations(Abbreviations.builder().exist(true).build()).build())
    			.permitSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.reviewSectionsCompleted(Map.of("rev_section1", true))
    			.build();
    	
    	RequestTask requestTask = RequestTask.builder()
    			.payload(PermitVariationApplicationSubmitRequestTaskPayload.builder()
    					.build())
    			.build();
    	
    	service.savePermitVariation(taskActionPayload, requestTask);
    	
    	ArgumentCaptor<RequestTask> captor = ArgumentCaptor.forClass(RequestTask.class);
        verify(requestTaskService, times(1)).saveRequestTask(captor.capture());
        RequestTask captured = captor.getValue();
        PermitVariationApplicationSubmitRequestTaskPayload capturedPayload = (PermitVariationApplicationSubmitRequestTaskPayload) captured.getPayload();
        assertThat(capturedPayload.getPermitVariationDetails()).isEqualTo(taskActionPayload.getPermitVariationDetails());
     	assertThat(capturedPayload.getPermitVariationDetailsCompleted()).isEqualTo(taskActionPayload.getPermitVariationDetailsCompleted());
    	assertThat(capturedPayload.getPermit()).isEqualTo(taskActionPayload.getPermit());
    	assertThat(capturedPayload.getPermitSectionsCompleted()).isEqualTo(taskActionPayload.getPermitSectionsCompleted());
    	assertThat(capturedPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
    }
    
    @Test
    void submitPermitVariation() {
    	PmrvUser authUser = PmrvUser.builder().userId("user1").build();
    	UUID att1UUID = UUID.randomUUID();
    	Request request = Request.builder()
    			.accountId(1L)
    			.id("1")
    			.payload(PermitVariationRequestPayload.builder().build())
    			.build();
    	Permit permit = Permit.builder().abbreviations(Abbreviations.builder().exist(true).build()).build();
    	InstallationOperatorDetails installationOperatorDetails1 = InstallationOperatorDetails.builder()
				.installationName("installationName1")
				.build();
    	InstallationOperatorDetails installationOperatorDetails2 = InstallationOperatorDetails.builder()
				.installationName("installationName2")
				.build();
        PermitVariationDetails permitVariationDetails = PermitVariationDetails.builder()
				.reason("reason")
				.modifications(List.of(
						PermitVariationModification.builder().type(PermitVariationModificationType.CALCULATION_TO_MEASUREMENT_METHODOLOGIES).build(),
						PermitVariationModification.builder().type(PermitVariationModificationType.OTHER_MONITORING_PLAN).otherSummary("summ").build()
						))
				.build();
        PermitVariationApplicationSubmitRequestTaskPayload requestTaskPayload = PermitVariationApplicationSubmitRequestTaskPayload.builder()
			.permitVariationDetails(permitVariationDetails)
			.permitType(PermitType.GHGE)
			.permit(permit)
			.installationOperatorDetails(installationOperatorDetails1)
			.permitSectionsCompleted(Map.of("section1", List.of(true, false)))
			.permitVariationDetailsCompleted(Boolean.TRUE)
			.permitAttachments(Map.of(att1UUID, "att1"))
			.reviewSectionsCompleted(Map.of("rev_section1", true))
			.build();
    	RequestTask requestTask = RequestTask.builder()
    			.request(request)
    			.payload(requestTaskPayload)
    			.build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(request.getAccountId()))
				.thenReturn(installationOperatorDetails2);
    	
    	service.submitPermitVariation(requestTask, authUser);
    	
    	PermitContainer permitContainer = PermitContainer.builder()
    			.permitType(PermitType.GHGE)
				.permit(permit)
				.installationOperatorDetails(installationOperatorDetails2)
    			.permitAttachments(Map.of(att1UUID, "att1"))
    			.build();
    	
    	verify(permitValidatorService, times(1)).validatePermit(permitContainer);
    	ArgumentCaptor<Request> captor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(captor.capture());
        Request capturedRequest = captor.getValue();
        PermitVariationRequestPayload capturedRequestPayload = (PermitVariationRequestPayload) capturedRequest.getPayload();
        assertThat(capturedRequestPayload.getPermitVariationDetails()).isEqualTo(permitVariationDetails);
        assertThat(capturedRequestPayload.getPermitType()).isEqualTo(PermitType.GHGE);
        assertThat(capturedRequestPayload.getPermit()).isEqualTo(permit);
        assertThat(capturedRequestPayload.getPermitSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(Map.of("section1", List.of(true, false)));
        assertThat(capturedRequestPayload.getPermitVariationDetailsCompleted()).isTrue();
        assertThat(capturedRequestPayload.getPermitAttachments()).containsExactlyInAnyOrderEntriesOf(Map.of(att1UUID, "att1"));
        assertThat(capturedRequestPayload.getReviewSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(Map.of("rev_section1", true));
        
		PermitVariationApplicationSubmittedRequestActionPayload actionPayload = Mappers
				.getMapper(PermitVariationMapper.class).toPermitVariationApplicationSubmittedRequestActionPayload(
						(PermitVariationApplicationSubmitRequestTaskPayload) requestTask.getPayload(),
						installationOperatorDetails2);
        verify(permitValidatorService, times(1)).validatePermit(Mappers.getMapper(PermitVariationMapper.class).toPermitContainer(requestTaskPayload, installationOperatorDetails2));
        verify(permitVariationDetailsValidator, times(1)).validate(permitVariationDetails);        
        verify(requestService, times(1)).addActionToRequest(capturedRequest, actionPayload, RequestActionType.PERMIT_VARIATION_APPLICATION_SUBMITTED, authUser.getUserId());
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(request.getAccountId());
    }
}
