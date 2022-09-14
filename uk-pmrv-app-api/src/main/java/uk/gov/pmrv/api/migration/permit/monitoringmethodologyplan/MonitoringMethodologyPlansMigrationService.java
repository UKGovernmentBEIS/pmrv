package uk.gov.pmrv.api.migration.permit.monitoringmethodologyplan;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentMapper;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentType;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.attachments.EtsPermitFileAttachmentQueryService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.MonitoringMethodologyPlans;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class MonitoringMethodologyPlansMigrationService implements PermitSectionMigrationService<MonitoringMethodologyPlans> {

    private final EtsPermitFileAttachmentQueryService etsPermitFileAttachmentQueryService;
    private final EtsFileAttachmentMapper etsFileAttachmentMapper = Mappers.getMapper(EtsFileAttachmentMapper.class);

    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        Map<String, List<EtsFileAttachment>> etsPlans = etsPermitFileAttachmentQueryService
            .query(new ArrayList<>(accountsToMigratePermit.keySet()), EtsFileAttachmentType.MONITORING_METHODOLOGY_PLAN);

        accountsToMigratePermit.forEach((etsAccId, account) -> {
            List<EtsFileAttachment> etsAccountPlans = etsPlans.getOrDefault(etsAccId, List.of());

            PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());
            PermitContainer permitContainer = permitMigrationContainer.getPermitContainer();
            permitContainer.getPermit().setMonitoringMethodologyPlans(toMonitoringMethodologyPlans(etsAccountPlans));

            etsAccountPlans.forEach(file ->
                permitContainer.getPermitAttachments().put(file.getUuid(), file.getUploadedFileName()));
            permitMigrationContainer.getFileAttachments()
                .addAll(etsFileAttachmentMapper.toFileAttachments(etsAccountPlans));
        });
    }

    private MonitoringMethodologyPlans toMonitoringMethodologyPlans(List<EtsFileAttachment> plans) {
        return MonitoringMethodologyPlans.builder()
            .exist(!ObjectUtils.isEmpty(plans))
            .plans(plans.stream()
                .map(EtsFileAttachment::getUuid).collect(Collectors.toSet()))
            .build();
    }

    @Override
    public Map<String, MonitoringMethodologyPlans> queryEtsSection(List<String> accountIds) {
        throw new UnsupportedOperationException();
    }
}
