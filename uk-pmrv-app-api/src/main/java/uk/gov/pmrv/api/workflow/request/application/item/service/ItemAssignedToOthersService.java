package uk.gov.pmrv.api.workflow.request.application.item.service;

import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;

public interface ItemAssignedToOthersService {

    /**
     * Get items (requests and tasks) assigned to other users.
     *
     * @param pmrvUser {@link PmrvUser}
     * @param page The requested page
     * @param pageSize The request page size
     * @return {@link ItemDTOResponse}
     */
    ItemDTOResponse getItemsAssignedToOthers(PmrvUser pmrvUser, Long page, Long pageSize);

    /**
     * Get items (requests and tasks) assigned to other users per account.
     *
     * @param pmrvUser {@link PmrvUser}
     * @param accountId Account id
     * @param page The requested page
     * @param pageSize The request page size
     * @return {@link ItemDTOResponse}
     */
    ItemDTOResponse getItemsAssignedToOthersByAccount(PmrvUser pmrvUser, Long accountId, Long page, Long pageSize);

    /**
     * The service's role type acceptance.
     *
     * @return {@link RoleType}
     */
    RoleType getRoleType();
}
