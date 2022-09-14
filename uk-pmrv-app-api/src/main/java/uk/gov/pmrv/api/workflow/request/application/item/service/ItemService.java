package uk.gov.pmrv.api.workflow.request.application.item.service;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;

public interface ItemService {

    ItemDTOResponse getItemsByRequest(PmrvUser pmrvUser, String requestId);

    RoleType getRoleType();
}
