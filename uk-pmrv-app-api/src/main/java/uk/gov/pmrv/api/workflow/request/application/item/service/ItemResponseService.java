package uk.gov.pmrv.api.workflow.request.application.item.service;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections.CollectionUtils;
import org.mapstruct.factory.Mappers;
import org.springframework.stereotype.Component;
import uk.gov.pmrv.api.account.service.AccountQueryService;
import uk.gov.pmrv.api.authorization.core.service.UserRoleTypeService;
import uk.gov.pmrv.api.common.domain.enumeration.RoleType;
import uk.gov.pmrv.api.common.domain.model.PmrvUser;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.user.core.domain.model.UserInfo;
import uk.gov.pmrv.api.user.core.service.auth.UserAuthService;
import uk.gov.pmrv.api.workflow.request.application.item.domain.Item;
import uk.gov.pmrv.api.workflow.request.application.item.domain.ItemPage;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemAccountDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTO;
import uk.gov.pmrv.api.workflow.request.application.item.domain.dto.ItemDTOResponse;
import uk.gov.pmrv.api.workflow.request.application.item.transform.ItemMapper;
import uk.gov.pmrv.api.workflow.request.core.domain.dto.UserInfoDTO;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ItemResponseService {

    private static final ItemMapper itemMapper = Mappers.getMapper(ItemMapper.class);
    private final UserAuthService userAuthService;
    private final AccountQueryService accountQueryService;
    private final UserRoleTypeService userRoleTypeService;
    private final PermitQueryService permitQueryService;

    public ItemDTOResponse toItemDTOResponse(ItemPage itemPage, PmrvUser pmrvUser) {

        //get user info from keycloak for the task assignee ids
        Map<String, UserInfoDTO> users = getUserInfoForItemAssignees(pmrvUser, itemPage);
        //get accounts for operator or regulator
        Map<Long, ItemAccountDTO> accounts = getUserAccounts(pmrvUser, itemPage);
        final Map<Long, Optional<String>> accountPermitIdMap = itemPage.getItems().stream()
            .map(Item::getAccountId)
            .distinct()
            .collect(Collectors.toMap(accId -> accId, permitQueryService::getPermitIdByAccountId));

        List<ItemDTO> itemDTOs = itemPage.getItems().stream().map(i -> {
            UserInfoDTO taskAssignee = i.getTaskAssigneeId() != null
                ? users.get(i.getTaskAssigneeId())
                : null;
            RoleType taskAssigneeType = i.getTaskAssigneeId() != null
                ? userRoleTypeService.getUserRoleTypeByUserId(i.getTaskAssigneeId()).getRoleType()
                : null;
            ItemAccountDTO account = accounts.get(i.getAccountId());
            final String permitId = accountPermitIdMap.get(i.getAccountId()).orElse(null);
            return itemMapper.itemToItemDTO(i,
                taskAssignee,
                taskAssigneeType,
                account,
                permitId);
        }).collect(Collectors.toList());

        return ItemDTOResponse.builder().items(itemDTOs)
            .totalItems(itemPage.getTotalItems())
            .build();

    }

    /**
     * Get user info from keycloak for item assignees
     *
     * @param itemPage {@link ItemPage}
     * @return Map of user id and user details
     */
    private Map<String, UserInfoDTO> getUserInfoForItemAssignees(PmrvUser pmrvUser, ItemPage itemPage) {
        Set<String> userIds = itemPage.getItems().stream()
            .map(Item::getTaskAssigneeId)
            .filter(Objects::nonNull)
            .collect(Collectors.toSet());

        if (CollectionUtils.isEmpty(userIds))
            return Collections.emptyMap();

        //if the assignee of all items is the pmrvUser
        if (userIds.size() == 1 && userIds.contains(pmrvUser.getUserId()))
            return Map.of(pmrvUser.getUserId(),
                new UserInfoDTO(pmrvUser.getFirstName(), pmrvUser.getLastName()));

        return userAuthService.getUsers(new ArrayList<>(userIds)).stream()
            .collect(Collectors.toMap(
                UserInfo::getId,
                u -> new UserInfoDTO(u.getFirstName(), u.getLastName())));
    }

    /**
     * Get user accounts (operator or regulator)
     *
     * @param pmrvUser {@link PmrvUser}
     * @param itemPage {@link ItemPage}
     * @return Map of account id and account details
     */
    private Map<Long, ItemAccountDTO> getUserAccounts(PmrvUser pmrvUser, ItemPage itemPage) {
        final Map<Long, ItemAccountDTO> userAccounts;
        switch (pmrvUser.getRoleType()) {
        case OPERATOR:
            userAccounts = getAccountsForOperator(pmrvUser);
            break;
        case REGULATOR:
            userAccounts = getAccountsForRegulator(pmrvUser, itemPage);
            break;
        case VERIFIER:
            userAccounts = getAccountsForVerifier(pmrvUser, itemPage);
            break;
        default:
            throw new UnsupportedOperationException(String.format("Role type %s is not supported yet", pmrvUser.getRoleType()));
        }
        return userAccounts;
    }

    /**
     * Get operator accounts
     *
     * @param pmrvUser {@link PmrvUser}
     * @return map of accountId to AccountDTO
     */
    private Map<Long, ItemAccountDTO> getAccountsForOperator(PmrvUser pmrvUser) {
        return accountQueryService.getAccountsByOperatorUser(pmrvUser).stream()
            .map(itemMapper::accountDTOToItemAccountDTO)
            .collect(Collectors.toMap(ItemAccountDTO::getAccountId, a -> a));
    }

    /**
     * Get operator accounts that regulator has access to
     *
     * @param pmrvUser {@link PmrvUser}
     * @param itemPage {@link ItemPage}
     * @return map of accountId to AccountDTO
     */
    private Map<Long, ItemAccountDTO> getAccountsForRegulator(PmrvUser pmrvUser, ItemPage itemPage) {
        List<Long> accountIds = itemPage.getItems()
            .stream().map(Item::getAccountId)
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(accountIds))
            return Collections.emptyMap();

        return accountQueryService.getAccountsByCompetentAuthorityAndIds(pmrvUser.getCompetentAuthority(), accountIds).stream()
            .map(itemMapper::accountDTOToItemAccountDTO)
            .collect(Collectors.toMap(ItemAccountDTO::getAccountId, a -> a));
    }
    
    /**
     * Get accounts that verifier has access.
     *
     * @param pmrvUser {@link PmrvUser}
     * @param itemPage {@link ItemPage}
     * @return map of accountId to AccountDTO
     */
    private Map<Long, ItemAccountDTO> getAccountsForVerifier(PmrvUser pmrvUser, ItemPage itemPage) {
        List<Long> accountIds = itemPage.getItems()
            .stream().map(Item::getAccountId)
            .collect(Collectors.toList());

        if (CollectionUtils.isEmpty(accountIds))
            return Collections.emptyMap();

        return accountQueryService.getAccountsByVerifierUserIdAndIds(pmrvUser, accountIds).stream()
            .map(itemMapper::accountDTOToItemAccountDTO)
            .collect(Collectors.toMap(ItemAccountDTO::getAccountId, a -> a));
    }

}
