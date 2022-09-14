package uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.submit.domain.PermitIssuanceApplicationSubmittedRequestActionPayload;

class PermitSubmitMapperTest {

    private PermitSubmitMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(PermitSubmitMapper.class);
    }

    @Test
    void toPermitIssuanceApplicationSubmittedRequestActionPayload() {
        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                .quantity(BigDecimal.TEN)
                .build())
            .build();

        InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
                .installationName("name")
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

        Map<UUID, String> permitAttachments = Map.of(
            UUID.randomUUID(), "attachment1",
            UUID.randomUUID(), "attachment2"
        );

        Map<String, List<Boolean>> permitSectionsCompleted = Map.of(
            "installationCategory", List.of(true),
            "installationOperatorDetails", List.of(true)
        );

        PermitIssuanceApplicationSubmitRequestTaskPayload permitIssuanceApplicationSubmitRequestTaskPayload = PermitIssuanceApplicationSubmitRequestTaskPayload
            .builder()
            .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMIT_PAYLOAD)
            .permitType(PermitType.GHGE)
            .permit(permit)
            .installationOperatorDetails(installationOperatorDetails)
            .permitAttachments(permitAttachments)
            .permitSectionsCompleted(permitSectionsCompleted)
            .build();


        PermitIssuanceApplicationSubmittedRequestActionPayload permitApplySubmittedPayload =
            mapper.toPermitIssuanceApplicationSubmittedRequestActionPayload(permitIssuanceApplicationSubmitRequestTaskPayload);

        assertThat(permitApplySubmittedPayload.getPayloadType()).isEqualTo(RequestActionPayloadType.PERMIT_ISSUANCE_APPLICATION_SUBMITTED_PAYLOAD);
        assertEquals(PermitType.GHGE, permitApplySubmittedPayload.getPermitType());
        assertThat(permitApplySubmittedPayload.getPermit()).isEqualTo(permit);
        assertNull(permitApplySubmittedPayload.getInstallationOperatorDetails());
        assertThat(permitApplySubmittedPayload.getPermitAttachments()).isEmpty();
        assertThat(permitApplySubmittedPayload.getPermitSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(permitSectionsCompleted);
    }
}