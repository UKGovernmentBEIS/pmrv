import {
  CalculationMonitoringApproach,
  CalculationSourceStreamCategoryAppliedTier,
  FallbackMonitoringApproach,
  FallbackSourceStreamCategoryAppliedTier,
  InherentCO2MonitoringApproach,
  MeasMonitoringApproach,
  MeasSourceStreamCategoryAppliedTier,
  N2OMonitoringApproach,
  N2OSourceStreamCategoryAppliedTier,
  PFCMonitoringApproach,
  PFCSourceStreamCategoryAppliedTier,
  TransferredCO2MonitoringApproach,
} from 'pmrv-api';
// Source stream Category applied tier constants
export const calculationSourceStreamMapper: Record<keyof CalculationSourceStreamCategoryAppliedTier, string> = {
  netCalorificValue: 'CALCULATION_Calorific',
  activityData: 'CALCULATION_Activity_Data',
  biomassFraction: 'CALCULATION_Biomass_Fraction',
  carbonContent: 'CALCULATION_Carbon_Content',
  conversionFactor: 'CALCULATION_Conversion_Factor',
  emissionFactor: 'CALCULATION_Emission_Factor',
  oxidationFactor: 'CALCULATION_Oxidation_Factor',
  sourceStreamCategory: 'CALCULATION_Category',
};

const N2OSourceStreamMapper: Record<keyof N2OSourceStreamCategoryAppliedTier, string> = {
  appliedStandard: 'N2O_Applied_Standard',
  measuredEmissions: 'N2O_Measured_Emissions',
  sourceStreamCategory: 'N2O_Category',
};

const fallbackStreamMapper: Record<keyof FallbackSourceStreamCategoryAppliedTier, string> = {
  sourceStreamCategory: 'FALLBACK_Category',
};

const measurementStreamMapper: Record<keyof MeasSourceStreamCategoryAppliedTier, string> = {
  appliedStandard: 'MEASUREMENT_Applied_Standard',
  measuredEmissions: 'MEASUREMENT_Measured_Emissions',
  sourceStreamCategory: 'MEASUREMENT_Category',
};

const PFCSourceStreamMapper: Record<keyof PFCSourceStreamCategoryAppliedTier, string> = {
  activityData: 'PFC_Activity_Data',
  emissionFactor: 'PFC_Emission_Factor',
  sourceStreamCategory: 'PFC_Category',
};

// Monitor appproach constants
const calculationMonitoringApproachMapper: Record<
  keyof Pick<CalculationMonitoringApproach, 'approachDescription' | 'samplingPlan'>,
  string
> = {
  approachDescription: 'CALCULATION_Description',
  samplingPlan: 'CALCULATION_Plan',
};

const N2OMonitoringApproachMapper: Record<
  keyof Omit<N2OMonitoringApproach, 'sourceStreamCategoryAppliedTiers' | 'type'>,
  string
> = {
  approachDescription: 'N2O_Description',
  emissionDetermination: 'N2O_Emission',
  gasFlowCalculation: 'N2O_Gas',
  nitrousOxideConcentrationDetermination: 'N2O_Concentration',
  nitrousOxideEmissionsDetermination: 'N2O_Emissions',
  operationalManagement: 'N2O_Operational',
  quantityMaterials: 'N2O_Materials',
  quantityProductDetermination: 'N2O_Product',
  referenceDetermination: 'N2O_Reference',
};

const fallbackMonitoringApproachMapper: Record<
  keyof Omit<FallbackMonitoringApproach, 'sourceStreamCategoryAppliedTiers' | 'type' | 'justification'>,
  string
> = {
  annualUncertaintyAnalysis: 'FALLBACK_Uncertainty',
  approachDescription: 'FALLBACK_Description',
};

const measMonitoringApproachMapper: Record<
  keyof Omit<MeasMonitoringApproach, 'type' | 'sourceStreamCategoryAppliedTiers'>,
  string
> = {
  approachDescription: 'MEASUREMENT_Description',
  biomassEmissions: 'MEASUREMENT_Biomass',
  corroboratingCalculations: 'MEASUREMENT_Corroborating',
  emissionDetermination: 'MEASUREMENT_Emission',
  gasFlowCalculation: 'MEASUREMENT_Gasflow',
  referencePeriodDetermination: 'MEASUREMENT_Reference',
};

const inherentCO2MonitoringApproachMapper: Record<
  keyof Pick<InherentCO2MonitoringApproach, 'approachDescription'>,
  string
> = {
  approachDescription: 'INHERENT_CO2_Description',
};

const transferredCO2MonitoringApproachMapper: Record<keyof Omit<TransferredCO2MonitoringApproach, 'type'>, string> = {
  accountingEmissions: 'TRANSFERRED_CO2_Accounting',
  approachDescription: 'TRANSFERRED_CO2_Description',
  deductionsToAmountOfTransferredCO2: 'TRANSFERRED_CO2_Deductions',
  procedureForLeakageEvents: 'TRANSFERRED_CO2_Leakage',
  quantificationMethodologies: 'TRANSFERRED_CO2_Quantification',
  receivingTransferringInstallations: 'TRANSFERRED_CO2_Installations',
  temperaturePressure: 'TRANSFERRED_CO2_Temperature',
  transferOfCO2: 'TRANSFERRED_CO2_Transfer',
};

const PFCMonitoringApproachMapper: Record<
  keyof Omit<PFCMonitoringApproach, 'type' | 'sourceStreamCategoryAppliedTiers'>,
  string
> = {
  approachDescription: 'PFC_Description',
  cellAndAnodeTypes: 'PFC_Types',
  collectionEfficiency: 'PFC_Efficiency',
  tier2EmissionFactor: 'PFC_Tier2EmissionFactor',
};

// General mappers

export const approachMapper: Record<string, any> = {
  CALCULATION: calculationMonitoringApproachMapper,
  N2O: N2OMonitoringApproachMapper,
  FALLBACK: fallbackMonitoringApproachMapper,
  MEASUREMENT: measMonitoringApproachMapper,
  INHERENT_CO2: inherentCO2MonitoringApproachMapper,
  TRANSFERRED_CO2: transferredCO2MonitoringApproachMapper,
  PFC: PFCMonitoringApproachMapper,
};

export const sourceStreamMapper: Record<string, any> = {
  CALCULATION: calculationSourceStreamMapper,
  N2O: N2OSourceStreamMapper,
  FALLBACK: fallbackStreamMapper,
  MEASUREMENT: measurementStreamMapper,
  PFC: PFCSourceStreamMapper,
};

export const reviewSectionsCompletedStandardInitialized = {
  PERMIT_TYPE: true,
  INSTALLATION_DETAILS: true,
  FUELS_AND_EQUIPMENT: true,
  DEFINE_MONITORING_APPROACHES: true,
  UNCERTAINTY_ANALYSIS: true,
  MANAGEMENT_PROCEDURES: true,
  MONITORING_METHODOLOGY_PLAN: true,
  ADDITIONAL_INFORMATION: true,
  CONFIDENTIALITY_STATEMENT: true,
  determination: true,
};
