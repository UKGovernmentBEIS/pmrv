package uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceDeterminateable;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceRejectDetermination;

class PermitMapperTest {

    private final PermitMapper permitMapper = Mappers.getMapper(PermitMapper.class);

    @Test
    void toPermitContainer_fromPermitIssuanceRequestPayload_withInstallationOperatorDetails() {
        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder().quantity(BigDecimal.TEN).build())
            .build();
        PermitIssuanceDeterminateable determination = PermitIssuanceRejectDetermination.builder()
            .type(DeterminationType.REJECTED)
            .officialNotice("notice")
            .build();
        InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
            .installationName("installation name")
            .siteName("site name")
            .build();
        PermitIssuanceRequestPayload permitIssuanceRequestPayload = PermitIssuanceRequestPayload.builder()
            .permitType(PermitType.GHGE)
            .permit(permit)
            .determination(determination)
            .build();

        PermitContainer permitContainer = permitMapper.toPermitContainer(permitIssuanceRequestPayload, installationOperatorDetails);

        assertEquals(PermitType.GHGE, permitContainer.getPermitType());
        assertEquals(permit, permitContainer.getPermit());
        assertEquals(installationOperatorDetails, permitContainer.getInstallationOperatorDetails());
        assertNull(permitContainer.getActivationDate());
        assertThat(permitContainer.getAnnualEmissionsTargets()).isEmpty();
    }

    @Test
    void toPermitContainer_fromPermitIssuanceApplicationReviewRequestTaskPayload_determination_rejected() {
        PermitType permitType = PermitType.GHGE;
        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder().quantity(BigDecimal.TEN).build())
            .build();
        InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
            .installationName("installation name")
            .siteName("site name")
            .build();
        PermitIssuanceApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload =
            PermitIssuanceApplicationReviewRequestTaskPayload.builder()
                .permitType(permitType)
                .permit(permit)
                .installationOperatorDetails(installationOperatorDetails)
                .determination(PermitIssuanceRejectDetermination.builder().type(DeterminationType.REJECTED).build())
                .build();

        PermitContainer permitContainer = permitMapper.toPermitContainer(applicationReviewRequestTaskPayload);

        assertEquals(permitType, permitContainer.getPermitType());
        assertEquals(permit, permitContainer.getPermit());
        assertEquals(installationOperatorDetails, permitContainer.getInstallationOperatorDetails());
        assertNull(permitContainer.getActivationDate());
        assertThat(permitContainer.getAnnualEmissionsTargets()).isEmpty();
    }

}
