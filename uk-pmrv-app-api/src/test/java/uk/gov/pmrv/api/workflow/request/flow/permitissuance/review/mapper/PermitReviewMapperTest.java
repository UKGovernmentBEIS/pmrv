package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_PAYLOAD;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceDeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceGrantDetermination;

class PermitReviewMapperTest {

    private PermitReviewMapper mapper;

    @BeforeEach
    void init() {
        mapper = Mappers.getMapper(PermitReviewMapper.class);
    }

    @Test
    void toPermitIssuanceApplicationPeerReviewRequestTaskPayload() {
        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                .quantity(BigDecimal.TEN)
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

        PermitIssuanceDeemedWithdrawnDetermination permitDetermination = PermitIssuanceDeemedWithdrawnDetermination.builder()
            .reason("some reason")
            .type(DeterminationType.DEEMED_WITHDRAWN)
            .build();

        PermitIssuanceRequestPayload permitIssuanceRequestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .permitType(PermitType.GHGE)
            .permit(permit)
            .permitAttachments(permitAttachments)
            .permitSectionsCompleted(permitSectionsCompleted)
            .determination(permitDetermination)
            .build();

        PermitIssuanceApplicationReviewRequestTaskPayload permitReviewTaskPayload = mapper.toPermitIssuanceApplicationReviewRequestTaskPayload(
            permitIssuanceRequestPayload, PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_PAYLOAD);

        assertThat(permitReviewTaskPayload.getPayloadType()).isEqualTo(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW_PAYLOAD);
        assertEquals(PermitType.GHGE, permitReviewTaskPayload.getPermitType());
        assertThat(permitReviewTaskPayload.getPermit()).isEqualTo(permit);
        assertThat(permitReviewTaskPayload.getInstallationOperatorDetails()).isNull();
        assertThat(permitReviewTaskPayload.getPermitAttachments()).containsExactlyInAnyOrderEntriesOf(permitAttachments);
        assertThat(permitReviewTaskPayload.getPermitSectionsCompleted()).containsExactlyInAnyOrderEntriesOf(permitSectionsCompleted);
        assertThat(permitReviewTaskPayload.getDetermination()).isEqualTo(permitDetermination);
        assertThat(permitReviewTaskPayload.getReviewSectionsCompleted()).isEmpty();
        assertThat(permitReviewTaskPayload.getReviewGroupDecisions()).isEmpty();
        assertThat(permitReviewTaskPayload.getReviewAttachments()).isEmpty();
    }
    
    @Test
    void toPermitContainer_fromPermitIssuanceApplicationReviewRequestTaskPayload_determination_granted() {
        PermitType permitType = PermitType.HSE;
        TreeMap<String, BigDecimal> annualEmissionsTargets = new TreeMap<>();
        annualEmissionsTargets.put("2022", BigDecimal.valueOf(25000.1));
        annualEmissionsTargets.put("2023", BigDecimal.valueOf(25000.9));

        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder().quantity(BigDecimal.TEN).build())
            .build();
        InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
            .installationName("installation name")
            .siteName("site name")
            .build();
        PermitIssuanceGrantDetermination determination = PermitIssuanceGrantDetermination.builder()
            .type(DeterminationType.GRANTED)
            .activationDate(LocalDate.now())
            .annualEmissionsTargets(annualEmissionsTargets)
            .build();
        PermitIssuanceApplicationReviewRequestTaskPayload applicationReviewRequestTaskPayload =
            PermitIssuanceApplicationReviewRequestTaskPayload.builder()
                .permitType(permitType)
                .permit(permit)
                .installationOperatorDetails(installationOperatorDetails)
                .determination(determination)
                .build();

        PermitContainer permitContainer = mapper.toPermitContainer(applicationReviewRequestTaskPayload);

        assertEquals(permitType, permitContainer.getPermitType());
        assertEquals(permit, permitContainer.getPermit());
        assertEquals(installationOperatorDetails, permitContainer.getInstallationOperatorDetails());
        assertEquals(determination.getActivationDate(), permitContainer.getActivationDate());
        assertEquals(annualEmissionsTargets, permitContainer.getAnnualEmissionsTargets());
    }
}