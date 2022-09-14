package uk.gov.pmrv.api.workflow.request.flow.permitvariation.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class PermitVariationApplicationSubmitRequestTaskInitializerTest {

	@InjectMocks
    private PermitVariationApplicationSubmitRequestTaskInitializer handler;
	
	@Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
	
	@Mock
    private PermitQueryService permitQueryService;
	
	@Test
	void initializePayload() {
		UUID attachment = UUID.randomUUID();
		Long accountId = 1L;
		Request request = Request.builder().accountId(accountId).build();
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
        		.operator("operator")
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
						.build())
				.permitAttachments(Map.of(attachment, "att"))
				.build();
		
		when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);
		when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
		.thenReturn(installationOperatorDetails);
		
		RequestTaskPayload result = handler.initializePayload(request);
		
		InstallationOperatorDetails actualInstallationOperatorDetails =
                ((PermitVariationApplicationSubmitRequestTaskPayload)result).getInstallationOperatorDetails();
		
		verify(permitQueryService, times(1)).getPermitContainerByAccountId(accountId);
		verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
		
		
		PermitVariationApplicationSubmitRequestTaskPayload payload = (PermitVariationApplicationSubmitRequestTaskPayload) result;
		assertThat(payload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD);
		assertThat(actualInstallationOperatorDetails).isEqualTo(installationOperatorDetails);
		assertThat(payload.getPermitType()).isEqualTo(permitContainer.getPermitType());
		assertThat(payload.getPermit()).isEqualTo(permitContainer.getPermit());
		assertThat(payload.getPermitAttachments()).isEqualTo(permitContainer.getPermitAttachments());
		
	}
	
	@Test
	void getRequestTaskTypes() {
		assertThat(handler.getRequestTaskTypes()).containsExactlyInAnyOrder(RequestTaskType.PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT,
				RequestTaskType.PERMIT_VARIATION_REGULATOR_APPLICATION_SUBMIT);
	}
}
