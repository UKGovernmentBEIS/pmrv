package uk.gov.pmrv.api.workflow.request.flow.aer.handler;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.account.domain.dto.LocationOnShoreDTO;
import uk.gov.pmrv.api.account.domain.enumeration.LegalEntityType;
import uk.gov.pmrv.api.account.domain.enumeration.LocationType;
import uk.gov.pmrv.api.account.domain.installationoperatordetails.InstallationOperatorDetails;
import uk.gov.pmrv.api.account.service.InstallationOperatorDetailsQueryService;
import uk.gov.pmrv.api.common.domain.dto.AddressDTO;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoint;
import uk.gov.pmrv.api.permit.domain.emissionpoints.EmissionPoints;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSource;
import uk.gov.pmrv.api.permit.domain.emissionsources.EmissionSources;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.CapacityUnit;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivities;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivity;
import uk.gov.pmrv.api.permit.domain.regulatedactivities.RegulatedActivityType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStream;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreamType;
import uk.gov.pmrv.api.permit.domain.sourcestreams.SourceStreams;
import uk.gov.pmrv.api.permit.service.PermitQueryService;
import uk.gov.pmrv.api.reporting.domain.Aer;
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivities;
import uk.gov.pmrv.api.reporting.domain.regulatedactivities.AerRegulatedActivity;
import uk.gov.pmrv.api.workflow.request.core.domain.Request;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskPayloadType;
import uk.gov.pmrv.api.workflow.request.core.domain.enumeration.RequestTaskType;
import uk.gov.pmrv.api.workflow.request.flow.aer.domain.AerApplicationSubmitRequestTaskPayload;
import uk.gov.pmrv.api.workflow.request.flow.aer.service.init.AerEmissionPointsInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.aer.service.init.AerEmissionSourcesInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.aer.service.init.AerMonitoringApproachTypesInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.aer.service.init.AerRegulatedActivitiesInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.aer.service.init.AerSectionInitializationService;
import uk.gov.pmrv.api.workflow.request.flow.aer.service.init.AerSourceStreamsInitializationService;

@ExtendWith(MockitoExtension.class)
class AerApplicationSubmitInitializerTest {

    @InjectMocks
    private AerApplicationSubmitInitializer initializer;

    @Mock
    private InstallationOperatorDetailsQueryService installationOperatorDetailsQueryService;

    @Mock
    private PermitQueryService permitQueryService;

    @Spy
    private ArrayList<AerSectionInitializationService> aerSectionInitializationServices;

    @Mock
    private AerEmissionPointsInitializationService aerEmissionPointsInitService;

    @Mock
    private AerMonitoringApproachTypesInitializationService aerMonitoringApproachTypesInitService;

    @Mock
    private AerEmissionSourcesInitializationService aerEmissionSourcesInitService;

    @Mock
    private AerSourceStreamsInitializationService aerSourceStreamsInitService;

    @Mock
    private AerRegulatedActivitiesInitializationService aerRegulatedActivitiesInitializationService;

    @BeforeEach
    void setUp() {
        aerSectionInitializationServices.add(aerEmissionPointsInitService);
        aerSectionInitializationServices.add(aerMonitoringApproachTypesInitService);
        aerSectionInitializationServices.add(aerEmissionSourcesInitService);
        aerSectionInitializationServices.add(aerSourceStreamsInitService);
        aerSectionInitializationServices.add(aerRegulatedActivitiesInitializationService);
    }

    @Test
    void initializePayload() {
        final long accountId = 1L;
        final Request request = Request.builder().accountId(1L).build();
        final InstallationOperatorDetails installationOperatorDetails = InstallationOperatorDetails.builder()
                .installationName("Account name")
                .siteName("Site name")
                .installationLocation(LocationOnShoreDTO.builder()
                        .type(LocationType.ONSHORE)
                        .gridReference("ST330000")
                        .address(AddressDTO.builder()
                                .line1("line1")
                                .city("city")
                                .country("GB")
                                .postcode("postcode")
                                .build())
                        .build())
                .operator("le")
                .operatorType(LegalEntityType.LIMITED_COMPANY)
                .companyReferenceNumber("408812")
                .operatorDetailsAddress(AddressDTO.builder()
                        .line1("line1")
                        .city("city")
                        .country("GR")
                        .postcode("postcode")
                        .build())
                .build();
        final SourceStreams sourceStreams = SourceStreams.builder()
            .sourceStreams(List.of(
                SourceStream.builder()
                    .type(SourceStreamType.COMBUSTION_FLARES)
                    .reference("reference 1")
                    .build(),
                SourceStream.builder()
                    .type(SourceStreamType.CEMENT_CLINKER_CKD)
                    .reference("reference 2")
                    .build()))
            .build();

        final Set<MonitoringApproachType> monitoringApproachTypes = Set.of(
            MonitoringApproachType.CALCULATION
        );

        final MonitoringApproaches monitoringApproaches = MonitoringApproaches.builder()
            .monitoringApproaches(Map.of(MonitoringApproachType.CALCULATION, CalculationMonitoringApproach.builder().build()))
            .build();

        final EmissionSources emissionSources = EmissionSources.builder()
            .emissionSources(List.of(
                EmissionSource.builder()
                    .reference("reference 1")
                    .description("description 1")
                    .build(),
                EmissionSource.builder()
                    .reference("reference 2")
                    .description("description 2")
                    .build()
            )).build();

        final EmissionPoints emissionPoints = EmissionPoints.builder()
            .emissionPoints(List.of(EmissionPoint.builder().reference("ep ref 1").description("ep desc 1").build()))
            .build();

        final RegulatedActivities regulatedActivities = RegulatedActivities.builder().regulatedActivities(
            List.of(RegulatedActivity.builder()
                .type(RegulatedActivityType.COMBUSTION)
                .capacity(new BigDecimal(11))
                .capacityUnit(CapacityUnit.KW)
                .build())
        ).build();

        final Permit permit = Permit.builder()
            .sourceStreams(sourceStreams)
            .monitoringApproaches(monitoringApproaches)
            .emissionSources(emissionSources)
            .emissionPoints(emissionPoints)
            .regulatedActivities(regulatedActivities)
            .build();
        final PermitContainer permitContainer = PermitContainer.builder().permit(permit).build();

        final AerApplicationSubmitRequestTaskPayload expected = AerApplicationSubmitRequestTaskPayload.builder()
            .payloadType(RequestTaskPayloadType.AER_APPLICATION_SUBMIT_PAYLOAD)
            .installationOperatorDetails(installationOperatorDetails)
            .aer(Aer.builder()
                .sourceStreams(sourceStreams)
                .monitoringApproachTypes(monitoringApproachTypes)
                .emissionSources(emissionSources)
                .emissionPoints(emissionPoints)
                .regulatedActivities((AerRegulatedActivities.builder()
                    .regulatedActivities(List.of(AerRegulatedActivity.builder()
                        .type(RegulatedActivityType.COMBUSTION)
                        .capacity(new BigDecimal(11))
                        .capacityUnit(CapacityUnit.KW)
                        .build()))
                    .build()))
                .build()
            )
            .build();

        when(installationOperatorDetailsQueryService.getInstallationOperatorDetails(accountId)).thenReturn(installationOperatorDetails);
        when(permitQueryService.getPermitContainerByAccountId(accountId)).thenReturn(permitContainer);

        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setEmissionPoints(emissionPoints);
            return null;
        }).when(aerEmissionPointsInitService).initialize(any(Aer.class), eq(permit));

        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setMonitoringApproachTypes(monitoringApproachTypes);
            return null;
        }).when(aerMonitoringApproachTypesInitService).initialize(any(Aer.class), eq(permit));

        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setEmissionSources(emissionSources);
            return null;
        }).when(aerEmissionSourcesInitService).initialize(any(Aer.class), eq(permit));

        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setSourceStreams(sourceStreams);
            return null;
        }).when(aerSourceStreamsInitService).initialize(any(Aer.class), eq(permit));
        
        doAnswer(invocationOnMock -> {
            Aer aerParam = invocationOnMock.getArgument(0);
            aerParam.setRegulatedActivities(AerRegulatedActivities.builder()
                .regulatedActivities(List.of(AerRegulatedActivity.builder()
                    .type(RegulatedActivityType.COMBUSTION)
                    .capacity(new BigDecimal(11))
                    .capacityUnit(CapacityUnit.KW)
                    .build()))
                .build());
            return null;
        }).when(aerRegulatedActivitiesInitializationService).initialize(any(Aer.class), eq(permit));

        // Invoke
        AerApplicationSubmitRequestTaskPayload actual = (AerApplicationSubmitRequestTaskPayload)
            initializer.initializePayload(request);

        // Verify
        verify(installationOperatorDetailsQueryService, times(1)).getInstallationOperatorDetails(accountId);
        verify(permitQueryService, times(1)).getPermitContainerByAccountId(accountId);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    void getRequestTaskTypes() {
        // Invoke
        Set<RequestTaskType> actual = initializer.getRequestTaskTypes();

        // Verify
        assertThat(actual).isEqualTo(Set.of(RequestTaskType.AER_APPLICATION_SUBMIT));
    }
}
