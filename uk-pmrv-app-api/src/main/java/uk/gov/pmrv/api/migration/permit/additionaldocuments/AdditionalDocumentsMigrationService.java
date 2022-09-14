package uk.gov.pmrv.api.migration.permit.additionaldocuments;

import lombok.RequiredArgsConstructor;

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
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class AdditionalDocumentsMigrationService implements PermitSectionMigrationService<AdditionalDocuments> {

    private final EtsPermitFileAttachmentQueryService etsPermitFileAttachmentQueryService;
    private final EtsFileAttachmentMapper etsFileAttachmentMapper = Mappers.getMapper(EtsFileAttachmentMapper.class);

    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, Map<Long, PermitMigrationContainer> permits) {
        Map<String, List<EtsFileAttachment>> attachments = etsPermitFileAttachmentQueryService
                .query(new ArrayList<>(accountsToMigratePermit.keySet()), EtsFileAttachmentType.ADDITIONAL_DOCUMENTS);

        accountsToMigratePermit.forEach((etsAccId, account) -> {
            List<EtsFileAttachment> sectionAttachments = attachments.getOrDefault(etsAccId, List.of());
            // populate section
            PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());
            PermitContainer permitContainer = permitMigrationContainer.getPermitContainer();

            permitContainer.getPermit().setAdditionalDocuments(toAdditionalDocuments(sectionAttachments));

            sectionAttachments.forEach(file ->
                    permitContainer.getPermitAttachments().put(file.getUuid(), file.getUploadedFileName()));
            permitMigrationContainer.getFileAttachments()
                    .addAll(etsFileAttachmentMapper.toFileAttachments(sectionAttachments));
        });
    }

    @Override
    public Map<String, AdditionalDocuments> queryEtsSection(List<String> accountIds) {
        throw new UnsupportedOperationException();
    }

    private AdditionalDocuments toAdditionalDocuments(List<EtsFileAttachment> sectionAttachments) {
        return AdditionalDocuments.builder()
                .exist(!ObjectUtils.isEmpty(sectionAttachments))
                .documents(sectionAttachments.stream()
                        .map(EtsFileAttachment::getUuid).collect(Collectors.toSet()))
                .build();
    }
}
