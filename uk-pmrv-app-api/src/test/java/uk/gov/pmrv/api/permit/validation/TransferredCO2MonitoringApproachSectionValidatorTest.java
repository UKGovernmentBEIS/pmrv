package uk.gov.pmrv.api.permit.validation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyCollection;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import uk.gov.pmrv.api.permit.domain.Permit;
import uk.gov.pmrv.api.permit.domain.PermitContainer;
import uk.gov.pmrv.api.permit.domain.PermitValidationResult;
import uk.gov.pmrv.api.permit.domain.PermitViolation;
import uk.gov.pmrv.api.permit.domain.common.MeasuredEmissionsSamplingFrequency;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDeviceOrMethod;
import uk.gov.pmrv.api.permit.domain.measurementdevices.MeasurementDevicesOrMethods;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproachType;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.MonitoringApproaches;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.calculation.CalculationMonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.HighestRequiredTier;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.common.NoHighestRequiredTierJustification;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.TransferredCO2MonitoringApproach;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.accountingemissions.AccountingEmissions;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.accountingemissions.AccountingEmissionsDetails;
import uk.gov.pmrv.api.permit.domain.monitoringapproaches.transferredco2.accountingemissions.AccountingEmissionsTier;

@ExtendWith(MockitoExtension.class)
class TransferredCO2MonitoringApproachSectionValidatorTest {

    @InjectMocks
    private TransferredCO2MonitoringApproachSectionValidator validator;

    @Mock
    private PermitReferenceService permitReferenceService;

    private static String deviceId1, deviceId2;
    private static MeasurementDeviceOrMethod device1, device2;
    private static UUID file1;

    @BeforeAll
    static void setup() {
        deviceId1 = UUID.randomUUID().toString();
        deviceId2 = UUID.randomUUID().toString();
        file1 = UUID.randomUUID();

        device1 = MeasurementDeviceOrMethod.builder().id(deviceId1).reference("deviceId1").build();
        device2 = MeasurementDeviceOrMethod.builder().id(deviceId2).reference("deviceId2").build();
    }

    @Test
    void validatePermitContainer_whenNoAccountingEmissions_thenAllow() {
        final Permit permit = buildPermit(null);

        final PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        final PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, never()).validateExistenceInPermit(anyCollection(), anyCollection(), any());
    }

    @Test
    void validatePermitContainer_whenNoTransferredCO2ApproachExists_thenAllow() {
        final Permit permit = Permit.builder()
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                        List.of(device1, device2)).build())
                .monitoringApproaches(MonitoringApproaches.builder()
                        .monitoringApproaches(
                                Map.of(MonitoringApproachType.CALCULATION, CalculationMonitoringApproach.builder().build()))
                        .build())
                .build();

        final PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        final PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, never()).validateExistenceInPermit(anyCollection(), anyCollection(), any());
    }

    @Test
    void validatePermitContainer_whenChemicallyBound_thenAllow() {
        final Permit permit = buildPermit(AccountingEmissions.builder()
                .chemicallyBound(true)
                .build());

        final PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        final PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, never()).validateExistenceInPermit(anyCollection(), anyCollection(), any());
    }

    @Test
    void validatePermitContainer_whenNoEmissions_thenPermitViolation() {
        final Permit permit = buildPermit(AccountingEmissions.builder()
                .chemicallyBound(false)
                .build());

        final PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        final PermitValidationResult result = validator.validate(permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
                new PermitViolation(AccountingEmissions.class.getSimpleName(),
                        PermitViolation.PermitViolationMessage.ACCOUNTING_EMISSIONS_NOT_FOUND));
        verify(permitReferenceService, never()).validateExistenceInPermit(anyCollection(), anyCollection(), any());
    }

    @Test
    void validatePermitContainer_whenNotChemicallyBoundAndTier4_thenAllow() {
        AccountingEmissions accountingEmissions = AccountingEmissions.builder()
                .chemicallyBound(false)
                .accountingEmissionsDetails(AccountingEmissionsDetails.builder()
                        .measurementDevicesOrMethods(Set.of(deviceId1, deviceId2))
                        .samplingFrequency(MeasuredEmissionsSamplingFrequency.ANNUALLY)
                        .tier(AccountingEmissionsTier.TIER_4)
                        .highestRequiredTier(HighestRequiredTier.builder().build())
                        .build())
                .build();
        final Permit permit = buildPermit(accountingEmissions);

        final PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        final PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
                accountingEmissions.getAccountingEmissionsDetails().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validatePermitContainer_whenInvalidDevice_thenPermitViolation() {
        final String deviceId3 = UUID.randomUUID().toString();
        final AccountingEmissions accountingEmissions = AccountingEmissions.builder()
                .chemicallyBound(false)
                .accountingEmissionsDetails(AccountingEmissionsDetails.builder()
                        .measurementDevicesOrMethods(Set.of(deviceId1, deviceId2, deviceId3))
                        .samplingFrequency(MeasuredEmissionsSamplingFrequency.ANNUALLY)
                        .tier(AccountingEmissionsTier.TIER_4)
                        .highestRequiredTier(HighestRequiredTier.builder().build())
                        .build())
                .build();
        final Permit permit = buildPermit(accountingEmissions);

        final PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        when(permitReferenceService.validateExistenceInPermit(permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
                accountingEmissions.getAccountingEmissionsDetails().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST))
                .thenReturn(Optional.of(Pair.of(PermitViolation.PermitViolationMessage.INVALID_MEASUREMENT_DEVICE_OR_METHOD,
                        List.of(deviceId3))));

        final PermitValidationResult result = validator.validate(permitContainer);
        final List<PermitViolation> permitViolations = result.getPermitViolations();

        assertFalse(result.isValid());
        assertFalse(permitViolations.isEmpty());
        assertThat(permitViolations.size()).isEqualTo(1);
        assertThat(permitViolations).containsExactly(
                new PermitViolation(AccountingEmissions.class.getSimpleName(),
                        PermitViolation.PermitViolationMessage.INVALID_MEASUREMENT_DEVICE_OR_METHOD,
                        List.of(deviceId3).toArray()));
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
                accountingEmissions.getAccountingEmissionsDetails().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validatePermitContainer_whenNotChemicallyBoundAndHighTier_thenAllow() {
        final AccountingEmissions accountingEmissions = AccountingEmissions.builder()
                .chemicallyBound(false)
                .accountingEmissionsDetails(AccountingEmissionsDetails.builder()
                        .measurementDevicesOrMethods(Set.of(deviceId1, deviceId2))
                        .samplingFrequency(MeasuredEmissionsSamplingFrequency.ANNUALLY)
                        .tier(AccountingEmissionsTier.TIER_3)
                        .highestRequiredTier(HighestRequiredTier.builder()
                                .isHighestRequiredTier(Boolean.TRUE)
                                .build())
                        .build())
                .build();
        final Permit permit = buildPermit(accountingEmissions);

        final PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .build();

        final PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
                accountingEmissions.getAccountingEmissionsDetails().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    @Test
    void validatePermitContainer_whenNotChemicallyBoundAndNotHighTier_thenAllow() {
        final AccountingEmissions accountingEmissions = AccountingEmissions.builder()
                .chemicallyBound(false)
                .accountingEmissionsDetails(AccountingEmissionsDetails.builder()
                        .measurementDevicesOrMethods(Set.of(deviceId1, deviceId2))
                        .samplingFrequency(MeasuredEmissionsSamplingFrequency.ANNUALLY)
                        .tier(AccountingEmissionsTier.TIER_3)
                        .highestRequiredTier(HighestRequiredTier.builder()
                                .isHighestRequiredTier(Boolean.FALSE)
                                .noHighestRequiredTierJustification(NoHighestRequiredTierJustification.builder()
                                        .isCostUnreasonable(Boolean.TRUE)
                                        .files(Set.of(file1))
                                        .build())
                                .build())
                        .build())
                .build();
        final Permit permit = buildPermit(accountingEmissions);

        final PermitContainer permitContainer = PermitContainer
                .builder().permit(permit)
                .permitAttachments(Map.of(file1, "file1"))
                .build();

        final PermitValidationResult result = validator.validate(permitContainer);

        assertTrue(result.isValid());
        assertTrue(result.getPermitViolations().isEmpty());
        verify(permitReferenceService, times(1)).validateExistenceInPermit(
                permitContainer.getPermit().getMeasurementDevicesOrMethodsIds(),
                accountingEmissions.getAccountingEmissionsDetails().getMeasurementDevicesOrMethods(),
                PermitReferenceService.Rule.MEASUREMENT_DEVICES_OR_METHODS_EXIST);
        verifyNoMoreInteractions(permitReferenceService);
    }

    private Permit buildPermit(AccountingEmissions accountingEmissions) {
        return Permit.builder()
                .measurementDevicesOrMethods(MeasurementDevicesOrMethods.builder().measurementDevicesOrMethods(
                        List.of(device1, device2)).build())
                .monitoringApproaches(MonitoringApproaches.builder().monitoringApproaches(
                        Map.of(MonitoringApproachType.TRANSFERRED_CO2, TransferredCO2MonitoringApproach.builder()
                                .accountingEmissions(accountingEmissions).build()))
                        .build())
                .build();
    }
}
