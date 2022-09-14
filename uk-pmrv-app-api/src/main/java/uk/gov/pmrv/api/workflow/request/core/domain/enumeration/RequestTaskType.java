package uk.gov.pmrv.api.workflow.request.core.domain.enumeration;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.APPLICATION_PEER_REVIEW;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.CONFIRM_PAYMENT;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.MAKE_PAYMENT;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.RDE_RESPONSE_SUBMIT;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.RFI_RESPONSE_SUBMIT;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.TRACK_PAYMENT;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.WAIT_FOR_RDE_RESPONSE;
import static uk.gov.pmrv.api.workflow.request.flow.common.taskhandler.DynamicUserTaskDefinitionKey.WAIT_FOR_RFI_RESPONSE;

/**
 * Task type enum. <br/>
 * Note: The enum is used in bpmn workflow engine to set the user task definition key (ID), e.g. <br/> 
 * <i>&lt;bpmn:userTask id="INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW" name="Review application"&gt;</i> 
 *
 */
@Getter
@AllArgsConstructor
public enum RequestTaskType {

    INSTALLATION_ACCOUNT_OPENING_APPLICATION_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_AMEND_APPLICATION,
                RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_SUBMIT_DECISION);
        }
    },
    
    ACCOUNT_USERS_SETUP(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return Collections.singletonList(RequestTaskActionType.SYSTEM_MESSAGE_DISMISS);
        }
    },
    
    INSTALLATION_ACCOUNT_OPENING_ARCHIVE(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return Collections.singletonList(RequestTaskActionType.INSTALLATION_ACCOUNT_OPENING_ARCHIVE);
        }
    },
    
    PERMIT_ISSUANCE_APPLICATION_SUBMIT(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION,
                RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.PERMIT_ISSUANCE_SUBMIT_APPLICATION);
        }
    },
    
    PERMIT_ISSUANCE_WAIT_FOR_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },
    
    PERMIT_ISSUANCE_APPLICATION_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION_REVIEW,
                    RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT,
                    RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_GROUP_DECISION,
                    RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT,
                    RequestTaskActionType.PERMIT_ISSUANCE_SAVE_REVIEW_DETERMINATION,
                    RequestTaskActionType.PERMIT_ISSUANCE_NOTIFY_OPERATOR_FOR_DECISION,
                    RequestTaskActionType.PERMIT_ISSUANCE_REQUEST_PEER_REVIEW,
                    RequestTaskActionType.PERMIT_ISSUANCE_REVIEW_RETURN_FOR_AMENDS,
                    RequestTaskActionType.RFI_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    PERMIT_ISSUANCE_WAIT_FOR_AMENDS(true){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.PERMIT_ISSUANCE_RECALL_FROM_AMENDS,
                    RequestTaskActionType.RFI_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT(true){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_ISSUANCE_SAVE_APPLICATION_AMEND,
                RequestTaskActionType.PERMIT_ISSUANCE_UPLOAD_SECTION_ATTACHMENT,
                RequestTaskActionType.PERMIT_ISSUANCE_SUBMIT_APPLICATION_AMEND);
        }
    },

    PERMIT_ISSUANCE_APPLICATION_PEER_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_ISSUANCE_REVIEW_SUBMIT_PEER_REVIEW_DECISION
            );
        }
    },

    PERMIT_ISSUANCE_WAIT_FOR_PEER_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RDE_SUBMIT
            );
        }
    },
    
    PERMIT_ISSUANCE_RFI_RESPONSE_SUBMIT(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    PERMIT_ISSUANCE_WAIT_FOR_RFI_RESPONSE(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    PERMIT_ISSUANCE_RDE_RESPONSE_SUBMIT(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_RESPONSE_SUBMIT);
        }
    },

    PERMIT_ISSUANCE_WAIT_FOR_RDE_RESPONSE(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_FORCE_DECISION,
                           RequestTaskActionType.RDE_UPLOAD_ATTACHMENT);
        }
    },

    PERMIT_ISSUANCE_MAKE_PAYMENT(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    PERMIT_ISSUANCE_TRACK_PAYMENT(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_ISSUANCE_CONFIRM_PAYMENT(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },
    
    PERMIT_SURRENDER_APPLICATION_SUBMIT(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_SURRENDER_SAVE_APPLICATION,
                RequestTaskActionType.PERMIT_SURRENDER_UPLOAD_ATTACHMENT,
                RequestTaskActionType.PERMIT_SURRENDER_SUBMIT_APPLICATION,
                RequestTaskActionType.PERMIT_SURRENDER_CANCEL_APPLICATION);
        }
    },
    
    PERMIT_SURRENDER_APPLICATION_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_SURRENDER_SAVE_REVIEW_GROUP_DECISION,
                RequestTaskActionType.PERMIT_SURRENDER_SAVE_REVIEW_DETERMINATION,
                RequestTaskActionType.PERMIT_SURRENDER_NOTIFY_OPERATOR_FOR_DECISION,
                RequestTaskActionType.PERMIT_SURRENDER_REQUEST_PEER_REVIEW,
                RequestTaskActionType.RFI_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                RequestTaskActionType.RDE_SUBMIT
            );
        }
    },
    
    PERMIT_SURRENDER_WAIT_FOR_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    PERMIT_SURRENDER_APPLICATION_PEER_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_SURRENDER_REVIEW_SUBMIT_PEER_REVIEW_DECISION
            );
        }
    },

    PERMIT_SURRENDER_WAIT_FOR_PEER_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RDE_SUBMIT
            );
        }
    },

    PERMIT_SURRENDER_RFI_RESPONSE_SUBMIT(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    PERMIT_SURRENDER_WAIT_FOR_RFI_RESPONSE(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    PERMIT_SURRENDER_RDE_RESPONSE_SUBMIT(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_RESPONSE_SUBMIT);
        }
    },

    PERMIT_SURRENDER_WAIT_FOR_RDE_RESPONSE(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_FORCE_DECISION,
                    RequestTaskActionType.RDE_UPLOAD_ATTACHMENT);
        }
    },

    PERMIT_SURRENDER_CESSATION_SUBMIT(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_SURRENDER_SAVE_CESSATION,
                RequestTaskActionType.PERMIT_SURRENDER_CESSATION_NOTIFY_OPERATOR_FOR_DECISION
            );
        }
    },

    PERMIT_SURRENDER_MAKE_PAYMENT(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    PERMIT_SURRENDER_TRACK_PAYMENT(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_SURRENDER_CONFIRM_PAYMENT(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_REVOCATION_APPLICATION_SUBMIT(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.PERMIT_REVOCATION_SAVE_APPLICATION,
                           RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_SUBMISSION,
                           RequestTaskActionType.PERMIT_REVOCATION_CANCEL_APPLICATION,
                           RequestTaskActionType.PERMIT_REVOCATION_REQUEST_PEER_REVIEW);
        }
    },

    PERMIT_REVOCATION_APPLICATION_PEER_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.PERMIT_REVOCATION_SUBMIT_PEER_REVIEW_DECISION);
        }
    },

    PERMIT_REVOCATION_WAIT_FOR_PEER_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.PERMIT_REVOCATION_CANCEL_APPLICATION);
        }
    },

    PERMIT_REVOCATION_WAIT_FOR_APPEAL(true){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.PERMIT_REVOCATION_WITHDRAW_APPLICATION,
                           RequestTaskActionType.PERMIT_REVOCATION_UPLOAD_ATTACHMENT,
                           RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_WITHDRAWAL);
        }
    },

    PERMIT_REVOCATION_CESSATION_SUBMIT(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_REVOCATION_SAVE_CESSATION,
                RequestTaskActionType.PERMIT_REVOCATION_NOTIFY_OPERATOR_FOR_CESSATION
            );
        }
    },

    PERMIT_REVOCATION_MAKE_PAYMENT(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getMakePaymentAllowedTypes();
        }
    },

    PERMIT_REVOCATION_TRACK_PAYMENT(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_REVOCATION_CONFIRM_PAYMENT(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return RequestTaskActionType.getTrackAndConfirmPaymentAllowedTypes();
        }
    },

    PERMIT_NOTIFICATION_APPLICATION_SUBMIT(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.PERMIT_NOTIFICATION_SAVE_APPLICATION,
                    RequestTaskActionType.PERMIT_NOTIFICATION_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.PERMIT_NOTIFICATION_SUBMIT_APPLICATION,
                    RequestTaskActionType.PERMIT_NOTIFICATION_CANCEL_APPLICATION
            );
        }
    },

    PERMIT_NOTIFICATION_APPLICATION_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.PERMIT_NOTIFICATION_SAVE_REVIEW_GROUP_DECISION,
                    RequestTaskActionType.PERMIT_NOTIFICATION_NOTIFY_OPERATOR_FOR_DECISION,
                    RequestTaskActionType.RFI_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
                    RequestTaskActionType.PERMIT_NOTIFICATION_REQUEST_PEER_REVIEW
            );
        }
    },

    PERMIT_NOTIFICATION_WAIT_FOR_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    PERMIT_NOTIFICATION_APPLICATION_PEER_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_REVIEW_SUBMIT_PEER_REVIEW_DECISION);
        }
    },

    PERMIT_NOTIFICATION_WAIT_FOR_PEER_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    PERMIT_NOTIFICATION_RFI_RESPONSE_SUBMIT(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                    RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    PERMIT_NOTIFICATION_WAIT_FOR_RFI_RESPONSE(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.RFI_CANCEL
            );
        }
    },

    PERMIT_NOTIFICATION_FOLLOW_UP(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_RESPONSE,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_RESPONSE
            );
        }
    },

    PERMIT_NOTIFICATION_WAIT_FOR_FOLLOW_UP(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE);
        }
    },

    PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_REVIEW_DECISION,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_NOTIFY_OPERATOR_FOR_DECISION,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RETURN_FOR_AMENDS
            );
        }
    },
    
    PERMIT_NOTIFICATION_FOLLOW_UP_WAIT_FOR_AMENDS(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_EXTEND_DATE,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS
            );
        }
    },
    
    PERMIT_NOTIFICATION_FOLLOW_UP_APPLICATION_AMENDS_SUBMIT(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_UPLOAD_ATTACHMENT,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SAVE_APPLICATION_AMEND,
                RequestTaskActionType.PERMIT_NOTIFICATION_FOLLOW_UP_SUBMIT_APPLICATION_AMEND
            );
        }
    },
    
    PERMIT_VARIATION_OPERATOR_APPLICATION_SUBMIT(true){
    	@Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
			return List.of(
					RequestTaskActionType.PERMIT_VARIATION_OPERATOR_SAVE_APPLICATION,
					RequestTaskActionType.PERMIT_VARIATION_UPLOAD_SECTION_ATTACHMENT,
                    RequestTaskActionType.PERMIT_VARIATION_OPERATOR_SUBMIT_APPLICATION,
                    RequestTaskActionType.PERMIT_VARIATION_CANCEL_APPLICATION);
        }
    },
    
    PERMIT_VARIATION_REGULATOR_APPLICATION_SUBMIT(true){
    	@Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            //TODO
    		return List.of();
        }
    },
    
    PERMIT_VARIATION_APPLICATION_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
            		RequestTaskActionType.PERMIT_VARIATION_SAVE_APPLICATION_REVIEW,
            		RequestTaskActionType.PERMIT_VARIATION_UPLOAD_SECTION_ATTACHMENT,
                    RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_GROUP_DECISION,
                    RequestTaskActionType.PERMIT_VARIATION_SAVE_DETAILS_REVIEW_GROUP_DECISION,
                    RequestTaskActionType.PERMIT_VARIATION_UPLOAD_REVIEW_GROUP_DECISION_ATTACHMENT,
                    RequestTaskActionType.PERMIT_VARIATION_SAVE_REVIEW_DETERMINATION,
            		RequestTaskActionType.RFI_SUBMIT,
            		RequestTaskActionType.RFI_UPLOAD_ATTACHMENT,
            		RequestTaskActionType.RDE_SUBMIT
            		);
        }
    },
    
    PERMIT_VARIATION_WAIT_FOR_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },
    
    PERMIT_VARIATION_RFI_RESPONSE_SUBMIT(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_RESPONSE_SUBMIT,
                RequestTaskActionType.RFI_UPLOAD_ATTACHMENT
            );
        }
    },

    PERMIT_VARIATION_WAIT_FOR_RFI_RESPONSE(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                RequestTaskActionType.RFI_CANCEL
            );
        }
    },
    
    PERMIT_VARIATION_RDE_RESPONSE_SUBMIT(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_RESPONSE_SUBMIT);
        }
    },

    PERMIT_VARIATION_WAIT_FOR_RDE_RESPONSE(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(RequestTaskActionType.RDE_FORCE_DECISION,
                    RequestTaskActionType.RDE_UPLOAD_ATTACHMENT);
        }
    },

    AER_APPLICATION_SUBMIT(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of(
                    RequestTaskActionType.AER_SAVE_APPLICATION,
                    RequestTaskActionType.AER_UPLOAD_SECTION_ATTACHMENT
            );
        }
    },

    AER_APPLICATION_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            // TODO
            return List.of();
        }
    },

    AER_WAIT_FOR_REVIEW(true) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return List.of();
        }
    },

    AER_APPLICATION_AMENDS_SUBMIT(true){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            // TODO
            return List.of();
        }
    },

    AER_WAIT_FOR_AMENDS(true){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            // TODO
            return List.of();
        }
    },

    AER_APPLICATION_VERIFICATION_SUBMIT(true){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            // TODO
            return List.of();
        }
    },

    AER_WAIT_FOR_VERIFICATION(true){
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            // TODO
            return List.of();
        }
    },
    
    NEW_VERIFICATION_BODY_INSTALLATION(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return Collections.singletonList(RequestTaskActionType.SYSTEM_MESSAGE_DISMISS);
        }
    },
    VERIFIER_NO_LONGER_AVAILABLE(false) {
        @Override
        public List<RequestTaskActionType> getAllowedRequestTaskActionTypes() {
            return Collections.singletonList(RequestTaskActionType.SYSTEM_MESSAGE_DISMISS);
        }
    }
    ;

    private final boolean assignable;

    public abstract List<RequestTaskActionType> getAllowedRequestTaskActionTypes();

    public static Set<RequestTaskType> getSystemMessageNotificationTypes() {
        return Set.of(
            ACCOUNT_USERS_SETUP,
            VERIFIER_NO_LONGER_AVAILABLE,
            NEW_VERIFICATION_BODY_INSTALLATION
        );
    }

    public static Set<RequestTaskType> getPeerReviewTypes() {
        return Stream.of(RequestTaskType.values())
            .filter(requestTaskType -> requestTaskType.name().endsWith(APPLICATION_PEER_REVIEW.getId()))
            .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getMakePaymentTypes() {
        return Stream.of(RequestTaskType.values())
            .filter(requestTaskType -> requestTaskType.name().endsWith(MAKE_PAYMENT.getId()))
            .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getTrackPaymentTypes() {
        return Stream.of(RequestTaskType.values())
            .filter(requestTaskType -> requestTaskType.name().endsWith(TRACK_PAYMENT.getId()))
            .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getConfirmPaymentTypes() {
        return Stream.of(RequestTaskType.values())
            .filter(requestTaskType -> requestTaskType.name().endsWith(CONFIRM_PAYMENT.getId()))
            .collect(Collectors.toSet());
    }

    public static Set<RequestTaskType> getRegulatorPaymentTypes() {
        Set<RequestTaskType> requestTaskRegulatorPaymentTypes = new HashSet<>();
        requestTaskRegulatorPaymentTypes.addAll(RequestTaskType.getTrackPaymentTypes());
        requestTaskRegulatorPaymentTypes.addAll(RequestTaskType.getConfirmPaymentTypes());
        return requestTaskRegulatorPaymentTypes;
    }

    public static Set<RequestTaskType> getRfiResponseTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(RFI_RESPONSE_SUBMIT.getId()))
                .collect(Collectors.toSet());
    }
    
    public static Set<RequestTaskType> getRfiWaitForResponseTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(WAIT_FOR_RFI_RESPONSE.getId()))
                .collect(Collectors.toSet());
    }
    
    public static Set<RequestTaskType> getRdeResponseTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(RDE_RESPONSE_SUBMIT.getId()))
                .collect(Collectors.toSet());
    }
    
    public static Set<RequestTaskType> getRdeWaitForResponseTypes() {
        return Stream.of(RequestTaskType.values())
                .filter(requestTaskType -> requestTaskType.name().endsWith(WAIT_FOR_RDE_RESPONSE.getId()))
                .collect(Collectors.toSet());
    }
    
    public static Set<RequestTaskType> getRfiRdeWaitForResponseTypes() {
		return Stream.concat(getRfiWaitForResponseTypes().stream(), getRdeWaitForResponseTypes().stream())
				.collect(Collectors.toSet());
    }
}
