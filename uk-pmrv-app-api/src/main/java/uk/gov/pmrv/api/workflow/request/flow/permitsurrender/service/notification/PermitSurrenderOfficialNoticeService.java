package uk.gov.pmrv.api.workflow.request.flow.permitsurrender.service.notification;

import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.notification.template.domain.enumeration.DocumentTemplateType;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;
import uk.gov.pmrv.api.workflow.request.flow.common.service.DecisionNotificationUsersService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.DocumentTemplateGenerationContextActionType;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeGeneratorService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.notification.OfficialNoticeSendService;
import uk.gov.pmrv.api.workflow.request.flow.permitsurrender.domain.PermitSurrenderRequestPayload;

@Service
@RequiredArgsConstructor
public class PermitSurrenderOfficialNoticeService {

    private final RequestService requestService;
    private final DecisionNotificationUsersService decisionNotificationUsersService;
    private final OfficialNoticeSendService officialNoticeSendService;
    private final OfficialNoticeGeneratorService officialNoticeGeneratorService;
    
    @Transactional
    public void generateAndSaveGrantedOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermitSurrenderRequestPayload requestPayload = (PermitSurrenderRequestPayload) request.getPayload();
        
        //generate
        final FileInfoDTO officialNotice = officialNoticeGeneratorService.generate(request,
            DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_GRANTED,
            DocumentTemplateType.PERMIT_SURRENDERED_NOTICE,
            requestPayload.getReviewDecisionNotification(),
            "permit_surrender_notice.pdf");
        
        //save to payload
        requestPayload.setOfficialNotice(officialNotice);
    }
    
    @Transactional
    public void generateAndSaveRejectedOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermitSurrenderRequestPayload requestPayload = (PermitSurrenderRequestPayload) request.getPayload();
        
        //generate
        final FileInfoDTO officialNotice = officialNoticeGeneratorService.generate(request,
            DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_REJECTED,
            DocumentTemplateType.PERMIT_SURRENDER_REJECTED_NOTICE,
            requestPayload.getReviewDecisionNotification(),
            "permit_surrender_refused_notice.pdf");
        
        //save to payload
        requestPayload.setOfficialNotice(officialNotice);
    }
    
    @Transactional
    public void generateAndSaveDeemedWithdrawnOfficialNotice(String requestId) {
        final Request request = requestService.findRequestById(requestId);
        final PermitSurrenderRequestPayload requestPayload = (PermitSurrenderRequestPayload) request.getPayload();
        
        //generate
        final FileInfoDTO officialNotice = officialNoticeGeneratorService.generate(request,
            DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_DEEMED_WITHDRAWN,
            DocumentTemplateType.PERMIT_SURRENDER_DEEMED_WITHDRAWN_NOTICE,
            requestPayload.getReviewDecisionNotification(),
            "permit_surrender_deemed_withdrawn_notice.pdf");
        
        //save to payload
        requestPayload.setOfficialNotice(officialNotice);
    }
    
    @Transactional
    public FileInfoDTO generateCessationOfficialNotice(Request request, DecisionNotification cessationDecisionNotification) {
        return officialNoticeGeneratorService.generate(request,
            DocumentTemplateGenerationContextActionType.PERMIT_SURRENDER_CESSATION,
            DocumentTemplateType.PERMIT_SURRENDER_CESSATION,
            cessationDecisionNotification,
            "permit_surrender_cessation_complete_notice.pdf");
    }
    
    public void sendReviewDeterminationOfficialNotice(Request request) {
        final PermitSurrenderRequestPayload requestPayload = (PermitSurrenderRequestPayload) request.getPayload();
        final FileInfoDTO officialNotice = requestPayload.getOfficialNotice();
        final DecisionNotification decisionNotification = requestPayload.getReviewDecisionNotification();

        sendOfficialNotice(request, officialNotice, decisionNotification);
    }

    public void sendOfficialNotice(Request request, FileInfoDTO officialNotice, DecisionNotification decisionNotification ) {
        final List<String> ccRecipientsEmails = decisionNotificationUsersService.findUserEmails(decisionNotification);
        officialNoticeSendService.sendOfficialNotice(officialNotice, request, ccRecipientsEmails);
    }
}
