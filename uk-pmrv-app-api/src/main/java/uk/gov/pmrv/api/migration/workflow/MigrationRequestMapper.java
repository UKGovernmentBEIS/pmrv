package uk.gov.pmrv.api.migration.workflow;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.common.transform.MapperConfig;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", config = MapperConfig.class)
public interface MigrationRequestMapper {

    @Mapping(target = "id", source = "workflowEntityVO.workflowId")
    @Mapping(target = "status", source = "workflowEntityVO.status")
    @Mapping(target = "accountId", source = "account.id")
    @Mapping(target = "verificationBodyId", ignore = true)
    Request toRequest(WorkflowEntityVO workflowEntityVO, Account account);

    default List<Request> toRequests(List<WorkflowEntityVO> etsWorkflows, Map<String, Account> accounts) {
        return etsWorkflows.stream()
                .filter(entityVO -> accounts.containsKey(entityVO.getEmitterId()))
                .map(entityVO -> toRequest(entityVO, accounts.get(entityVO.getEmitterId())))
                .collect(Collectors.toList());
    }
}
