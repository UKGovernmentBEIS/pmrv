package uk.gov.pmrv.api.authorization.rules.services.authorityinfo.providers;

import uk.gov.pmrv.api.authorization.rules.services.authorityinfo.dto.RequestTaskAuthorityInfoDTO;

public interface RequestTaskAuthorityInfoProvider {
    RequestTaskAuthorityInfoDTO getRequestTaskInfo(Long requestTaskId);
}
