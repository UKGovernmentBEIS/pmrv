package uk.gov.pmrv.api.user.operator.service;

import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.user.operator.domain.OperatorUserAcceptInvitationDTO;
import uk.gov.pmrv.api.user.core.domain.enumeration.UserInvitationStatus;

@Service
@RequiredArgsConstructor
public class OperatorRoleCodeAcceptInvitationServiceDelegator {

    private final List<OperatorRoleCodeAcceptInvitationService> operatorRoleCodeAcceptInvitationServices;

    @Transactional
    public UserInvitationStatus acceptInvitation(OperatorUserAcceptInvitationDTO operatorUserAcceptInvitation, String roleCode) {
        return getOperatorUserAcceptInvitationService(roleCode)
            .map(service -> service.acceptInvitation(operatorUserAcceptInvitation))
            .orElseThrow(() -> new BusinessException(ErrorCode.AUTHORITY_USER_IS_NOT_OPERATOR));
    }

    private Optional<OperatorRoleCodeAcceptInvitationService> getOperatorUserAcceptInvitationService(String roleCode) {
        return operatorRoleCodeAcceptInvitationServices.stream()
            .filter(service -> service.getRoleCodes().contains(roleCode))
            .findAny();
    }
}
