package uk.gov.pmrv.api.migration.permit;

import lombok.RequiredArgsConstructor;
import org.mapstruct.factory.Mappers;
import org.springframework.boot.actuate.autoconfigure.endpoint.condition.ConditionalOnAvailableEndpoint;
import org.springframework.stereotype.Service;
import uk.gov.pmrv.api.account.domain.Account;
import uk.gov.pmrv.api.account.repository.AccountRepository;
import uk.gov.pmrv.api.account.transform.AccountToInstallationOperatorDetailsMapper;
import uk.gov.pmrv.api.migration.MigrationEndpoint;
import uk.gov.pmrv.api.migration.permit.abbreviations.AbbreviationsSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.activationdate.ActivationDateMigrationService;
import uk.gov.pmrv.api.migration.permit.additionaldocuments.AdditionalDocumentsMigrationService;
import uk.gov.pmrv.api.migration.permit.annualemissionstarget.AnnualEmissionsTargetMigrationService;
import uk.gov.pmrv.api.migration.permit.confidentialitystatement.ConfidentialityStatementMigrationService;
import uk.gov.pmrv.api.migration.permit.emissionSources.EmissionSourcesSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.emissionpoints.EmissionPointsSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.emissionsummaries.EmissionSummariesSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.envpermitsandlicences.EnvironmentalPermitsAndLicencesSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.installationcategory.EstimatedAnnualEmissionsSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.installationdesc.InstallationDescriptionSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.managementprocedures.ManagementProceduresMigrationService;
import uk.gov.pmrv.api.migration.permit.measurementdevices.MeasurementDevicesOrMethodsMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.MonitoringApproachesListMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.calculation.CalculationMonitoringApproachesMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.fallback.FallbackMonitoringApproachesMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.inherentco2.InherentCO2MonitoringApproachesMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.measurement.MeasMonitoringApproachesMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.n2o.N2OMonitoringApproachesMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.pfc.PFCMonitoringApproachesMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringapproaches.transferredco2.TransferredCO2MonitoringApproachesMigrationService;
import uk.gov.pmrv.api.migration.permit.monitoringmethodologyplan.MonitoringMethodologyPlansMigrationService;
import uk.gov.pmrv.api.migration.permit.regulatedactivities.RegulatedActivitiesSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.sitediagram.SiteDiagramsMigrationService;
import uk.gov.pmrv.api.migration.permit.sourcestreams.SourceStreamsSectionMigrationService;
import uk.gov.pmrv.api.migration.permit.type.PermitTypeMigrationService;
import uk.gov.pmrv.api.migration.permit.uncertaintyanalysis.UncertaintyAnalysisMigrationService;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.additionaldocuments.AdditionalDocuments;
import uk.gov.pmrv.api.permit.domain.managementprocedures.ManagementProcedures;
import uk.gov.pmrv.api.permit.domain.monitoringmethodologyplan.MonitoringMethodologyPlans;
import uk.gov.pmrv.api.permit.domain.uncertaintyanalysis.UncertaintyAnalysis;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@ConditionalOnAvailableEndpoint(endpoint = MigrationEndpoint.class)
class PermitBuilderService {

    private final AccountRepository accountRepository;
    private final EnvironmentalPermitsAndLicencesSectionMigrationService environmentalPermitsAndLicencesService;
    private final MonitoringMethodologyPlansMigrationService monitoringMethodologyPlansMigrationService;
    private final EstimatedAnnualEmissionsSectionMigrationService estimatedAnnualEmissionsSectionMigrationService;
    private final InstallationDescriptionSectionMigrationService installationDescriptionSectionMigrationService;
    private final RegulatedActivitiesSectionMigrationService regulatedActivitiesSectionMigrationService;
    private final ManagementProceduresMigrationService managementProceduresMigrationService;
    private final EmissionSourcesSectionMigrationService emissionSourcesSectionMigrationService;
    private final EmissionPointsSectionMigrationService emissionPointsSectionMigrationService;
    private final SourceStreamsSectionMigrationService sourceStreamsSectionMigrationService;
    private final MeasurementDevicesOrMethodsMigrationService measurementDevicesOrMethodsMigrationService;
    private final EmissionSummariesSectionMigrationService emissionSummariesSectionMigrationService;
    private final AbbreviationsSectionMigrationService abbreviationsSectionMigrationService;
    private final AdditionalDocumentsMigrationService additionalDocumentsMigrationService;
    private final ConfidentialityStatementMigrationService confidentialityStatementMigrationService;
    private final SiteDiagramsMigrationService siteDiagramsMigrationService;
    private final MonitoringApproachesListMigrationService monitoringApproachesListMigrationService;
    private final CalculationMonitoringApproachesMigrationService calculationMonitoringApproachesMigrationService;
    private final N2OMonitoringApproachesMigrationService n2OMonitoringApproachesMigrationService;
    private final PFCMonitoringApproachesMigrationService pfcMonitoringApproachesMigrationService;
    private final FallbackMonitoringApproachesMigrationService fallbackMonitoringApproachesMigrationService;
    private final MeasMonitoringApproachesMigrationService measMonitoringApproachesMigrationService;
    private final InherentCO2MonitoringApproachesMigrationService inherentCO2MonitoringApproachesMigrationService;
    private final TransferredCO2MonitoringApproachesMigrationService transferredCO2MonitoringApproachesMigrationService;
    private final UncertaintyAnalysisMigrationService uncertaintyAnalysisMigrationService;
    private final PermitTypeMigrationService permitTypeMigrationService;
    private final ActivationDateMigrationService activationDateMigrationService;
    private final AnnualEmissionsTargetMigrationService annualEmissionsTargetMigrationService;
    private static final AccountToInstallationOperatorDetailsMapper installationOperatorDetailsMapper = Mappers.getMapper(AccountToInstallationOperatorDetailsMapper.class);

    public Map<Long, PermitMigrationContainer> buildPermits(List<String> etsAccountIds, List<String> failedEntries) {
        Map<String, Account> migratedAccountsToMigratePermit = findMigratedAccountsToMigratePermits(etsAccountIds, failedEntries);
        
        if(migratedAccountsToMigratePermit.isEmpty()) {
            failedEntries.add("No migrated accounts found!");
            return Map.of();
        }
        
        // initialize permits
        Map<Long, PermitMigrationContainer> permits = new HashMap<>();
        initializePermits(migratedAccountsToMigratePermit.values(), permits);
        
        // Populate sections
        environmentalPermitsAndLicencesService.populateSection(migratedAccountsToMigratePermit, permits);
        monitoringMethodologyPlansMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        estimatedAnnualEmissionsSectionMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        installationDescriptionSectionMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        regulatedActivitiesSectionMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        managementProceduresMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        emissionSourcesSectionMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        sourceStreamsSectionMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        measurementDevicesOrMethodsMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        emissionPointsSectionMigrationService.populateSection(migratedAccountsToMigratePermit, permits);

        //emissions summaries migration should run after regulatedActivities, sourceStreams, emissionSources and emissionPoints
        emissionSummariesSectionMigrationService.populateSection(migratedAccountsToMigratePermit, permits);

        confidentialityStatementMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        abbreviationsSectionMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        additionalDocumentsMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        siteDiagramsMigrationService.populateSection(migratedAccountsToMigratePermit, permits);

        // Populate monitoring approaches
        monitoringApproachesListMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        calculationMonitoringApproachesMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        n2OMonitoringApproachesMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        pfcMonitoringApproachesMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        fallbackMonitoringApproachesMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        measMonitoringApproachesMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        inherentCO2MonitoringApproachesMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        transferredCO2MonitoringApproachesMigrationService.populateSection(migratedAccountsToMigratePermit, permits);
        uncertaintyAnalysisMigrationService.populateSection(migratedAccountsToMigratePermit, permits);

        //populate permit container properties other than permit
        permitTypeMigrationService.populateType(migratedAccountsToMigratePermit, permits);
        activationDateMigrationService.populateActivationDate(migratedAccountsToMigratePermit, permits);
        annualEmissionsTargetMigrationService.populateAnnualEmissionsTarget(migratedAccountsToMigratePermit, permits);

        Permit emptyPermit = Permit.builder()
                .additionalDocuments(AdditionalDocuments.builder()
                        .exist(false)
                        .documents(Set.of())
                        .build())
                .uncertaintyAnalysis(UncertaintyAnalysis.builder()
                        .exist(false)
                        .build())
                .monitoringMethodologyPlans(MonitoringMethodologyPlans.builder()
                        .exist(false)
                        .plans(Set.of())
                        .build())
                .managementProcedures(ManagementProcedures.builder()
                        .managementProceduresExist(false)
                        .build())
                .build();
        return permits.entrySet().stream()
                .filter(entry -> !entry.getValue().getPermitContainer().getPermit().equals(emptyPermit))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private void initializePermits(Collection<Account> migratedAccountsToMigratePermit,
            Map<Long, PermitMigrationContainer> permits) {
        migratedAccountsToMigratePermit
            .forEach(account -> 
                permits.put(account.getId(), 
                        PermitMigrationContainer.builder()
                            .permitContainer(PermitContainer.builder()
                                    .installationOperatorDetails(installationOperatorDetailsMapper.toPermitInstallationOperatorDetails(account))
                                    .permit(Permit.builder().build())
                                    .build())
                            .build()));
    }
    
    private Map<String, Account> findMigratedAccountsToMigratePermits(List<String> etsAccountIdsToMigrate, List<String> failedEntries) {
        Map<String, Account> allMigratedAccounts = accountRepository.findByMigratedAccountIdIsNotNull().stream()
                .collect(Collectors.toMap(Account::getMigratedAccountId, acc -> acc));
        
        if(etsAccountIdsToMigrate.isEmpty()) {
            return new HashMap<>(allMigratedAccounts);
        }
        
        Map<String, Account> accountsToMigratePermit = allMigratedAccounts.entrySet()
            .stream()
            .filter(entry -> etsAccountIdsToMigrate.contains(entry.getKey()))
            .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
        
        etsAccountIdsToMigrate.removeAll(accountsToMigratePermit.keySet());
        etsAccountIdsToMigrate.forEach(notFoundEtsAccountId -> 
                        failedEntries.add("Ets account id: " + notFoundEtsAccountId + " not found"));
        return accountsToMigratePermit;
    }
    
}
