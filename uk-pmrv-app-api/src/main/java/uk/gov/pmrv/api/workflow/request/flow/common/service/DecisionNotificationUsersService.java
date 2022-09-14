package uk.gov.pmrv.api.workflow.request.flow.common.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import uk.gov.pmrv.api.account.service.CaExternalContactService;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.flow.common.domain.DecisionNotification;

@Service
@RequiredArgsConstructor
public class DecisionNotificationUsersService {

	private final UserAuthService userAuthService;
	private final CaExternalContactService caExternalContactService;
	
	public List<String> findUserEmails(DecisionNotification decisionNotification) {
		final List<String> operatorEmails = userAuthService
				.getUsers(new ArrayList<>(decisionNotification.getOperators())).stream()
				.map(UserInfo::getEmail).collect(Collectors.toList());
        
		final List<String> externalContactEmails = caExternalContactService
				.getCaExternalContactEmailsByIds(decisionNotification.getExternalContacts());
		
		return Stream
				.concat(operatorEmails.stream(), externalContactEmails.stream())
				.collect(Collectors.toList());
    }
}
