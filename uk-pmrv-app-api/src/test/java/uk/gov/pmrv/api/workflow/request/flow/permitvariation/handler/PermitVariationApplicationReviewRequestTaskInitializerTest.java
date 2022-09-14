package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.inherentco2.InherentCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationRequestPayload;

@ExtendWith(MockitoExtension.class)
class PermitVariationApplicationReviewRequestTaskInitializerTest {

	@InjectMocks
    private PermitVariationApplicationReviewRequestTaskInitializer handler;
	
	@Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
	
	@Mock
    private PermitQueryService permitQueryService;
	
	@Test
	void initializePayload() {
		UUID attachment = UUID.randomUUID();
		Long accountId = 1L;
		
		PermitContainer originalPermitContainer = PermitContainer.builder()
				.permitType(PermitType.GHGE)
				.permit(Permit.builder()
						.abbreviations(Abbreviations.builder().exist(true).build())
						.monitoringApproaches(MonitoringApproaches.builder()
		    					.monitoringApproaches(Map.of(
		    							MonitoringApproachType.INHERENT_CO2, InherentCO2MonitoringApproach.builder().approachDescription("apprdescr").build()
		    							))
		    					.build())
						.build())
				.permitAttachments(Map.of(attachment, "att"))
				.build();
		
		InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
        		.installationName("Account name")
				.siteName("siteName")
				.installationLocation(LocationOnShoreDTO.builder()
						.type(LocationType.ONSHORE)
						.gridReference("ST330000")
						.address(AddressDTO.builder()
								.line1("line1")
								.city("city")
								.country("GB")
								.postcode("postcode")
								.build())
						.build())
				.operatorType(LegalEntityType.LIMITED_COMPANY)
				.companyReferenceNumber("408812")
				.operatorDetailsAddress(AddressDTO.builder()
						.line1("line1")
						.city("city")
						.country("GR")
						.postcode("postcode")
						.build())
        		.build();
		PermitContainer permitContainer = PermitContainer.builder()
				.permitType(PermitType.GHGE)
				.permit(Permit.builder()
						.abbreviations(Abbreviations.builder().exist(false).build())
						.monitoringApproaches(MonitoringApproaches.builder()
								.monitoringApproaches(Map.of(
										MonitoringApproachType.INHERENT_CO2, InherentCO2MonitoringApproach.builder().approachDescription("apprdescr").build()
										))
								.build())
						.build())
				.permitAttachments(Map.of(attachment, "att"))
				.build();
		PermitVariationRequestPayload requestPayload = PermitVariationRequestPayload.builder()
				.permitType(PermitType.GHGE)
				.permit(permitContainer.getPermit())
				.permitVariationDetails(PermitVariationDetails.builder().reason("reason").build())
				.permitVariationDetailsCompleted(Boolean.TRUE)
				.permitSectionsCompleted(Map.of("section1", List.of(true, false)))
    			.reviewSectionsCompleted(Map.of("section2", true))
				.build();
		Request request = Request.builder().accountId(accountId).payload(requestPayload).build();
		
		when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(originalPermitContainer);
		when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
			.thenReturn(installationOperatorDetails);
		
		// invoke
		RequestTaskPayload result = handler.initializePayload(request);
		PermitVariationApplicationReviewRequestTaskPayload variationTaskPayloadResult = (PermitVariationApplicationReviewRequestTaskPayload) result;
		assertThat(variationTaskPayloadResult.getPermit()).isEqualTo(requestPayload.getPermit());
		assertThat(variationTaskPayloadResult.getPermitType()).isEqualTo(requestPayload.getPermitType());
		assertThat(variationTaskPayloadResult.getPermitVariationDetails()).isEqualTo(requestPayload.getPermitVariationDetails());
		assertThat(variationTaskPayloadResult.getPermitVariationDetailsCompleted()).isEqualTo(requestPayload.getPermitVariationDetailsCompleted());
		assertThat(variationTaskPayloadResult.getPermitSectionsCompleted()).isEqualTo(requestPayload.getPermitSectionsCompleted());
		assertThat(variationTaskPayloadResult.getReviewSectionsCompleted()).isEqualTo(requestPayload.getReviewSectionsCompleted());
		assertThat(variationTaskPayloadResult.getInstallationOperatorDetails()).isEqualTo(installationOperatorDetails);
		assertThat(variationTaskPayloadResult.getOriginalPermitContainer()).isEqualTo(originalPermitContainer);
		assertThat(variationTaskPayloadResult.getReviewGroupDecisions().keySet())
		.containsExactlyInAnyOrder(PermitReviewGroup.PERMIT_TYPE,
			PermitReviewGroup.INSTALLATION_DETAILS,
			PermitReviewGroup.FUELS_AND_EQUIPMENT,
			PermitReviewGroup.DEFINE_MONITORING_APPROACHES, 
			PermitReviewGroup.UNCERTAINTY_ANALYSIS,
			PermitReviewGroup.MANAGEMENT_PROCEDURES,
			PermitReviewGroup.MONITORING_METHODOLOGY_PLAN, 
			PermitReviewGroup.ADDITIONAL_INFORMATION,
			PermitReviewGroup.CONFIDENTIALITY_STATEMENT,
			PermitReviewGroup.INHERENT_CO2);
		
		verify(permitQueryService, times(1)).getPermitContainerByAccountId(accountId);
		verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
	}
	
}
