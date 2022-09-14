package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrender;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationGrantedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationReviewRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderApplicationSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderContainer;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationDeemWithdraw;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationGrant;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationReject;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderReviewDeterminationType;

class PermitSurrenderMapperTest {

    private PermitSurrenderMapper mapper = Mappers.getMapper(PermitSurrenderMapper.class);

    @Test
    void toPermitSurrenderContainer() {
        UUID document1 = UUID.randomUUID();
        UUID document2 = UUID.randomUUID();
        
        PermitSurrender permitSurrender = 
                PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).justification("justify").documentsExist(Boolean.TRUE).documents(Set.of(document1, document2)).build();
        
        Map<UUID, String> permitSurrenderAttachments = Map.of(document1, "doc1", document2, "doc2");
        
        PermitSurrenderApplicationSubmitRequestTaskPayload taskPayload = 
                PermitSurrenderApplicationSubmitRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_SUBMIT_PAYLOAD)
                    .permitSurrender(permitSurrender)
                    .permitSurrenderAttachments(permitSurrenderAttachments)
                    .build();
        
        PermitSurrenderContainer container = mapper.toPermitSurrenderContainer(taskPayload);
        
        assertThat(container.getPermitSurrender()).isEqualTo(permitSurrender);
        assertThat(container.getPermitSurrenderAttachments()).isEqualTo(permitSurrenderAttachments);
    }
    
    @Test
    void toApplicationSubmittedRequestActionPayload() {
        UUID document1 = UUID.randomUUID();
        UUID document2 = UUID.randomUUID();
        
        PermitSurrender permitSurrender = 
                PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).justification("justify").documentsExist(Boolean.TRUE).documents(Set.of(document1, document2)).build();
        
        Map<UUID, String> permitSurrenderAttachments = Map.of(document1, "doc1", document2, "doc2");
        
        PermitSurrenderApplicationSubmitRequestTaskPayload taskPayload = 
                PermitSurrenderApplicationSubmitRequestTaskPayload.builder()
                    .payloadType(RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_SUBMIT_PAYLOAD)
                    .permitSurrender(permitSurrender)
                    .permitSurrenderAttachments(permitSurrenderAttachments)
                    .build();
        
        PermitSurrenderApplicationSubmittedRequestActionPayload actionPayload = mapper.toApplicationSubmittedRequestActionPayload(taskPayload);
        
        assertThat(actionPayload.getPermitSurrender()).isEqualTo(permitSurrender);
        assertThat(actionPayload.getPermitSurrenderAttachments()).isEmpty();
    }
    
    @Test
    void toApplicationReviewRequestTaskPayload() {
        UUID document1 = UUID.randomUUID();
        UUID document2 = UUID.randomUUID();
        
        PermitSurrender permitSurrender = 
                PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).justification("justify").documentsExist(Boolean.TRUE).documents(Set.of(document1, document2)).build();
        
        Map<UUID, String> permitSurrenderAttachments = Map.of(document1, "doc1", document2, "doc2");
        
        PermitSurrenderRequestPayload requestPayload = 
                PermitSurrenderRequestPayload.builder()
                    .payloadType(RequestPayloadType.PERMIT_SURRENDER_REQUEST_PAYLOAD)
                    .permitSurrender(permitSurrender)
                    .permitSurrenderAttachments(permitSurrenderAttachments)
                    .build();

        RequestTaskPayloadType requestTaskPayloadType = RequestTaskPayloadType.PERMIT_SURRENDER_APPLICATION_REVIEW_PAYLOAD;
        
        PermitSurrenderApplicationReviewRequestTaskPayload reviewTaskPayload =
            mapper.toApplicationReviewRequestTaskPayload(requestPayload, requestTaskPayloadType);
        
        assertThat(reviewTaskPayload.getPermitSurrender()).isEqualTo(permitSurrender);
        assertThat(reviewTaskPayload.getPermitSurrenderAttachments()).isEqualTo(permitSurrenderAttachments);
        assertThat(reviewTaskPayload.getPayloadType()).isEqualTo(requestTaskPayloadType);
    }
    
    @Test
    void toPermitSurrenderApplicationGrantedRequestActionPayload() {
        UUID document1 = UUID.randomUUID();
        UUID document2 = UUID.randomUUID();
        
        PermitSurrender permitSurrender = 
                PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).justification("justify").documentsExist(Boolean.TRUE).documents(Set.of(document1, document2)).build();
        Map<UUID, String> permitSurrenderAttachments = Map.of(document1, "doc1", document2, "doc2");
        PermitSurrenderReviewDeterminationGrant determination = PermitSurrenderReviewDeterminationGrant.builder()
                .type(PermitSurrenderReviewDeterminationType.GRANTED)
                .stopDate(LocalDate.now())
                .build();
        
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("user1", "user2"))
            .signatory("signatory")
            .build();
        
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
                .permitSurrender(permitSurrender)
                .permitSurrenderAttachments(permitSurrenderAttachments)
                .reviewDetermination(determination)
                .reviewDecisionNotification(decisionNotification)
                .build();
        
        PermitSurrenderApplicationGrantedRequestActionPayload expectedActionPayload = 
                PermitSurrenderApplicationGrantedRequestActionPayload.builder()
                    .payloadType(RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_GRANTED_PAYLOAD)
                    .reviewDetermination(determination)
                    .reviewDecisionNotification(decisionNotification)
                    .build();
        
        PermitSurrenderApplicationGrantedRequestActionPayload actualActionPayload = mapper.toPermitSurrenderApplicationGrantedRequestActionPayload(requestPayload);
        
        assertThat(actualActionPayload).isEqualTo(expectedActionPayload);
    }
    
    @Test
    void toPermitSurrenderApplicationRejectedRequestActionPayload() {
        UUID document1 = UUID.randomUUID();
        UUID document2 = UUID.randomUUID();
        
        PermitSurrender permitSurrender = 
                PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).justification("justify").documentsExist(Boolean.TRUE).documents(Set.of(document1, document2)).build();
        Map<UUID, String> permitSurrenderAttachments = Map.of(document1, "doc1", document2, "doc2");
        PermitSurrenderReviewDeterminationReject determination = PermitSurrenderReviewDeterminationReject.builder()
                .type(PermitSurrenderReviewDeterminationType.REJECTED)
                .officialRefusalLetter("letter")
                .build();
        
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("user1", "user2"))
            .signatory("signatory")
            .build();
        
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
                .permitSurrender(permitSurrender)
                .permitSurrenderAttachments(permitSurrenderAttachments)
                .reviewDetermination(determination)
                .reviewDecisionNotification(decisionNotification)
                .build();
        
        PermitSurrenderApplicationRejectedRequestActionPayload expectedActionPayload = 
                PermitSurrenderApplicationRejectedRequestActionPayload.builder()
                    .payloadType(RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_REJECTED_PAYLOAD)
                    .reviewDetermination(determination)
                    .reviewDecisionNotification(decisionNotification)
                    .build();
        
        PermitSurrenderApplicationRejectedRequestActionPayload actualActionPayload = mapper.toPermitSurrenderApplicationRejectedRequestActionPayload(requestPayload);
        
        assertThat(actualActionPayload).isEqualTo(expectedActionPayload);
    }
    
    @Test
    void toPermitSurrenderApplicationDeemedWithdrawnRequestActionPayload() {
        UUID document1 = UUID.randomUUID();
        UUID document2 = UUID.randomUUID();
        
        PermitSurrender permitSurrender = 
                PermitSurrender.builder().stopDate(LocalDate.now().minusDays(1)).justification("justify").documentsExist(Boolean.TRUE).documents(Set.of(document1, document2)).build();
        Map<UUID, String> permitSurrenderAttachments = Map.of(document1, "doc1", document2, "doc2");
        PermitSurrenderReviewDeterminationDeemWithdraw determination = PermitSurrenderReviewDeterminationDeemWithdraw.builder()
                .type(PermitSurrenderReviewDeterminationType.DEEMED_WITHDRAWN)
                .build();
        
        DecisionNotification decisionNotification = DecisionNotification.builder()
            .operators(Set.of("user1", "user2"))
            .signatory("signatory")
            .build();
        
        PermitSurrenderRequestPayload requestPayload = PermitSurrenderRequestPayload.builder()
                .payloadType(RequestPayloadType.PERMIT_ISSUANCE_REQUEST_PAYLOAD)
                .permitSurrender(permitSurrender)
                .permitSurrenderAttachments(permitSurrenderAttachments)
                .reviewDetermination(determination)
                .reviewDecisionNotification(decisionNotification)
                .build();
        
        PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload expectedActionPayload = 
                PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload.builder()
                    .payloadType(RequestActionPayloadType.PERMIT_SURRENDER_APPLICATION_DEEMED_WITHDRAWN_PAYLOAD)
                    .reviewDetermination(determination)
                    .reviewDecisionNotification(decisionNotification)
                    .build();
        
        PermitSurrenderApplicationDeemedWithdrawnRequestActionPayload actualActionPayload = mapper.toPermitSurrenderApplicationDeemedWithdrawnRequestActionPayload(requestPayload);
        
        assertThat(actualActionPayload).isEqualTo(expectedActionPayload);
    }
}
