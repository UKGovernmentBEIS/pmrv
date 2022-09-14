package uk.gov.pmrv.api.workflow.request.application.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.common.domain.enumeration.CompetentAuthority;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.workflow.request.application.authorization.RegulatorAuthorityResourceAdapter;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemAssignedToOthersRegulatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemAssignedToOthersRegulatorService implements ItemAssignedToOthersService {

    private final ItemAssignedToOthersRegulatorRepository itemAssignedToOthersRegulatorRepository;
    private final ItemResponseService itemResponseService;
    private final RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;

    /** {@inheritDoc} */
    @Override
    public ItemDTOResponse getItemsAssignedToOthers(PmrvUser pmrvUser, Long page, Long pageSize) {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes = regulatorAuthorityResourceAdapter
            .findUserScopedRequestTaskTypes(pmrvUser.getUserId());
        
        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemAssignedToOthersRegulatorRepository.findItemsAssignedToOthers(
                pmrvUser.getUserId(),
                scopedRequestTaskTypes,
                page,
                pageSize);

        return itemResponseService.toItemDTOResponse(itemPage, pmrvUser);
    }

    /** {@inheritDoc} */
    @Override
    public ItemDTOResponse getItemsAssignedToOthersByAccount(PmrvUser pmrvUser, Long accountId, Long page, Long pageSize) {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes = regulatorAuthorityResourceAdapter
            .findUserScopedRequestTaskTypes(pmrvUser.getUserId());

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemAssignedToOthersRegulatorRepository.findItemsAssignedToOthersByAccount(
                pmrvUser.getUserId(),
                accountId,
                scopedRequestTaskTypes,
                page,
                pageSize);

        return itemResponseService.toItemDTOResponse(itemPage, pmrvUser);
    }

    /** {@inheritDoc} */
    @Override
    public RoleType getRoleType() {
        return RoleType.REGULATOR;
    }
}
