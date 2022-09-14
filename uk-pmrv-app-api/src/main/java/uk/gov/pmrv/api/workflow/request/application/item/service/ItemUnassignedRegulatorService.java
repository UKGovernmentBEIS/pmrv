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
import uk.gov.pmrv.api.workflow.request.application.item.repository.ItemUnassignedRegulatorRepository;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;

import java.util.Map;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class ItemUnassignedRegulatorService implements ItemUnassignedService {

    private final ItemUnassignedRegulatorRepository itemUnassignedRegulatorRepository;
    private final ItemResponseService itemResponseService;
    private final RegulatorAuthorityResourceAdapter regulatorAuthorityResourceAdapter;

    /** {@inheritDoc} */
    @Override
    public ItemDTOResponse getUnassignedItems(PmrvUser pmrvUser, Long page, Long pageSize) {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes = regulatorAuthorityResourceAdapter
            .findUserScopedRequestTaskTypes(pmrvUser.getUserId());

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemUnassignedRegulatorRepository.findUnassignedItems(
                pmrvUser.getUserId(),
                scopedRequestTaskTypes,
                page,
                pageSize);

        return itemResponseService.toItemDTOResponse(itemPage, pmrvUser);
    }

    /** {@inheritDoc} */
    @Override
    public ItemDTOResponse getUnassignedItemsByAccount(PmrvUser pmrvUser, Long accountId, Long page, Long pageSize) {
        Map<CompetentAuthority, Set<RequestTaskType>> scopedRequestTaskTypes = regulatorAuthorityResourceAdapter
            .findUserScopedRequestTaskTypes(pmrvUser.getUserId());

        if (ObjectUtils.isEmpty(scopedRequestTaskTypes)) {
            return ItemDTOResponse.emptyItemDTOResponse();
        }

        ItemPage itemPage = itemUnassignedRegulatorRepository.findUnassignedItemsByAccount(
                pmrvUser.getUserId(),
                scopedRequestTaskTypes,
                accountId,
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
