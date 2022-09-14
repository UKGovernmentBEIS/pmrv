import { PermitVariationModification } from 'pmrv-api';

export const nonSignificantChanges: { [key in PermitVariationModification['type']]?: string } = {
  COMPANY_NAME_NOT_DUE_TO_OWNERSHIP_CHANGE: 'Change of company name not due to a change of ownership',
  INSTALLATION_NAME: 'Change of installation name',
  REGISTERED_OFFICE_ADDRESS: 'Change of registered office address',
  INSTALLATION_ADDRESS: 'Change of installation address',
  GRID_REFERENCE: 'Change of grid reference',
  METER_RENAMING: 'Renaming a meter',
  METER_LOCATION_DESCRIPTION:
    'Change to the description of a meter location without a change to the positions and no reassessment of the monitoring plan',
  RELEASE_EMISSION_POINT_DESCRIPTION:
    'A change to the name of a release point/emission point description in, but no actual change to the plant or other equipment',
  OTHER_NON_SIGNFICANT: 'Other non-significant changes to the Monitoring Plan or the Monitoring Methodology Plan',
};

export const significantChangesMonitoringPlan: { [key in PermitVariationModification['type']]?: string } = {
  INSTALLATION_CATEGORY:
    'Changes to the category of the installation where such changes require a change to the monitoring methodology or lead to a change of the applicable materiality level pursuant to Article 23 of Implementing Regulation (EU) 2018/2067',
  NOTWITHSTANDING_ARTICLE_47_8:
    'Notwithstanding Article 47(8), changes regarding whether the installation is considered an ‘installation with low emissions’',
  EMISSION_SOURCES: 'Changes to emission sources',
  CALCULATION_TO_MEASUREMENT_METHODOLOGIES:
    'A change from calculation-based to measurement-based methodologies, or vice versa, or from a fall-back methodology to a tier-based methodology for determining emissions or vice versa',
  TIER_APPLIED: 'A change in the tier applied',
  NEW_SOURCE_STREAMS: 'The introduction of new source streams',
  SOURCE_STREAMS_CATEGORISATION:
    'A change in the categorisation of source streams – between major, minor or de-minimis source streams where such a change requires a change to the monitoring methodology',
  METHODS:
    'The introduction of new methods or changes to existing methods related to sampling, analysis or calibration, where this has a direct impact on the accuracy of emissions data',
  QUANTIFICATION_METHODOLOGY_FOR_EMISSIONS:
    'The implementation or adaption of a quantification methodology for emissions from leakage at storage sites.',
  OTHER_MONITORING_PLAN: 'Other significant modifications to the Monitoring Plan',
};

export const significantChangesMonitoringMethodologyPlan: { [key in PermitVariationModification['type']]?: string } = {
  INSTALLATION_SUB:
    'Modifications resulting from changes to the installation, in particular new sub-installations, changes to the boundaries of existing sub-installations or closures of sub installations.',
  MONITORING_REPORT_METHODOLOGY_4_4_OR_4_6:
    'A switch from a monitoring reporting methodology laid down in sections 4.4 to 4.6 of Annex VII to another methodology laid down in those sections.',
  DEFAULT_VALUE_OR_ESTIMATION_METHOD:
    'The change of a default value or estimation method laid down in the monitoring methodology plan.',
  COMPETENT_AUTHORITY:
    'Changes requested by the competent authority to ensure conformity of the monitoring methodology plan with the requirements of this regulation.',
  OTHER_MONITORING_METHODOLOGY_PLAN: 'Other significant modifications to the the Monitoring Methodology Plan',
};
