package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitType;
import uk.gov.pmrv.api.permit.domain.estimatedannualemissions.EstimatedAnnualEmissions;
import uk.gov.pmrv.api.permit.domain.installationdesc.InstallationDescription;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.pfc.PFCMonitoringApproach;
import uk.gov.pmrv.api.permit.validation.PermitValidatorService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewDecisionRequiredChange;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.service.permit.PermitReviewService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationAmendsSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceDeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceRejectDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveApplicationAmendRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceReviewServiceTest {

    private final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder().installationName("sample name").build();
    private final Long accountId = 1L;

    @InjectMocks
    private PermitIssuanceReviewService cut;
    
    @Mock
    private RequestService requestService;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private PermitReviewService permitReviewService;

    @Mock
    private PermitValidatorService permitValidatorService;

    @Test
    void savePermit() {
        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                .quantity(BigDecimal.TEN)
                .build())
            .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(Map.of(
            		MonitoringApproachType.PFC, PFCMonitoringApproach.builder().build()
            		)).build())
            .build();

        PermitIssuanceApplicationReviewRequestTaskPayload permitIssuanceApplicationReviewRequestTaskPayload =
            PermitIssuanceApplicationReviewRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD)
                .installationOperatorDetails(InstallationOperatorDetails.builder().installationName("instName").build())
                .reviewSectionsCompleted(Map.of("entry", true))
                .permitType(PermitType.GHGE)
                .permit(permit)
                .reviewGroupDecisions(Map.of(
                		PermitReviewGroup.INSTALLATION_DETAILS, PermitIssuanceReviewDecision.builder().build(),
                		PermitReviewGroup.PFC, PermitIssuanceReviewDecision.builder().build()
                		))
                .determination(PermitIssuanceRejectDetermination.builder().type(DeterminationType.REJECTED)
                    .reason("reason").officialNotice("officialNotice").build())
                .build();
        
    	RequestTask requestTask = RequestTask.builder().payload(permitIssuanceApplicationReviewRequestTaskPayload).build();
    	
    	Permit requestActionPermit = Permit.builder()
    			.installationDescription(InstallationDescription.builder().mainActivitiesDesc("mainAct").siteDescription("siteDescription").build())
                .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(Map.of(
                		MonitoringApproachType.CALCULATION, CalculationMonitoringApproach.builder().build()
                		)).build())
                .build();
    	
    	PermitIssuanceSaveApplicationReviewRequestTaskActionPayload permitReviewRequestTaskActionPayload =
                PermitIssuanceSaveApplicationReviewRequestTaskActionPayload.builder()
                    .payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW_PAYLOAD)
                    .permitType(PermitType.HSE)
                    .permit(requestActionPermit)
                    .permitSectionsCompleted(Map.of(InstallationOperatorDetails.class.getName(), List.of(true)))
                    .reviewSectionsCompleted(Map.of(InstallationOperatorDetails.class.getName(), true))
                    .build();
    	
    	
        cut.savePermit(permitReviewRequestTaskActionPayload, requestTask);
        
        //verify
        verify(permitReviewService, times(1)).cleanUpDeprecatedReviewGroupDecisions(permitIssuanceApplicationReviewRequestTaskPayload, Set.of(MonitoringApproachType.CALCULATION));
        verify(permitReviewService, times(1)).resetDeterminationIfNotDeemedWithdrawn(permitIssuanceApplicationReviewRequestTaskPayload);

        ArgumentCaptor<RequestTask> requestTaskCaptor = ArgumentCaptor.forClass(RequestTask.class);
        verify(requestTaskService, times(1)).saveRequestTask(requestTaskCaptor.capture());
        RequestTask updatedRequestTask = requestTaskCaptor.getValue();
        assertThat(updatedRequestTask.getPayload()).isInstanceOf(PermitIssuanceApplicationReviewRequestTaskPayload.class);

        PermitIssuanceApplicationReviewRequestTaskPayload
            payloadSaved = (PermitIssuanceApplicationReviewRequestTaskPayload) updatedRequestTask.getPayload();
        assertEquals(PermitType.HSE, payloadSaved.getPermitType());
        assertThat(payloadSaved.getPermit().getInstallationDescription().getMainActivitiesDesc())
            .isEqualTo(permitReviewRequestTaskActionPayload.getPermit().getInstallationDescription().getMainActivitiesDesc());
        assertThat(payloadSaved.getPermit().getInstallationDescription().getSiteDescription())
            .isEqualTo(permitReviewRequestTaskActionPayload.getPermit().getInstallationDescription().getSiteDescription());
        assertThat(payloadSaved.getInstallationOperatorDetails().getInstallationName())
            .isEqualTo(permitIssuanceApplicationReviewRequestTaskPayload.getInstallationOperatorDetails().getInstallationName());
        assertThat(payloadSaved.getPermitSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(permitReviewRequestTaskActionPayload.getPermitSectionsCompleted());
        assertThat(payloadSaved.getReviewSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(permitReviewRequestTaskActionPayload.getReviewSectionsCompleted());
    }

    @Test
    void saveReviewGroupDecision() {
        final PermitIssuanceRejectDetermination determination = PermitIssuanceRejectDetermination.builder().type(DeterminationType.REJECTED)
                .reason("reason").officialNotice("officialNotice").build();
        final PermitIssuanceApplicationReviewRequestTaskPayload permitIssuanceApplicationReviewRequestTaskPayload =
            PermitIssuanceApplicationReviewRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD)
                .installationOperatorDetails(InstallationOperatorDetails.builder().installationName("instName").build())
                .reviewSectionsCompleted(Map.of("entry", true))
                .determination(determination)
                .build();

        final RequestTask requestTask = RequestTask.builder()
        		.payload(permitIssuanceApplicationReviewRequestTaskPayload)
        		.request(Request.builder().type(RequestType.PERMIT_ISSUANCE).build())
        		.build();

        final UUID file = UUID.randomUUID();

        final PermitIssuanceReviewDecision decision = PermitIssuanceReviewDecision.builder()
            .type(ReviewDecisionType.REJECTED)
            .requiredChange(new PermitReviewDecisionRequiredChange("changesRequired", Set.of(file)))
            .notes("notes")
            .build();

        final PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload payload =
            PermitIssuanceSaveReviewGroupDecisionRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION_PAYLOAD)
                .group(PermitReviewGroup.INSTALLATION_DETAILS)
                .decision(decision)
                .build();

        cut.saveReviewGroupDecision(payload, requestTask);

        verify(permitReviewService, times(1)).resetDeterminationIfNotValidWithDecisions(permitIssuanceApplicationReviewRequestTaskPayload, RequestType.PERMIT_ISSUANCE);
        final ArgumentCaptor<RequestTask> requestTaskCaptor = ArgumentCaptor.forClass(RequestTask.class);
        verify(requestTaskService, times(1)).saveRequestTask(requestTaskCaptor.capture());

        final RequestTask updatedRequestTask = requestTaskCaptor.getValue();
        assertThat(updatedRequestTask.getPayload()).isInstanceOf(PermitIssuanceApplicationReviewRequestTaskPayload.class);

        final PermitIssuanceApplicationReviewRequestTaskPayload
            payloadSaved = (PermitIssuanceApplicationReviewRequestTaskPayload) updatedRequestTask.getPayload();

        assertThat(payloadSaved.getReviewGroupDecisions()).containsEntry(PermitReviewGroup.INSTALLATION_DETAILS, decision);
        assertThat(payloadSaved.getReviewSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(payload.getReviewSectionsCompleted());
    }

    @Test
    void savePermitDecisionNotification() {

        final PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload actionPayload =
            PermitIssuanceNotifyOperatorForDecisionRequestTaskActionPayload
                .builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION_PAYLOAD)
                .permitDecisionNotification(DecisionNotification.builder()
                    .operators(Set.of("operator1"))
                    .externalContacts(Set.of(10L))
                    .signatory("signatory").build())
                .build();

        final Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                .quantity(BigDecimal.TEN)
                .build())
            .build();

        final Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecisions = new EnumMap<>(PermitReviewGroup.class);
        reviewGroupDecisions.put(PermitReviewGroup.INSTALLATION_DETAILS, PermitIssuanceReviewDecision.builder().build());
        reviewGroupDecisions.put(PermitReviewGroup.CALCULATION, PermitIssuanceReviewDecision.builder().build());
        reviewGroupDecisions.put(PermitReviewGroup.FALLBACK, PermitIssuanceReviewDecision.builder().build());
        reviewGroupDecisions.put(PermitReviewGroup.PFC, PermitIssuanceReviewDecision.builder().build());

        final PermitIssuanceApplicationReviewRequestTaskPayload taskPayload =
            PermitIssuanceApplicationReviewRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD)
                .reviewSectionsCompleted(Map.of("entry", true))
                .permit(permit)
                .reviewGroupDecisions(reviewGroupDecisions)
                .determination(PermitIssuanceRejectDetermination.builder().type(DeterminationType.REJECTED)
                    .reason("reason").officialNotice("officialNotice").build())
                .build();

        final RequestTask requestTask = RequestTask.builder()
            .request(Request.builder().accountId(accountId).payload(PermitIssuanceRequestPayload.builder().build()).build())
            .payload(taskPayload)
            .build();

        final PmrvUser pmrvUser = PmrvUser.builder().userId("userId").roleType(RoleType.OPERATOR).build();

        cut.savePermitDecisionNotification(requestTask, actionPayload.getPermitDecisionNotification(), pmrvUser);

        final ArgumentCaptor<Request> requestTaskCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestTaskCaptor.capture());

        final Request updatedRequestTask = requestTaskCaptor.getValue();
        assertThat(updatedRequestTask.getPayload()).isInstanceOf(PermitIssuanceRequestPayload.class);

        final PermitIssuanceRequestPayload payloadSaved = (PermitIssuanceRequestPayload) updatedRequestTask.getPayload();

        assertThat(payloadSaved.getReviewGroupDecisions()).isEqualTo(reviewGroupDecisions);

        assertThat(payloadSaved.getPermit()).isEqualTo(taskPayload.getPermit());
        assertThat(payloadSaved.getPermitSectionsCompleted()).isEqualTo(taskPayload.getPermitSectionsCompleted());
        assertThat(payloadSaved.getPermitAttachments()).isEqualTo(taskPayload.getPermitAttachments());
        assertThat(payloadSaved.getReviewSectionsCompleted()).isEqualTo(taskPayload.getReviewSectionsCompleted());
        assertThat(payloadSaved.getReviewGroupDecisions()).isEqualTo(taskPayload.getReviewGroupDecisions());
        assertThat(payloadSaved.getReviewAttachments()).isEqualTo(taskPayload.getReviewAttachments());
        assertThat(payloadSaved.getDetermination()).isEqualTo(taskPayload.getDetermination());
        assertThat(payloadSaved.getPermitDecisionNotification()).isEqualTo(actionPayload.getPermitDecisionNotification());
        assertThat(payloadSaved.getRegulatorReviewer()).isEqualTo(pmrvUser.getUserId());
    }

    @Test
    void saveRequestPeerReviewAction() {
        String selectedPeerReviewer = "selectedPeerReviewer";
        String user = "user";
        PmrvUser reviewer = PmrvUser.builder().userId(user).build();

        PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
            .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
            .build();
        Request request = Request.builder()
            .type(RequestType.PERMIT_ISSUANCE)
            .accountId(accountId)
            .payload(requestPayload)
            .build();
        PermitIssuanceApplicationReviewRequestTaskPayload requestTaskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_REVIEW_PAYLOAD)
            .permit(Permit.builder()
                .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                    .quantity(BigDecimal.TEN)
                    .build())
                .build())
            .determination(PermitIssuanceDeemedWithdrawnDetermination.builder().type(DeterminationType.DEEMED_WITHDRAWN).reason("reason").build())
            .reviewGroupDecisions(Map.of(PermitReviewGroup.INSTALLATION_DETAILS, PermitIssuanceReviewDecision.builder().build()))
            .permitSectionsCompleted(Map.of("section1", List.of(true)))
            .reviewSectionsCompleted(Map.of("section1", true))
            .build();
        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_REVIEW)
            .request(request)
            .payload(requestTaskPayload)
            .build();

        cut.saveRequestPeerReviewAction(requestTask, selectedPeerReviewer, reviewer);

        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestCaptor.capture());

        final Request updatedRequest = requestCaptor.getValue();
        assertThat(updatedRequest.getPayload()).isInstanceOf(PermitIssuanceRequestPayload.class);

        final PermitIssuanceRequestPayload updatedPayload = (PermitIssuanceRequestPayload) updatedRequest.getPayload();

        assertEquals(reviewer.getUserId(), updatedPayload.getRegulatorReviewer());
        assertEquals(selectedPeerReviewer, updatedPayload.getRegulatorPeerReviewer());
        assertThat(updatedPayload.getPermit()).isEqualTo(requestTaskPayload.getPermit());
        assertThat(updatedPayload.getPermitSectionsCompleted()).isEqualTo(requestTaskPayload.getPermitSectionsCompleted());
        assertThat(updatedPayload.getReviewSectionsCompleted()).isEqualTo(requestTaskPayload.getReviewSectionsCompleted());
        assertThat(updatedPayload.getReviewGroupDecisions()).isEqualTo(requestTaskPayload.getReviewGroupDecisions());
        assertThat(updatedPayload.getDetermination()).isEqualTo(requestTaskPayload.getDetermination());
        assertThat(updatedPayload.getReviewAttachments()).isEmpty();
        assertThat(updatedPayload.getPermitAttachments()).isEmpty();
    }

    @Test
    void amendPermit() {
        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                .quantity(BigDecimal.TEN)
                .build())
            .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(Map.of(
            		MonitoringApproachType.PFC, PFCMonitoringApproach.builder().build()
            		)).build())
            .build();

        PermitIssuanceApplicationAmendsSubmitRequestTaskPayload permitIssuanceApplicationAmendsSubmitRequestTaskPayload =
            PermitIssuanceApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .installationOperatorDetails(InstallationOperatorDetails.builder().installationName("instName").build())
                .reviewSectionsCompleted(Map.of("entry", true))
                .permitType(PermitType.HSE)
                .permit(permit)
                .reviewGroupDecisions(Map.of(
                		PermitReviewGroup.INSTALLATION_DETAILS, PermitIssuanceReviewDecision.builder().build(),
                		PermitReviewGroup.PFC, PermitIssuanceReviewDecision.builder().build()
                		))
                .build();

        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT)
            .payload(permitIssuanceApplicationAmendsSubmitRequestTaskPayload)
            .build();

        Permit requestActionPermit = Permit.builder()
            .installationDescription(InstallationDescription.builder().mainActivitiesDesc("mainAct").siteDescription("siteDescription").build())
            .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(Map.of(
            		MonitoringApproachType.CALCULATION, CalculationMonitoringApproach.builder().build()
            		)).build())
            .build();

        PermitIssuanceSaveApplicationAmendRequestTaskActionPayload permitIssuanceAmendRequestTaskActionPayload =
            PermitIssuanceSaveApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND_PAYLOAD)
                .permitType(PermitType.GHGE)
                .permit(requestActionPermit)
                .permitSectionsCompleted(Map.of(InstallationOperatorDetails.class.getName(), List.of(true)))
                .reviewSectionsCompleted(Map.of(InstallationOperatorDetails.class.getName(), true))
                .build();

        cut.amendPermit(permitIssuanceAmendRequestTaskActionPayload, requestTask);

        //verify
        verify(permitReviewService, times(1)).cleanUpDeprecatedReviewGroupDecisions(permitIssuanceApplicationAmendsSubmitRequestTaskPayload, Set.of(MonitoringApproachType.CALCULATION));
        ArgumentCaptor<RequestTask> requestTaskCaptor = ArgumentCaptor.forClass(RequestTask.class);
        verify(requestTaskService, times(1)).saveRequestTask(requestTaskCaptor.capture());
        RequestTask updatedRequestTask = requestTaskCaptor.getValue();
        assertThat(updatedRequestTask.getPayload()).isInstanceOf(PermitIssuanceApplicationAmendsSubmitRequestTaskPayload.class);

        PermitIssuanceApplicationAmendsSubmitRequestTaskPayload
            payloadSaved = (PermitIssuanceApplicationAmendsSubmitRequestTaskPayload) updatedRequestTask.getPayload();
        assertEquals(PermitType.GHGE, payloadSaved.getPermitType());
        assertThat(payloadSaved.getPermit().getInstallationDescription().getMainActivitiesDesc())
            .isEqualTo(permitIssuanceAmendRequestTaskActionPayload.getPermit().getInstallationDescription().getMainActivitiesDesc());
        assertThat(payloadSaved.getPermit().getInstallationDescription().getSiteDescription())
            .isEqualTo(permitIssuanceAmendRequestTaskActionPayload.getPermit().getInstallationDescription().getSiteDescription());
        assertThat(payloadSaved.getInstallationOperatorDetails().getInstallationName())
            .isEqualTo(permitIssuanceApplicationAmendsSubmitRequestTaskPayload.getInstallationOperatorDetails().getInstallationName());
        assertThat(payloadSaved.getPermitSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(permitIssuanceAmendRequestTaskActionPayload.getPermitSectionsCompleted());
        assertThat(payloadSaved.getReviewSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(permitIssuanceAmendRequestTaskActionPayload.getReviewSectionsCompleted());
    }

    @Test
    void submitAmendedPermit() {
        Permit permit = Permit.builder()
            .estimatedAnnualEmissions(EstimatedAnnualEmissions.builder()
                .quantity(BigDecimal.TEN)
                .build())
            .build();

        Map<UUID, String> permitAttachments = Map.of(
            UUID.randomUUID(), "file1",
            UUID.randomUUID(), "file2"
        );

        PermitIssuanceApplicationAmendsSubmitRequestTaskPayload permitIssuanceApplicationAmendsSubmitRequestTaskPayload =
            PermitIssuanceApplicationAmendsSubmitRequestTaskPayload
                .builder()
                .payloadType(RequestTaskPayloadType.PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT_PAYLOAD)
                .installationOperatorDetails(installationOperatorDetails)
                .reviewSectionsCompleted(Map.of("entry", true))
                .permit(permit)
                .permitAttachments(permitAttachments)
                .permitSectionsCompleted(Map.of(
                    "installationCategory", List.of(true),
                    "installationOperatorDetails", List.of(true),
                    "amendDetails", List.of(true)
                ))
                .build();

        RequestTask requestTask = RequestTask.builder()
            .type(RequestTaskType.PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT)
            .payload(permitIssuanceApplicationAmendsSubmitRequestTaskPayload)
            .request(Request.builder()
                .accountId(accountId)
                .payload(PermitIssuanceRequestPayload.builder().payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD).build())
                .build())
            .build();

        PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload submitApplicationAmendRequestTaskActionPayload =
            PermitIssuanceSubmitApplicationAmendRequestTaskActionPayload.builder()
                .payloadType(RequestTaskActionPayloadType.PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND_PAYLOAD)
                .permitSectionsCompleted(Map.of(
                    "installationCategory", List.of(true),
                    "installationOperatorDetails", List.of(true)
                ))
                .build();

        PermitContainer permitContainer = PermitContainer.builder()
            .permit(permit)
            .installationOperatorDetails(installationOperatorDetails)
            .permitAttachments(permitAttachments)
            .build();

        doNothing().when(permitValidatorService).validatePermit(permitContainer);

        cut.submitAmendedPermit(submitApplicationAmendRequestTaskActionPayload, requestTask);

        //verify
        ArgumentCaptor<Request> requestCaptor = ArgumentCaptor.forClass(Request.class);
        verify(requestService, times(1)).saveRequest(requestCaptor.capture());
        Request updatedRequest = requestCaptor.getValue();
        assertThat(updatedRequest.getPayload()).isInstanceOf(PermitIssuanceRequestPayload.class);

        PermitIssuanceRequestPayload
            payloadSaved = (PermitIssuanceRequestPayload) updatedRequest.getPayload();
        assertThat(payloadSaved.getPermit()).isEqualTo(permitIssuanceApplicationAmendsSubmitRequestTaskPayload.getPermit());
        assertThat(payloadSaved.getPermitAttachments())
            .containsExactlyInAnyOrderEntriesOf(permitIssuanceApplicationAmendsSubmitRequestTaskPayload.getPermitAttachments());
        assertThat(payloadSaved.getReviewSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(permitIssuanceApplicationAmendsSubmitRequestTaskPayload.getReviewSectionsCompleted());
        assertThat(payloadSaved.getPermitSectionsCompleted())
            .containsExactlyInAnyOrderEntriesOf(submitApplicationAmendRequestTaskActionPayload.getPermitSectionsCompleted());
    }
}