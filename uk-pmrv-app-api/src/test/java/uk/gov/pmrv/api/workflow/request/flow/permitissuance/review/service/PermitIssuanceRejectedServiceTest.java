package uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.service;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import uk.gov.pmrv.api.account.service.AccountStatusService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.permit.DeterminationType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.common.domain.PermitIssuanceRequestPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceApplicationRejectedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.permitissuance.review.domain.PermitIssuanceRejectDetermination;

@ExtendWith(MockitoExtension.class)
class PermitIssuanceRejectedServiceTest {

	@InjectMocks
    private PermitIssuanceRejectedService cut;

    @Mock
    private RequestService requestService;
    
    @Mock
    private AccountStatusService accountStatusService;
    
    @Mock
    private RequestActionUserInfoResolver requestActionUserInfoResolver;
    
    @Test
    void reject() {
    	String requestId = "1";
    	DecisionNotification decisionNotification = DecisionNotification.builder()
				.signatory("sign")
				.operators(Set.of("op1"))
				.build();
		PermitIssuanceRejectDetermination rejectDetermination = PermitIssuanceRejectDetermination.builder()
				.type(DeterminationType.REJECTED)
				.reason("reason")
				.officialNotice("offNotice").build();
    	
    	PermitIssuanceRequestPayload requestPayload = PermitIssuanceRequestPayload.builder()
    			.regulatorReviewer("regReviewer")
    			.permitDecisionNotification(decisionNotification)
    			.determination(rejectDetermination)
    			.build();
    	
    	Request request = Request.builder()
    			.id(requestId)
    			.accountId(1L)
    			.payload(requestPayload)
    			.build();
    	
    	Map<String, RequestActionUserInfo> usersInfo = Map.of(
    			"oper1", RequestActionUserInfo.builder().name("opUserInfo1").build()
    			);
    	
    	when(requestService.findRequestById(requestId)).thenReturn(request);
    	when(requestActionUserInfoResolver.getUsersInfo(Set.of("op1"), "sign", request))
    		.thenReturn(usersInfo);
    	
    	//invoke
    	cut.reject(requestId);
    	
    	verify(requestService, times(1)).findRequestById(requestId);
    	verify(accountStatusService, times(1)).handlePermitRejected(request.getAccountId());
    	verify(requestActionUserInfoResolver, times(1)).getUsersInfo(Set.of("op1"), "sign", request);
    	
    	
    	PermitIssuanceApplicationRejectedRequestActionPayload actionPayloadExpected = PermitIssuanceApplicationRejectedRequestActionPayload.builder()
    			.payloadType(RequestActionPayloadType.PERMIT_ISSUANCE_APPLICATION_REJECTED_PAYLOAD)
    			.usersInfo(usersInfo)
    			.permitDecisionNotification(decisionNotification)
    			.determination(rejectDetermination)
    			.build();
    	verify(requestService, times(1)).addActionToRequest(request, actionPayloadExpected, RequestActionType.PERMIT_ISSUANCE_APPLICATION_REJECTED, requestPayload.getRegulatorReviewer());
    }
}
