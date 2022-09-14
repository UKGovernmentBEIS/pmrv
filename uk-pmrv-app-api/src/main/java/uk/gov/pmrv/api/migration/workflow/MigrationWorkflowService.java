package uk.gov.pmrv.api.migration.workflow;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;

import org.mapstruct.factory.Mappers;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.migration.MigrationBaseService;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.repository.RequestRepository;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@Log4j2
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
@Service
@RequiredArgsConstructor
public class MigrationWorkflowService extends MigrationBaseService {

    private final JdbcTemplate migrationJdbcTemplate;
    private final AccountRepository accountRepository;
    private final RequestRepository requestRepository;
    private final MigrationRequestMapper requestMapper = Mappers.getMapper(MigrationRequestMapper.class);

    private static final String QUERY_BASE = "with mig_wf_status as (\n" +
            "    select * from (values\n" +
            "            ('%', 'Approved', 'APPROVED'),\n" +
            "            ('%', 'Cancelled', 'CANCELLED'),\n" +
            "            ('%', 'Deemed Withdrawn', 'WITHDRAWN'),\n" +
            "            ('%', 'Refused', 'REJECTED'),\n" +
            "            ('%', 'Withdrawn', 'WITHDRAWN'),\n" +
            "            ('INPermitApplication', 'Complete', 'APPROVED'),\n" +
            "            ('INNotification', 'Complete', 'APPROVED'),\n" +
            "            ('INSurrender', 'Complete', 'APPROVED'),\n" +
            "            ('INRevocation', 'Complete', 'APPROVED'),\n" +
            "            ('INNewApplicant', 'Complete', 'COMPLETED'),\n" +
            "            ('INDetermination', 'Complete', 'COMPLETED')\n" +
            "    ) as t (wf_type_etswap, wf_status_etswap, wf_status_pmrv)\n" +
            "), mig_wf as (\n" +
            "    select * from (values\n" +
            "            ('INPermitApplication', 'PERMIT_ISSUANCE'),\n" +
            "            ('INNotification', 'PERMIT_NOTIFICATION'),\n" +
            "            ('INVariation','PERMIT_VARIATION'),\n" +
            "            ('INSurrender', 'PERMIT_SURRENDER'),\n" +
            "            ('INRevocation', 'PERMIT_REVOCATION'),\n" +
            "            ('INTransferPartA','PERMIT_TRANSFER'),\n" +
            "            ('INTransferPartB','PERMIT_TRANSFER')\n" +
            "    ) as t (wf_type_etswap, wf_type_pmrv)\n" +
            "), emitter as (\n" +
            "    select e.*, et.fldCode emitterType, es.fldDisplayName emitterStatus\n" +
            "      from tblEmitter e\n" +
            "      join tlkpEmitterType et on et.fldEmitterTypeID = e.fldEmitterTypeID\n" +
            "      join tlkpEmitterStatus es on es.fldEmitterStatusID = e.fldEmitterStatusID\n" +
            "     where et.fldCode = 'IN' and es.fldDisplayName = 'Live'\n" +
            "), wf as (\n" +
            "    select e.fldEmitterId,\n" +
            "           wt.fldName ETSWAPWorkflowType, \n" +
            "\t       replace(replace(replace(replace(replace(replace(wtp.fldDisplayFormat,\n" +
            "           '[[EmitterDisplayID]]', e.fldEmitterDisplayID),\n" +
            "           '[[PhaseDisplayID]]', p.fldDisplayID),\n" +
            "           '[[WorkflowDisplayID]]', w.fldDisplayID),\n" +
            "\t       '[[ReportingYear]]', isnull(YEAR(rp.fldReportingStartDate), '')),\n" +
            "           '[[BatchOperationID]]', isnull(bo.fldDisplayID, '')),\n" +
            "           '[[WorkflowCreatedDate]]', format(w.fldDateCreated, 'dd/MM/yyyy')) WorkflowId,\n" +
            "\t       w.fldDateCreated,\n" +
            "           ws.fldDisplayName ETSWAPWorkflowStatus\n" +
            "      from emitter e\n" +
            "      join tblWorkflow w on w.fldEmitterID = e.fldEmitterID\n" +
            "      join tblWorkflowTypePhase wtp on wtp.fldWorkflowTypePhaseID = w.fldWorkflowTypePhaseID\n" +
            "      join tlkpWorkflowType wt      on wtp.fldWorkflowTypeID = wt.fldWorkflowTypeID\n" +
            "      join tlkpPhase p              on p.fldPhaseID = wtp.fldPhaseID\n" +
            "      join tlkpWorkflowStatus ws    on ws.fldWorkflowStatusID = w.fldWorkflowStatusID\n" +
            "      left join tblReportingPeriod rp on rp.fldReportingPeriodID = w.fldReportingPeriodID\n" +
            "      left join tblBatchOperationEmitter boe on boe.fldWorkflowID = w.fldWorkflowID\n" +
            "      left join tblBatchOperation bo on bo.fldBatchOperationID = boe.fldBatchOperationID\n" +
            ")\n" +
            "select fldEmitterID, ETSWAPWorkflowType, ETSWAPWorkflowStatus, WorkflowId, fldDateCreated, t.wf_type_pmrv, s.wf_status_pmrv\n" +
            "  from wf\n" +
            "  left join mig_wf_status s on wf.ETSWAPWorkflowType like s.wf_type_etswap and wf.ETSWAPWorkflowStatus = s.wf_status_etswap\n" +
            "  left join mig_wf t on t.wf_type_etswap = wf.ETSWAPWorkflowType\n" +
            " where wf.ETSWAPWorkflowType in ('INPermitApplication','INNotification','INVariation','INTransferPartA','INTransferPartB','INSurrender','INRevocation'/*,'INDetermination','INNewApplicant',*/)\n" +
            "   and wf.fldDateCreated >= '2021/01/01'\n" +
            "   and wf_status_pmrv is not null ";

    @Override
    public List<String> migrate(String ids) {
        Map<String, Account> allMigratedAccounts = accountRepository.findByMigratedAccountIdIsNotNull().stream()
                .collect(Collectors.toMap(Account::getMigratedAccountId, acc -> acc));
        List<WorkflowEntityVO> etsWorkflows = queryEts(ids);

        // Convert and save to Request table
        List<Request> requests = requestMapper.toRequests(etsWorkflows, allMigratedAccounts);
        requestRepository.saveAll(requests);

        return ObjectUtils.isEmpty(ids)
                ? List.of()
                : Arrays.stream(ids.split(",")).collect(Collectors.toSet()).stream()
                    .filter(id -> etsWorkflows.stream().noneMatch(w -> w.getEmitterId().equals(id)))
                    .map(id -> "no workflow for account id: " + id)
                    .collect(Collectors.toList());
    }

    private List<WorkflowEntityVO> queryEts(String ids) {
        String sql = ObjectUtils.isEmpty(ids)
                ? QUERY_BASE
                : QUERY_BASE + " and fldEmitterID IN (" +
                    Arrays.stream(ids.split(","))
                            .filter(Objects::nonNull)
                            .map(id -> "'" + id.trim() + "'").collect(Collectors.joining(","))
                    + ")";

        // Get all history workflows from ETSWAP
        return migrationJdbcTemplate.query(sql, new WorkflowEntityVOMapper());
    }

    @Override
    public String getResource() {
        return "workflow";
    }
}
