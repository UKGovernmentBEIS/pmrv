package uk.gov.pmrv.api.workflow.request.flow.permitvariation.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.abbreviations.Abbreviations;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationMonitoringApproach;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.ReviewDecisionType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.PermitReviewGroup;
import uk.gov.pmrv.api.workflow.request.flow.common.service.permit.PermitReviewService;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationDetails;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationGrantDetermination;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationReviewDecision;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveApplicationReviewRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveReviewDeterminationRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitvariation.domain.PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload;

@ExtendWith(MockitoExtension.class)
class PermitVariationReviewServiceTest {

    @InjectMocks
    private PermitVariationReviewService cut;

    @Mock
    private RequestTaskService requestTaskService;
    
    @Mock
    private PermitReviewService permitReviewService;
    
    @Test
    void savePermitVariation() {
        PermitVariationSaveApplicationReviewRequestTaskActionPayload taskActionPayload = PermitVariationSaveApplicationReviewRequestTaskActionPayload.builder()
            .permitVariationDetails(PermitVariationDetails.builder()
                .reason("reason")
                .build())
            .permitVariationDetailsCompleted(Boolean.TRUE)
            .permit(Permit.builder()
                .abbreviations(Abbreviations.builder().exist(true).build())
                .monitoringApproaches(MonitoringApproaches.builder()
						.monitoringApproaches(Map.of(
								MonitoringApproachType.CALCULATION, CalculationMonitoringApproach.builder().build()
								))
						.build())
                .build())
            .permitSectionsCompleted(Map.of("section1", List.of(true, false)))
            .reviewSectionsCompleted(Map.of("section2", true))
            .permitVariationDetailsReviewCompleted(Boolean.TRUE)
            .build();

        PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder()
        		.permit(Permit.builder().build())
        		.build();
        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
            .build();

        cut.savePermitVariation(taskActionPayload, requestTask);
        
        verify(permitReviewService, times(1)).cleanUpDeprecatedReviewGroupDecisions(taskPayload, Set.of(MonitoringApproachType.CALCULATION));
        verify(permitReviewService, times(1)).resetDeterminationIfNotDeemedWithdrawn(taskPayload);

        assertThat(taskPayload.getPermitVariationDetails()).isEqualTo(taskActionPayload.getPermitVariationDetails());
        assertThat(taskPayload.getPermitVariationDetailsCompleted()).isEqualTo(taskActionPayload.getPermitVariationDetailsCompleted());
        assertThat(taskPayload.getPermit()).isEqualTo(taskActionPayload.getPermit());
        assertThat(taskPayload.getPermitSectionsCompleted()).isEqualTo(taskActionPayload.getPermitSectionsCompleted());
        assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
        assertThat(taskPayload.getPermitVariationDetailsReviewCompleted()).isTrue();
        verify(requestTaskService, times(1)).saveRequestTask(requestTask);
    }

    @Test
    void saveReviewGroupDecision() {
        PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload taskActionPayload = PermitVariationSaveReviewGroupDecisionRequestTaskActionPayload.builder()
            .decision(
                PermitVariationReviewDecision.builder()
                    .type(ReviewDecisionType.ACCEPTED)
                    .notes("A note")
                    .variationScheduleItems(List.of("A change required",
                        "A second change required"))
                    .build()
            )
            .group(PermitReviewGroup.CONFIDENTIALITY_STATEMENT)
            .reviewSectionsCompleted(Map.of("section", true))
            .build();

        PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
            .build();

        cut.saveReviewGroupDecision(taskActionPayload, requestTask);
        
        verify(permitReviewService, times(1)).resetDeterminationIfNotValidWithDecisions(taskPayload, RequestType.PERMIT_VARIATION);

        assertThat(taskPayload.getPermitVariationDetailsReviewDecision()).isNull();
        assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
        assertThat(taskPayload.getReviewGroupDecisions().get(taskActionPayload.getGroup())).isEqualTo(taskActionPayload.getDecision());
    }

    @Test
    void saveDetailsReviewGroupDecision() {
        PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload taskActionPayload = PermitVariationSaveDetailsReviewGroupDecisionRequestTaskActionPayload.builder()
            .decision(
                PermitVariationReviewDecision.builder()
                    .type(ReviewDecisionType.ACCEPTED)
                    .notes("A note")
                    .variationScheduleItems(List.of("A change required",
                        "A second change required"))
                    .build()
            )
            .reviewSectionsCompleted(Map.of("section", true))
            .permitVariationDetailsReviewCompleted(Boolean.TRUE)
            .build();

        PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
            .build();

        cut.saveDetailsReviewGroupDecision(taskActionPayload, requestTask);
        
        verify(permitReviewService, times(1)).resetDeterminationIfNotValidWithDecisions(taskPayload, RequestType.PERMIT_VARIATION);

        assertThat(taskPayload.getPermitVariationDetailsReviewDecision()).isEqualTo(taskActionPayload.getDecision());
        assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
        assertThat(taskPayload.getPermitVariationDetailsReviewCompleted()).isTrue();
    }
    
    @Test
    void saveDetermination() {
    	PermitVariationSaveReviewDeterminationRequestTaskActionPayload taskActionPayload = PermitVariationSaveReviewDeterminationRequestTaskActionPayload.builder()
            .determination(
            		PermitVariationGrantDetermination.builder()
                    .type(DeterminationType.GRANTED)
                    .logChanges("log changes")
                    .activationDate(LocalDate.now().plusDays(10))
                    .reason("reason")
                    .build())
            .reviewSectionsCompleted(Map.of("section", true))
            .build();

        PermitVariationApplicationReviewRequestTaskPayload taskPayload = PermitVariationApplicationReviewRequestTaskPayload.builder().build();
        RequestTask requestTask = RequestTask.builder()
            .payload(taskPayload)
            .request(Request.builder().type(RequestType.PERMIT_VARIATION).build())
            .build();

        cut.saveDetermination(taskActionPayload, requestTask);

        assertThat(taskPayload.getDetermination()).isEqualTo(taskActionPayload.getDetermination());
        assertThat(taskPayload.getReviewSectionsCompleted()).isEqualTo(taskActionPayload.getReviewSectionsCompleted());
    }
}
