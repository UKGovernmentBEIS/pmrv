package uk.gov.pmrv.api.workflow.request.flow.rfi.handler;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.files.common.domain.dto.FileInfoDTO;
import uk.gov.pmrv.api.user.core.domain.dto.UserInfoDTO;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.RequestTask;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestActionType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskActionType;
import uk.gov.pmrv.api.workflow.request.core.service.RequestService;
import uk.gov.pmrv.api.workflow.request.core.service.RequestTaskService;
import uk.gov.pmrv.api.workflow.request.flow.common.actionhandler.RequestTaskActionHandler;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.dto.RequestActionUserInfo;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestAccountContactQueryService;
import uk.gov.pmrv.api.workflow.request.flow.common.service.RequestActionUserInfoResolver;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestPayloadRfiable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RequestTaskPayloadRfiAttachable;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiData;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiSubmitPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiSubmitRequestTaskActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.domain.RfiSubmittedRequestActionPayload;
import uk.gov.pmrv.api.workflow.request.flow.rfi.mapper.RfiMapper;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiSendEventService;
import uk.gov.pmrv.api.workflow.request.flow.rfi.service.RfiSubmitOfficialNoticeService;
import uk.gov.pmrv.api.workflow.request.flow.rfi.validation.SubmitRfiValidatorService;

@Component
@RequiredArgsConstructor
public class RfiSubmitActionHandler implements RequestTaskActionHandler<RfiSubmitRequestTaskActionPayload> {

    private static final RfiMapper RFI_MAPPER = Mappers.getMapper(RfiMapper.class);

    private final RequestService requestService;
    private final RequestTaskService requestTaskService;
    private final RfiSendEventService rfiSendEventService;
    private final SubmitRfiValidatorService validator;
    private final RequestActionUserInfoResolver requestActionUserInfoResolver;
    private final UserAuthService userAuthService;
    private final RequestAccountContactQueryService requestAccountContactQueryService;
    private final RfiSubmitOfficialNoticeService rfiSubmitOfficialNoticeService;
    
    @Override
    public void process(final Long requestTaskId,
                        final RequestTaskActionType requestTaskActionType,
                        final PmrvUser pmrvUser,
                        final RfiSubmitRequestTaskActionPayload actionPayload) {

        final RequestTask requestTask = requestTaskService.findTaskById(requestTaskId);
        final Request request = requestTask.getRequest();
        final RequestTaskPayloadRfiAttachable taskPayload = (RequestTaskPayloadRfiAttachable) requestTask.getPayload();
        final RfiSubmitPayload rfiSubmitPayload = actionPayload.getRfiSubmitPayload();

        // validate
        validator.validate(requestTask, rfiSubmitPayload, pmrvUser);

        // get users' information
        final Map<String, RequestActionUserInfo> usersInfo = 
            requestActionUserInfoResolver.getUsersInfo(rfiSubmitPayload.getOperators(), rfiSubmitPayload.getSignatory(), request);
        
        // copy rfi request and attachments in request payload
        final RequestPayloadRfiable requestPayload = (RequestPayloadRfiable) request.getPayload();
        requestPayload.setRfiData(RfiData.builder()
        		.rfiQuestionPayload(rfiSubmitPayload.getRfiQuestionPayload())
        		.rfiDeadline(rfiSubmitPayload.getDeadline())
        		.rfiAttachments(new HashMap<>(taskPayload.getRfiAttachments()))
        		.build());
        
        final UserInfoDTO accountPrimaryContact = requestAccountContactQueryService.getRequestAccountPrimaryContact(request);        
        final List<String> ccRecipientsEmails = userAuthService.getUsers(new ArrayList<>(rfiSubmitPayload.getOperators()))
                .stream().map(UserInfo::getEmail).collect(Collectors.toList());
        
        //generate official notice
        final FileInfoDTO officialNotice = rfiSubmitOfficialNoticeService.generateOfficialNotice(request,
                rfiSubmitPayload.getSignatory(), accountPrimaryContact, ccRecipientsEmails);

        // create timeline action
        final RfiSubmittedRequestActionPayload timelinePayload =
            RFI_MAPPER.toRfiSubmittedRequestActionPayload(actionPayload);
        final Map<UUID, String> timelineAttachments = new HashMap<>(taskPayload.getRfiAttachments());
        timelineAttachments.keySet().removeIf(k -> !rfiSubmitPayload.getRfiQuestionPayload().getFiles().contains(k));
        timelinePayload.setRfiAttachments(timelineAttachments);
        timelinePayload.setUsersInfo(usersInfo);
        timelinePayload.setOfficialDocument(officialNotice);
        
        requestService.addActionToRequest(request,
            timelinePayload,
            RequestActionType.RFI_SUBMITTED,
            pmrvUser.getUserId());

        // clear rfi attachments in task payload
        taskPayload.getRfiAttachments().clear();

        // send rfi event
        final String requestId = request.getId();
        rfiSendEventService.send(requestId, rfiSubmitPayload.getDeadline());

        //send email notification
        rfiSubmitOfficialNoticeService.sendOfficialNotice(officialNotice, request, ccRecipientsEmails);
    }

    @Override
    public List<RequestTaskActionType> getTypes() {
        return List.of(RequestTaskActionType.RFI_SUBMIT);
    }
    
}
