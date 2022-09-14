package uk.gov.pmrv.api.workflow.request.flow.common.service.permit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.confidentialitystatement.ConfidentialityStatement;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.fallback.FallbackMonitoringApproach;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.validation.PermitReviewDeterminationAndDecisionsValidatorService;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceDeemedWithdrawnDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceReviewDecision;

@ExtendWith(MockitoExtension.class)
class PermitReviewServiceTest {
	
	@InjectMocks
    private PermitReviewService cut;
	
	@Mock
	private PermitReviewDeterminationAndDecisionsValidatorService permitReviewDeterminationAndDecisionsValidatorService;

	@Test
	void cleanUpDeprecatedReviewGroupDecisions() {
		Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecisions = new HashMap<>();
		reviewGroupDecisions.put(PermitReviewGroup.CONFIDENTIALITY_STATEMENT, PermitIssuanceReviewDecision.builder().build());
		reviewGroupDecisions.put(PermitReviewGroup.FALLBACK, PermitIssuanceReviewDecision.builder().build());
		reviewGroupDecisions.put(PermitReviewGroup.CALCULATION, PermitIssuanceReviewDecision.builder().build());
		
		PermitIssuanceApplicationReviewRequestTaskPayload taskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
				.permit(Permit.builder()
						.confidentialityStatement(ConfidentialityStatement.builder().build())
						.monitoringApproaches(MonitoringApproaches.builder()
								.monitoringApproaches(Map.of(
										MonitoringApproachType.FALLBACK, FallbackMonitoringApproach.builder().build(),
										MonitoringApproachType.CALCULATION, CalculationMonitoringApproach.builder().build()
										))
								.build())
						.build())
				.reviewGroupDecisions(reviewGroupDecisions)
				.build();
		
		Set<MonitoringApproachType> newMonitoringApproaches = Set.of(MonitoringApproachType.FALLBACK, MonitoringApproachType.PFC);
		
		cut.cleanUpDeprecatedReviewGroupDecisions(taskPayload, newMonitoringApproaches);
		
		assertThat(taskPayload.getReviewGroupDecisions().keySet())
				.containsExactlyInAnyOrder(PermitReviewGroup.CONFIDENTIALITY_STATEMENT, PermitReviewGroup.FALLBACK);
		
	}
	
	@Test
	void cleanUpDeprecatedReviewGroupDecisions_no_removing() {
		Map<PermitReviewGroup, PermitIssuanceReviewDecision> reviewGroupDecisions = new HashMap<>();
		reviewGroupDecisions.put(PermitReviewGroup.CONFIDENTIALITY_STATEMENT, PermitIssuanceReviewDecision.builder().build());
		reviewGroupDecisions.put(PermitReviewGroup.FALLBACK, PermitIssuanceReviewDecision.builder().build());
		reviewGroupDecisions.put(PermitReviewGroup.CALCULATION, PermitIssuanceReviewDecision.builder().build());
		
		PermitIssuanceApplicationReviewRequestTaskPayload taskPayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
				.permit(Permit.builder()
						.confidentialityStatement(ConfidentialityStatement.builder().build())
						.monitoringApproaches(MonitoringApproaches.builder()
								.monitoringApproaches(Map.of(
										MonitoringApproachType.FALLBACK, FallbackMonitoringApproach.builder().build(),
										MonitoringApproachType.CALCULATION, CalculationMonitoringApproach.builder().build()
										))
								.build())
						.build())
				.reviewGroupDecisions(reviewGroupDecisions)
				.build();
		
		Set<MonitoringApproachType> newMonitoringApproaches = Set.of(MonitoringApproachType.FALLBACK, MonitoringApproachType.CALCULATION, MonitoringApproachType.PFC);
		
		cut.cleanUpDeprecatedReviewGroupDecisions(taskPayload, newMonitoringApproaches);
		
		assertThat(taskPayload.getReviewGroupDecisions().keySet())
				.containsExactlyInAnyOrder(PermitReviewGroup.CONFIDENTIALITY_STATEMENT, PermitReviewGroup.FALLBACK, PermitReviewGroup.CALCULATION);
	}
	
	@Test
	void resetDeterminationIfNotDeemedWithdrawn() {
		PermitIssuanceApplicationReviewRequestTaskPayload determinateablePayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
				.determination(PermitIssuanceGrantDetermination.builder()
						.type(DeterminationType.GRANTED)
						.activationDate(LocalDate.now())
						.build())
				.build();
		
		cut.resetDeterminationIfNotDeemedWithdrawn(determinateablePayload);
		
		assertThat(determinateablePayload.getDetermination()).isNull();
	}
	
	@Test
	void resetDeterminationIfNotDeemedWithdrawn_deemed_should_not_reset() {
		PermitIssuanceApplicationReviewRequestTaskPayload determinateablePayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
				.determination(PermitIssuanceDeemedWithdrawnDetermination.builder()
						.type(DeterminationType.DEEMED_WITHDRAWN)
						.build())
				.build();
		
		cut.resetDeterminationIfNotDeemedWithdrawn(determinateablePayload);
		
		assertThat(determinateablePayload.getDetermination()).isNotNull();
	}
	
	@Test
	void resetDeterminationIfNotValidWithDecisions_invalid() {
		PermitIssuanceGrantDetermination determination = PermitIssuanceGrantDetermination.builder()
				.type(DeterminationType.GRANTED).build();
		PermitIssuanceApplicationReviewRequestTaskPayload determinateablePayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
				.determination(determination)
				.build();
		
		when(permitReviewDeterminationAndDecisionsValidatorService.isDeterminationAndDecisionsValid(determination, determinateablePayload, RequestType.PERMIT_ISSUANCE))
			.thenReturn(false);
		
		cut.resetDeterminationIfNotValidWithDecisions(determinateablePayload, RequestType.PERMIT_ISSUANCE);
		
		assertThat(determinateablePayload.getDetermination()).isNull();
		verify(permitReviewDeterminationAndDecisionsValidatorService, times(1)).isDeterminationAndDecisionsValid(determination, determinateablePayload, RequestType.PERMIT_ISSUANCE);
	}
	
	@Test
	void resetDeterminationIfNotValidWithDecisions_valid() {
		PermitIssuanceGrantDetermination determination = PermitIssuanceGrantDetermination.builder()
				.type(DeterminationType.GRANTED).build();
		PermitIssuanceApplicationReviewRequestTaskPayload determinateablePayload = PermitIssuanceApplicationReviewRequestTaskPayload.builder()
				.determination(determination)
				.build();
		
		when(permitReviewDeterminationAndDecisionsValidatorService.isDeterminationAndDecisionsValid(determination, determinateablePayload, RequestType.PERMIT_ISSUANCE))
			.thenReturn(true);
		
		cut.resetDeterminationIfNotValidWithDecisions(determinateablePayload, RequestType.PERMIT_ISSUANCE);
		
		assertThat(determinateablePayload.getDetermination()).isNotNull();
		verify(permitReviewDeterminationAndDecisionsValidatorService, times(1)).isDeterminationAndDecisionsValid(determination, determinateablePayload, RequestType.PERMIT_ISSUANCE);
	}
	
}
