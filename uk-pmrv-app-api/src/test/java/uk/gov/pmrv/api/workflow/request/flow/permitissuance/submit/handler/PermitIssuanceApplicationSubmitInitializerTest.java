package uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceApplicationSubmitRequestTaskPayload;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceApplicationSubmitInitializerTest {

    @InjectMocks
    private PermitIssuanceApplicationSubmitInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;
    
    @Test
    void initializePayload() {
        final Long accountId = 1L;
        Request request = Request.builder()
            .accountId(accountId)
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

        // Mock data
		when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId))
				.thenReturn(installationOperatorDetails);

        // Invoke
        RequestTaskPayload result = initializer.initializePayload(request);

        InstallationOperatorDetails actualInstallationOperatorDetails =
                ((PermitIssuanceApplicationSubmitRequestTaskPayload)result).getInstallationOperatorDetails();
        
        // Verify
        verify(installationOperatorDetailsQueryService, times(1))
                .getInstallationOperatorDetails(accountId);

        assertEquals(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD, result.getPayloadType());
        assertThat(actualInstallationOperatorDetails).isEqualTo(installationOperatorDetails);
    }
    
    @Test
    void getRequestTaskTypes() {
    	assertThat(initializer.getRequestTaskTypes()).containsExactly(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_SUBMIT);
    }
}
