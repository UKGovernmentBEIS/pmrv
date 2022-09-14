package uk.gov.pmrv.api.authorization.regulator.service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.authorization.core.domain.AuthorityStatus;
import uk.gov.pmrv.api.authorization.core.repository.AuthorityRepository;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.exception.BusinessException;
import uk.gov.pmrv.api.common.exception.ErrorCode;
import uk.gov.pmrv.api.authorization.regulator.domain.RegulatorUserUpdateStatusDTO;

@Component
@RequiredArgsConstructor
public class RegulatorStatusModificationAllowanceValidator {

    private final AuthorityRepository authorityRepository;

    public void validateUpdate(final List<RegulatorUserUpdateStatusDTO> regulatorStatuses,
                               final CompetentAuthority competentAuthority) {

        final List<String> userIds = regulatorStatuses.stream()
            .map(RegulatorUserUpdateStatusDTO::getUserId)
            .collect(Collectors.toList());
        final Map<String, AuthorityStatus> previousRegulatorStatuses = authorityRepository.findStatusByUsersAndCA(userIds, competentAuthority);
        
        // from status ACCEPTED allow only the transition to ACTIVE
        regulatorStatuses.forEach(regulatorStatus -> {
            final String regulatorId = regulatorStatus.getUserId();
            if (previousRegulatorStatuses.containsKey(regulatorId) &&
                previousRegulatorStatuses.get(regulatorId).equals(AuthorityStatus.ACCEPTED) &&
                !(List.of(AuthorityStatus.ACTIVE, AuthorityStatus.ACCEPTED).contains(regulatorStatus.getAuthorityStatus()))) {

                throw new BusinessException(ErrorCode.AUTHORITY_INVALID_STATUS,
                    regulatorId, 
                    regulatorStatus.getAuthorityStatus().name());
            }
        });
    }
}
