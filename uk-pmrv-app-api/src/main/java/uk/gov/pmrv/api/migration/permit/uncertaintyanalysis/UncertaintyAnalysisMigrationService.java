package uk.gov.pmrv.api.migration.permit.uncertaintyanalysis;

import lombok.RequiredArgsConstructor;

import org.apache.commons.lang3.ObjectUtils;

import org.mapstruct.factory.Mappers;

import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;

import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.files.EtsFileAttachment;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentMapper;
import uk.gov.pmrv.api.migration.files.EtsFileAttachmentType;
import uk.gov.pmrv.api.migration.permit.PermitMigrationContainer;
import uk.gov.pmrv.api.migration.permit.PermitSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.attachments.EtsPermitFileAttachmentQueryService;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.uncertaintyanalysis.UncertaintyAnalysis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class UncertaintyAnalysisMigrationService implements PermitSectionMigrationService<UncertaintyAnalysis> {

    private final EtsPermitFileAttachmentQueryService etsPermitFileAttachmentQueryService;
    private final EtsFileAttachmentMapper etsFileAttachmentMapper = Mappers.getMapper(EtsFileAttachmentMapper.class);

    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit,
                                Map<Long, PermitMigrationContainer> permits) {

        Map<String, List<EtsFileAttachment>> etsUncertaintyAnalysis = etsPermitFileAttachmentQueryService
                .query(new ArrayList<>(accountsToMigratePermit.keySet()), EtsFileAttachmentType.UNCERTAINTY_ANALYSIS);

        accountsToMigratePermit.forEach((etsAccId, account) -> {
            List<EtsFileAttachment> sectionFiles = etsUncertaintyAnalysis.getOrDefault(etsAccId, List.of());

            UncertaintyAnalysis analysis = ObjectUtils.isEmpty(sectionFiles)
                    ? UncertaintyAnalysis.builder().exist(false).build()
                    : this.mapEtsFileAttachmentsToSiteDiagrams(sectionFiles);

            // Populate uncertainty analysis
            PermitMigrationContainer permitMigrationContainer = permits.get(account.getId());
            PermitContainer permitContainer = permitMigrationContainer.getPermitContainer();
            permitContainer.getPermit().setUncertaintyAnalysis(analysis);

            // Populate attachments
            if(!ObjectUtils.isEmpty(sectionFiles)){
                sectionFiles.forEach(file -> permitContainer.getPermitAttachments().put(file.getUuid(), file.getUploadedFileName()));
                permitMigrationContainer.getFileAttachments()
                        .addAll(etsFileAttachmentMapper.toFileAttachments(sectionFiles));
            }
        });
    }

    @Override
    public Map<String, UncertaintyAnalysis> queryEtsSection(List<String> accountIds) {
        throw new UnsupportedOperationException();
    }

    private UncertaintyAnalysis mapEtsFileAttachmentsToSiteDiagrams(final List<EtsFileAttachment> files) {
        return UncertaintyAnalysis.builder()
                .exist(true)
                .attachments(files.stream()
                        .map(EtsFileAttachment::getUuid).collect(Collectors.toSet())).build();
    }
}
