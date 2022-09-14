package uk.gov.pmrv.api.migration.permit.sitediagram;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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
import uk.gov.pmrv.api.permit.domain.sitediagram.SiteDiagrams;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Log4j2
@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
public class SiteDiagramsMigrationService implements PermitSectionMigrationService<SiteDiagrams> {

    private final EtsPermitFileAttachmentQueryService etsPermitFileAttachmentQueryService;
    private final EtsFileAttachmentMapper etsFileAttachmentMapper = Mappers.getMapper(EtsFileAttachmentMapper.class);
    
    @Override
    public void populateSection(Map<String, Account> accountsToMigratePermit, 
            Map<Long, PermitMigrationContainer> permits) {

        Map<String, List<EtsFileAttachment>> etsSiteDiagrams = etsPermitFileAttachmentQueryService
            .query(new ArrayList<>(accountsToMigratePermit.keySet()), EtsFileAttachmentType.SITE_DIAGRAM);
        
        etsSiteDiagrams
            .forEach((etsAccId, files) -> {
                // populate files
                PermitMigrationContainer permitMigrationContainer = permits.get(accountsToMigratePermit.get(etsAccId).getId());
                PermitContainer permitContainer = permitMigrationContainer.getPermitContainer();
                permitContainer.getPermit().setSiteDiagrams(this.mapEtsFileAttachmentsToSiteDiagrams(files));
                
                // populate attachments
                files.forEach(file -> permitContainer.getPermitAttachments().put(file.getUuid(), file.getUploadedFileName()));
                permitMigrationContainer.getFileAttachments().addAll(etsFileAttachmentMapper.toFileAttachments(files));
            });
    }
    
    private SiteDiagrams mapEtsFileAttachmentsToSiteDiagrams(final List<EtsFileAttachment> files) {
        return SiteDiagrams.builder().siteDiagrams(files.stream()
            .map(EtsFileAttachment::getUuid).collect(Collectors.toSet())).build();
    }

    @Override
    public Map<String, SiteDiagrams> queryEtsSection(List<String> accountIds) {
        throw new UnsupportedOperationException();
    }
}
