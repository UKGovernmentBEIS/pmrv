package uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers;

import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestActionAuthorityInfoDTO;

public interface RequestActionAuthorityInfoProvider {
    RequestActionAuthorityInfoDTO getRequestActionAuthorityInfo(Long requestActionId);
}
