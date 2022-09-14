import { PermitVariationApplicationSubmitRequestTaskPayload } from 'pmrv-api';

export const permitVariationPayload: PermitVariationApplicationSubmitRequestTaskPayload = {
  payloadType: 'PERMIT_VARIATION_APPLICATION_SUBMIT_PAYLOAD',
  permitType: 'GHGE',
  permit: {
    environmentalPermitsAndLicences: {
      exist: true,
      envPermitOrLicences: [
        {
          type: 'Environmental Protection Regulations',
          num: '2',
          issuingAuthority: 'Environment Agency',
          permitHolder: 'John Doe',
        },
      ],
    },
    estimatedAnnualEmissions: {
      quantity: 200000,
    },
    installationDescription: {
      mainActivitiesDesc: 'Description of the installation',
      siteDescription: 'Description of the site',
    },
    regulatedActivities: [
      {
        id: '16584088529630.8552025315237086',
        type: 'COMBUSTION',
        capacity: 20000,
        capacityUnit: 'MW_TH',
      },
      {
        id: '16584088529630.656780410872837',
        type: 'MINERAL_OIL_REFINING',
        capacity: 20000,
        capacityUnit: 'MW_TH',
      },
      {
        id: '16584088529630.23897723103057866',
        type: 'CAPTURE_OF_GREENHOUSE_GASES_UNDER_DIRECTIVE',
        capacity: 200000,
        capacityUnit: 'MW_TH',
      },
    ],
    monitoringMethodologyPlans: {
      exist: true,
      plans: ['299d81ff-706e-4f44-9357-7ef214db203b'],
    },
    sourceStreams: [
      {
        id: '16584088728380.004297921475426181',
        reference: 'F1',
        description: 'FUEL_GAS',
        type: 'BULK_ORGANIC_CHEMICALS_MASS_BALANCE_METHODOLOGY',
      },
      {
        id: '16584088843600.33461206108958286',
        reference: 'F2',
        description: 'BIOMASS',
        type: 'CARBON_BLACK_MASS_BALANCE_METHODOLOGY',
      },
    ],
    measurementDevicesOrMethods: [
      {
        id: '16584090261210.41985329955533346',
        reference: 'M1 Meter south boiler 1',
        type: 'BALANCE',
        measurementRange: '1000',
        meteringRangeUnits: 'litres',
        uncertaintySpecified: true,
        specifiedUncertaintyPercentage: 1.03,
        location: 'North terminal',
      },
      {
        id: '16584090430120.17597063201781316',
        reference: 'M1 Meter south boiler 2',
        type: 'BELLOWS_METER',
        measurementRange: '1000',
        meteringRangeUnits: 'litres',
        uncertaintySpecified: true,
        specifiedUncertaintyPercentage: 1.02,
        location: 'South terminal',
      },
    ],
    emissionSources: [
      {
        id: '16584089034220.18536180060525886',
        reference: 'S1',
        description: 'west side chimney',
      },
      {
        id: '16584089084370.4185121143116062',
        reference: 's2',
        description: 'Boiler',
      },
    ],
    emissionPoints: [
      {
        id: '16584089204130.20555942543417416',
        reference: 'EP1',
        description: 'west side chimney 2',
      },
      {
        id: '16584089257330.877914160029394',
        reference: 'EP2',
        description: 'east side chimney',
      },
    ],
    emissionSummaries: [
      {
        sourceStream: '16584088728380.004297921475426181',
        emissionSources: ['16584089034220.18536180060525886'],
        emissionPoints: ['16584089204130.20555942543417416'],
        excludedRegulatedActivity: false,
        regulatedActivity: '16584088529630.8552025315237086',
      },
      {
        sourceStream: '16584088843600.33461206108958286',
        emissionSources: ['16584089084370.4185121143116062'],
        emissionPoints: ['16584089257330.877914160029394'],
        excludedRegulatedActivity: false,
        regulatedActivity: '16584088529630.656780410872837',
      },
      {
        sourceStream: '16584088728380.004297921475426181',
        emissionSources: ['16584089034220.18536180060525886'],
        emissionPoints: ['16584089204130.20555942543417416'],
        excludedRegulatedActivity: false,
        regulatedActivity: '16584088529630.23897723103057866',
      },
      {
        sourceStream: '16584088843600.33461206108958286',
        emissionSources: ['16584089084370.4185121143116062'],
        emissionPoints: ['16584089257330.877914160029394'],
        excludedRegulatedActivity: true,
      },
    ],
    abbreviations: {
      exist: true,
      abbreviationDefinitions: [
        {
          abbreviation: 'NBA',
          definition: 'National basketball association',
        },
      ],
    },
    confidentialityStatement: {
      exist: true,
      confidentialSections: [
        {
          section: 'Confidentiality statement 1',
          explanation: 'dsdsds',
        },
      ],
    },
    additionalDocuments: {
      exist: true,
      documents: ['059a83bc-313a-4c80-96a0-e46db9afc7f8'],
    },
    monitoringApproaches: {
      N2O: {
        type: 'N2O',
        approachDescription: 'Approach description',
        emissionDetermination: {
          procedureDescription: 'Procedure description',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        referenceDetermination: {
          procedureDescription: 'Procedure description',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        operationalManagement: {
          procedureDescription: 'Procedure description',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        nitrousOxideEmissionsDetermination: {
          procedureDescription: 'Procedure description',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        nitrousOxideConcentrationDetermination: {
          procedureDescription: 'Procedure description',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        quantityProductDetermination: {
          procedureDescription: 'Procedure description',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        quantityMaterials: {
          procedureDescription: 'Procedure description',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        gasFlowCalculation: {
          exist: true,
          procedureForm: {
            procedureDescription: 'Procedure description',
            procedureDocumentName: 'procedure document',
            procedureReference: 'Procedure reference',
            diagramReference: 'Diagram reference',
            responsibleDepartmentOrRole: 'Department',
            locationOfRecords: 'Intranet',
            appliedStandards: 'EN',
          },
        },
        sourceStreamCategoryAppliedTiers: [
          {
            sourceStreamCategory: {
              sourceStream: '16584088728380.004297921475426181',
              emissionSources: ['16584089034220.18536180060525886'],
              annualEmittedCO2Tonnes: 20000,
              categoryType: 'MAJOR',
              emissionPoints: ['16584089204130.20555942543417416'],
              emissionType: 'ABATED',
              monitoringApproachType: 'CALCULATION',
            },
            measuredEmissions: {
              measurementDevicesOrMethods: ['16584090261210.41985329955533346'],
              samplingFrequency: 'CONTINUOUS',
              isHighestRequiredTier: true,
              tier: 'TIER_2',
            },
            appliedStandard: {
              parameter: 'ds',
              appliedStandard: 'QAL2',
              deviationFromAppliedStandardExist: true,
              deviationFromAppliedStandardDetails: 'dsdsds',
              laboratoryName: 'Laboratory name',
              laboratoryAccredited: true,
            },
          },
        ],
      },
      PFC: {
        type: 'PFC',
        approachDescription: 'Approach description',
        cellAndAnodeTypes: [
          {
            cellType: 'Cell type',
            anodeType: 'Anode type',
          },
        ],
        collectionEfficiency: {
          procedureDescription: 'Procedure description',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        tier2EmissionFactor: {
          exist: true,
          determinationInstallation: {
            procedureDescription: 'Procedure description',
            procedureDocumentName: 'Name of the procedure document',
            procedureReference: 'Procedure reference',
            diagramReference: 'Diagram reference (optional)',
            responsibleDepartmentOrRole: 'Department',
            locationOfRecords: 'Intranet',
            itSystemUsed: 'IT system used (optional)',
            appliedStandards: 'EN',
          },
          scheduleMeasurements: {
            procedureDescription: 'Procedure description',
            procedureDocumentName: 'Name of the procedure document',
            procedureReference: 'Procedure reference',
            diagramReference: 'Diagram reference (optional)',
            responsibleDepartmentOrRole: 'Department',
            locationOfRecords: 'Intranet',
            itSystemUsed: 'IT system used (optional)',
            appliedStandards: 'EN',
          },
        },
        sourceStreamCategoryAppliedTiers: [
          {
            sourceStreamCategory: {
              sourceStream: '16584088728380.004297921475426181',
              emissionSources: ['16584089034220.18536180060525886'],
              annualEmittedCO2Tonnes: 20000,
              categoryType: 'MARGINAL',
              emissionPoints: ['16584089204130.20555942543417416'],
              calculationMethod: 'SLOPE',
            },
            activityData: {
              massBalanceApproachUsed: true,
              tier: 'TIER_3',
              isHighestRequiredTier: true,
            },
            emissionFactor: {
              tier: 'TIER_1',
              isHighestRequiredTier: true,
            },
          },
        ],
      },
      FALLBACK: {
        type: 'FALLBACK',
        approachDescription: 'Approach description and justification',
        justification: 'Approach description and justification',
        annualUncertaintyAnalysis: {
          procedureDescription: 'Annual uncertainty analysis',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        sourceStreamCategoryAppliedTiers: [
          {
            sourceStreamCategory: {
              sourceStream: '16584088728380.004297921475426181',
              emissionSources: ['16584089034220.18536180060525886'],
              annualEmittedCO2Tonnes: 20000,
              categoryType: 'MARGINAL',
              measurementDevicesOrMethods: ['16584090261210.41985329955533346'],
              uncertainty: 'LESS_OR_EQUAL_1_5',
            },
          },
        ],
      },
      CALCULATION: {
        type: 'CALCULATION',
        approachDescription: 'Approach description',
        samplingPlan: {
          exist: true,
          details: {
            analysis: {
              procedureDescription: 'Procedure description',
              procedureDocumentName: 'procedure document',
              procedureReference: 'Procedure reference',
              diagramReference: 'Diagram reference',
              responsibleDepartmentOrRole:
                'Department or role that’s responsible for the procedure and the data generated',
              locationOfRecords: 'Intranet',
              appliedStandards: 'EN',
            },
            procedurePlan: {
              procedureDescription: 'Procedure description',
              procedureDocumentName: 'procedure document',
              procedureReference: 'Procedure reference',
              responsibleDepartmentOrRole: 'Department',
              locationOfRecords: 'Intranet',
              appliedStandards: 'EN',
            },
            appropriateness: {
              procedureDescription: 'Procedure description',
              procedureDocumentName: 'Name of the procedure document',
              procedureReference: 'Procedure reference',
              diagramReference: 'Diagram reference (optional)',
              responsibleDepartmentOrRole: 'Department',
              locationOfRecords: 'Intranet',
              itSystemUsed: 'IT system used (optional)',
              appliedStandards: 'EN',
            },
            yearEndReconciliation: {
              exist: true,
              procedureForm: {
                procedureDescription: 'Procedure description',
                procedureDocumentName: 'procedure document',
                procedureReference: 'Procedure reference',
                diagramReference: 'Diagram reference',
                responsibleDepartmentOrRole: 'Department',
                locationOfRecords: 'Intranet',
                appliedStandards: 'EN',
              },
            },
          },
        },
        sourceStreamCategoryAppliedTiers: [
          {
            sourceStreamCategory: {
              sourceStream: '16584088728380.004297921475426181',
              emissionSources: ['16584089034220.18536180060525886'],
              annualEmittedCO2Tonnes: 20000,
              categoryType: 'MINOR',
              calculationMethod: 'STANDARD',
            },
            emissionFactor: {
              exist: true,
              tier: 'TIER_2B',
              isHighestRequiredTier: true,
              defaultValueApplied: true,
              standardReferenceSource: {
                type: 'UK_NATIONAL_GREENHOUSE_GAS_INVENTORY',
              },
              analysisMethodUsed: true,
              analysisMethods: [
                {
                  analysis: 'ISO6976',
                  subParameter: 'moisture content',
                  samplingFrequency: 'CONTINUOUS',
                  frequencyMeetsMinRequirements: true,
                  laboratoryName: 'Laboratory name',
                  laboratoryAccredited: true,
                },
              ],
            },
            oxidationFactor: {
              exist: true,
              tier: 'TIER_2',
              isHighestRequiredTier: true,
              defaultValueApplied: true,
              standardReferenceSource: {
                type: 'UK_NATIONAL_GREENHOUSE_GAS_INVENTORY',
              },
              analysisMethodUsed: true,
              analysisMethods: [
                {
                  analysis: 'ISO6976',
                  subParameter: 'moisture content',
                  samplingFrequency: 'CONTINUOUS',
                  frequencyMeetsMinRequirements: true,
                  laboratoryName: 'Laboratory name',
                  laboratoryAccredited: true,
                },
              ],
            },
            activityData: {
              measurementDevicesOrMethods: ['16584090261210.41985329955533346'],
              uncertainty: 'LESS_OR_EQUAL_1_5',
              tier: 'TIER_4',
            },
            carbonContent: {
              exist: true,
              tier: 'TIER_2B',
              isHighestRequiredTier: true,
              defaultValueApplied: true,
              standardReferenceSource: {
                type: 'MONITORING_REPORTING_REGULATION_ARTICLE_25_1',
              },
              analysisMethodUsed: true,
              analysisMethods: [
                {
                  analysis: 'ISO6976',
                  subParameter: 'moisture content',
                  samplingFrequency: 'CONTINUOUS',
                  frequencyMeetsMinRequirements: false,
                  laboratoryName: 'Laboratory name',
                  laboratoryAccredited: true,
                  reducedSamplingFrequencyJustification: {
                    isCostUnreasonable: true,
                    isOneThirdRuleAndSampling: false,
                  },
                },
              ],
            },
            netCalorificValue: {
              exist: true,
              tier: 'TIER_1',
              isHighestRequiredTier: true,
              defaultValueApplied: true,
              standardReferenceSource: {
                type: 'UK_NATIONAL_GREENHOUSE_GAS_INVENTORY',
              },
              analysisMethodUsed: true,
              analysisMethods: [
                {
                  analysis: 'ISO6976',
                  subParameter: 'moisture content',
                  samplingFrequency: 'DAILY',
                  frequencyMeetsMinRequirements: true,
                  laboratoryName: 'Laboratory name',
                  laboratoryAccredited: true,
                },
              ],
            },
            conversionFactor: {
              exist: true,
              tier: 'TIER_1',
              isHighestRequiredTier: true,
              defaultValueApplied: true,
              standardReferenceSource: {
                type: 'MONITORING_REPORTING_REGULATION_ANNEX_II_SECTION_4_4',
              },
              analysisMethodUsed: true,
              analysisMethods: [
                {
                  analysis: 'ISO6976',
                  subParameter: 'moisture content',
                  samplingFrequency: 'DAILY',
                  frequencyMeetsMinRequirements: true,
                  laboratoryName: 'Laboratory name',
                  laboratoryAccredited: true,
                },
              ],
            },
            biomassFraction: {
              exist: true,
              tier: 'TIER_2',
              isHighestRequiredTier: true,
              defaultValueApplied: false,
              analysisMethodUsed: false,
            },
          },
          {
            sourceStreamCategory: {
              sourceStream: '16584088843600.33461206108958286',
              emissionSources: ['16584089084370.4185121143116062'],
              annualEmittedCO2Tonnes: 20000,
              categoryType: 'MARGINAL',
              calculationMethod: 'MASS_BALANCE',
            },
            emissionFactor: {
              exist: true,
              tier: 'TIER_2B',
              isHighestRequiredTier: true,
              defaultValueApplied: true,
              standardReferenceSource: {
                type: 'UK_NATIONAL_GREENHOUSE_GAS_INVENTORY',
              },
              analysisMethodUsed: true,
              analysisMethods: [
                {
                  analysis: 'ISO6976',
                  subParameter: 'moisture content',
                  samplingFrequency: 'CONTINUOUS',
                  frequencyMeetsMinRequirements: true,
                  laboratoryName: 'Laboratory name',
                  laboratoryAccredited: true,
                },
              ],
            },
            oxidationFactor: {
              exist: true,
              tier: 'TIER_2',
              isHighestRequiredTier: true,
              defaultValueApplied: false,
              analysisMethodUsed: false,
            },
            activityData: {
              measurementDevicesOrMethods: ['16584090261210.41985329955533346'],
              uncertainty: 'LESS_OR_EQUAL_1_5',
              tier: 'TIER_3',
              isHighestRequiredTier: true,
            },
            carbonContent: {
              exist: false,
            },
            netCalorificValue: {
              exist: true,
              tier: 'TIER_2B',
              isHighestRequiredTier: true,
              defaultValueApplied: true,
              standardReferenceSource: {
                type: 'MONITORING_AND_REPORTING_REGULATION',
              },
              analysisMethodUsed: false,
            },
            conversionFactor: {
              exist: false,
            },
            biomassFraction: {
              exist: true,
              tier: 'TIER_2',
              isHighestRequiredTier: true,
              defaultValueApplied: false,
              analysisMethodUsed: false,
            },
          },
        ],
      },
      MEASUREMENT: {
        type: 'MEASUREMENT',
        approachDescription: 'Approach description',
        emissionDetermination: {
          procedureDescription: 'Procedure description',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        referencePeriodDetermination: {
          procedureDescription: 'Procedure description',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        gasFlowCalculation: {
          exist: true,
          procedureForm: {
            procedureDescription: 'Provide gas flow details',
            procedureDocumentName: 'procedure document',
            procedureReference: 'Procedure reference',
            diagramReference: 'Diagram reference',
            responsibleDepartmentOrRole: 'Department',
            locationOfRecords: 'Intranet',
            appliedStandards: 'EN',
          },
        },
        biomassEmissions: {
          exist: true,
          procedureForm: {
            procedureDescription: 'Procedure description',
            procedureDocumentName: 'procedure document',
            procedureReference: 'Procedure reference',
            diagramReference: 'Diagram reference',
            responsibleDepartmentOrRole: 'Department',
            locationOfRecords: 'Intranet',
            appliedStandards: 'EN',
          },
        },
        corroboratingCalculations: {
          procedureDescription: 'Procedure description',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        sourceStreamCategoryAppliedTiers: [
          {
            sourceStreamCategory: {
              sourceStream: '16584088728380.004297921475426181',
              emissionSources: ['16584089034220.18536180060525886'],
              annualEmittedCO2Tonnes: 20000,
              categoryType: 'MARGINAL',
              emissionPoints: ['16584089204130.20555942543417416'],
            },
            measuredEmissions: {
              measurementDevicesOrMethods: ['16584090261210.41985329955533346'],
              samplingFrequency: 'CONTINUOUS',
              isHighestRequiredTier: true,
              tier: 'TIER_3',
            },
            appliedStandard: {
              parameter: 'ds',
              appliedStandard: 'QAL2',
              deviationFromAppliedStandardExist: true,
              deviationFromAppliedStandardDetails: 'dsdsds',
              laboratoryName: 'Laboratory name',
              laboratoryAccredited: true,
            },
          },
        ],
      },
      INHERENT_CO2: {
        type: 'INHERENT_CO2',
        approachDescription: 'Approach description',
      },
      TRANSFERRED_CO2: {
        type: 'TRANSFERRED_CO2',
        receivingTransferringInstallations: [
          {
            type: 'RECEIVING',
            installationIdentificationCode: 'Installation identification code',
            operator: 'Operator',
            installationName: 'Installation name',
            co2source: 'Source of CO2',
          },
          {
            type: 'TRANSFERRING',
            installationIdentificationCode: 'Installation identification code',
            operator: 'Operator',
            installationName: 'Installation name',
            co2source: 'Source of CO2',
          },
        ],
        accountingEmissions: {
          chemicallyBound: true,
        },
        deductionsToAmountOfTransferredCO2: {
          exist: true,
          procedureForm: {
            procedureDescription: 'Procedure description',
            procedureDocumentName: 'procedure document',
            procedureReference: 'Procedure reference',
            diagramReference: 'Diagram reference',
            responsibleDepartmentOrRole: 'Department',
            locationOfRecords: 'Intranet',
            appliedStandards: 'EN',
          },
        },
        procedureForLeakageEvents: {
          exist: true,
          procedureForm: {
            procedureDescription: 'Procedure description',
            procedureDocumentName: 'procedure document',
            procedureReference: 'Procedure reference',
            diagramReference: 'Diagram reference',
            responsibleDepartmentOrRole: 'Department',
            locationOfRecords: 'Intranet',
            appliedStandards: 'EN',
          },
        },
        temperaturePressure: {
          exist: true,
          measurementDevices: [
            {
              reference: 'M1 Meter south boiler 1',
              type: 'BELLOWS_METER',
              location: 'North terminal',
            },
          ],
        },
        transferOfCO2: {
          procedureDescription: 'Procedure description',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        quantificationMethodologies: {
          procedureDescription: 'Procedure description',
          procedureDocumentName: 'Name of the procedure document',
          procedureReference: 'Procedure reference',
          diagramReference: 'Diagram reference (optional)',
          responsibleDepartmentOrRole: 'Department',
          locationOfRecords: 'Intranet',
          itSystemUsed: 'IT system used (optional)',
          appliedStandards: 'EN',
        },
        approachDescription: 'Approach description',
      },
    },
    uncertaintyAnalysis: {
      exist: true,
      attachments: ['7c8b350c-1881-44d9-a901-bd8d91384724'],
    },
    managementProceduresExist: true,
    monitoringReporting: {
      monitoringRoles: [
        {
          jobTitle: 'dev',
          mainDuties: 'coding',
        },
      ],
    },
    assignmentOfResponsibilities: {
      procedureDescription: 'Procedure description',
      procedureDocumentName: 'Name of the procedure document',
      procedureReference: 'Procedure reference',
      diagramReference: 'Diagram reference (optional)',
      responsibleDepartmentOrRole: 'Department',
      locationOfRecords: 'Intranet',
      itSystemUsed: 'IT system used (optional)',
      appliedStandards: 'EN',
    },
    monitoringPlanAppropriateness: {
      procedureDescription: 'Procedure description',
      procedureDocumentName: 'Name of the procedure document',
      procedureReference: 'Procedure reference',
      diagramReference: 'Diagram reference (optional)',
      responsibleDepartmentOrRole: 'Department',
      locationOfRecords: 'Intranet',
      itSystemUsed: 'IT system used (optional)',
      appliedStandards: 'EN',
    },
    dataFlowActivities: {
      procedureDescription: 'Procedure description',
      procedureDocumentName: 'Name of the procedure document',
      procedureReference: 'Procedure reference',
      diagramReference: 'Diagram reference (optional)',
      responsibleDepartmentOrRole: 'Department',
      locationOfRecords: 'Intranet',
      itSystemUsed: 'IT system used (optional)',
      appliedStandards: 'EN',
      primaryDataSources: 'Primary data sources',
      processingSteps: 'Describe the processing steps for each data flow activity',
    },
    qaDataFlowActivities: {
      procedureDescription: 'Procedure description',
      procedureDocumentName: 'Name of the procedure document',
      procedureReference: 'Procedure reference',
      diagramReference: 'Diagram reference (optional)',
      responsibleDepartmentOrRole: 'Department',
      locationOfRecords: 'Intranet',
      appliedStandards: 'EN',
    },
    reviewAndValidationOfData: {
      procedureDescription: 'Procedure description',
      procedureDocumentName: 'Name of the procedure document',
      procedureReference: 'Procedure reference',
      diagramReference: 'Diagram reference (optional)',
      responsibleDepartmentOrRole: 'Department',
      locationOfRecords: 'Intranet',
      itSystemUsed: 'IT system used (optional)',
      appliedStandards: 'EN',
    },
    assessAndControlRisk: {
      procedureDescription: 'Procedure description',
      procedureDocumentName: 'Name of the procedure document',
      procedureReference: 'Procedure reference',
      diagramReference: 'Diagram reference (optional)',
      responsibleDepartmentOrRole: 'Department',
      locationOfRecords: 'Intranet',
      itSystemUsed: 'IT system used (optional)',
      appliedStandards: 'EN',
    },
    qaMeteringAndMeasuringEquipment: {
      procedureDescription: 'Procedure description',
      procedureDocumentName: 'Name of the procedure document',
      procedureReference: 'Procedure reference',
      diagramReference: 'Diagram reference (optional)',
      responsibleDepartmentOrRole: 'Department',
      locationOfRecords: 'Intranet',
      itSystemUsed: 'IT system used (optional)',
      appliedStandards: 'EN',
    },
    correctionsAndCorrectiveActions: {
      procedureDescription: 'Procedure description',
      procedureDocumentName: 'Name of the procedure document',
      procedureReference: 'Procedure reference',
      diagramReference: 'Diagram reference (optional)',
      responsibleDepartmentOrRole: 'Department or role that’s responsible for the procedure and the data generated',
      locationOfRecords: 'Intranet',
      itSystemUsed: 'IT system used (optional)',
      appliedStandards: 'EN',
    },
    controlOfOutsourcedActivities: {
      procedureDescription: 'Procedure description',
      procedureDocumentName: 'Name of the procedure document',
      procedureReference: 'Procedure reference',
      diagramReference: 'Diagram reference (optional)',
      responsibleDepartmentOrRole: 'Department',
      locationOfRecords: 'Intranet',
      itSystemUsed: 'IT system used (optional)',
      appliedStandards: 'EN',
    },
    recordKeepingAndDocumentation: {
      procedureDescription: 'Procedure description',
      procedureDocumentName: 'Name of the procedure document',
      procedureReference: 'Procedure reference',
      diagramReference: 'Diagram reference (optional)',
      responsibleDepartmentOrRole: 'Department',
      locationOfRecords: 'Intranet',
      itSystemUsed: 'IT system used (optional)',
      appliedStandards: 'EN',
    },
    environmentalManagementSystem: {
      exist: true,
      certified: true,
      certificationStandard: 'BS EN ISO14001: 2014',
    },
  },
  installationOperatorDetails: {
    installationName: 'JOHN JOHN AND BARBIE LTD',
    siteName: 'London headquarters',
    gridReference: 'NS4562',
    operator: 'JOHN JOHN AND BARBIE LTD',
    operatorDetailsAddress: {
      line1: 'Menedimou 32',
      city: 'London',
      postcode: '174341',
    },
    commencementDate: '2022-07-21',
  },
  permitVariationDetails: {},
  permitVariationDetailsCompleted: false,
  permitSectionsCompleted: {
    environmentalPermitsAndLicences: [true],
    monitoringApproachesPrepare: [true],
    estimatedAnnualEmissions: [true],
    installationDescription: [true],
    regulatedActivities: [true],
    monitoringMethodologyPlans: [true],
    sourceStreams: [true],
    measurementDevicesOrMethods: [true],
    emissionSources: [true],
    emissionPoints: [true],
    emissionSummaries: [true],
    abbreviations: [true],
    confidentialityStatement: [true],
    additionalDocuments: [true],
    N2O_Description: [true],
    N2O_Emission: [true],
    N2O_Reference: [true],
    N2O_Operational: [true],
    N2O_Emissions: [true],
    N2O_Concentration: [true],
    N2O_Product: [true],
    N2O_Materials: [true],
    N2O_Gas: [true],
    N2O_Category: [true],
    N2O_Measured_Emissions: [true],
    N2O_Applied_Standard: [true],
    PFC_Description: [true],
    PFC_Types: [true],
    PFC_Efficiency: [true],
    PFC_Tier2EmissionFactor: [true],
    PFC_Category: [true],
    PFC_Activity_Data: [true],
    PFC_Emission_Factor: [true],
    FALLBACK_Description: [true],
    FALLBACK_Uncertainty: [true],
    FALLBACK_Category: [true],
    CALCULATION_Description: [true],
    CALCULATION_Plan: [true],
    CALCULATION_Category: [true, true],
    CALCULATION_Emission_Factor: [true, true],
    CALCULATION_Oxidation_Factor: [true, true],
    CALCULATION_Activity_Data: [true, true],
    CALCULATION_Carbon_Content: [true, true],
    CALCULATION_Calorific: [true, true],
    CALCULATION_Conversion_Factor: [true, true],
    CALCULATION_Biomass_Fraction: [true, true],
    MEASUREMENT_Description: [true],
    MEASUREMENT_Emission: [true],
    MEASUREMENT_Reference: [true],
    MEASUREMENT_Gasflow: [true],
    MEASUREMENT_Biomass: [true],
    MEASUREMENT_Corroborating: [true],
    MEASUREMENT_Category: [true],
    MEASUREMENT_Measured_Emissions: [true],
    MEASUREMENT_Applied_Standard: [true],
    INHERENT_CO2_Description: [true],
    TRANSFERRED_CO2_Installations: [true, true],
    TRANSFERRED_CO2_Accounting: [true],
    TRANSFERRED_CO2_Deductions: [true],
    TRANSFERRED_CO2_Leakage: [true],
    TRANSFERRED_CO2_Temperature: [true],
    TRANSFERRED_CO2_Transfer: [true],
    TRANSFERRED_CO2_Quantification: [true],
    TRANSFERRED_CO2_Description: [true],
    monitoringApproaches: [true],
    uncertaintyAnalysis: [true],
    managementProceduresExist: [true],
    monitoringReporting: [true],
    assignmentOfResponsibilities: [true],
    monitoringPlanAppropriateness: [true],
    dataFlowActivities: [true],
    qaDataFlowActivities: [true],
    reviewAndValidationOfData: [true],
    assessAndControlRisk: [true],
    qaMeteringAndMeasuringEquipment: [true],
    correctionsAndCorrectiveActions: [true],
    controlOfOutsourcedActivities: [true],
    recordKeepingAndDocumentation: [true],
    environmentalManagementSystem: [true],
    siteDiagrams: [true],
  },
  permitAttachments: {
    '059a83bc-313a-4c80-96a0-e46db9afc7f8': 'govgr_document.pdf',
    '299d81ff-706e-4f44-9357-7ef214db203b': 'DGINTPA_ECCRM_High+Level+Architecture_V1.5.docx',
    '7c8b350c-1881-44d9-a901-bd8d91384724': 'govgr_document.pdf',
  },
  reviewSectionsCompleted: {
    N2O: true,
    PFC: true,
    FALLBACK: true,
    CALCULATION: false,
    MEASUREMENT: true,
    INHERENT_CO2: true,
    TRANSFERRED_CO2: true,
    PERMIT_TYPE: true,
    INSTALLATION_DETAILS: true,
    determination: false,
    FUELS_AND_EQUIPMENT: false,
    DEFINE_MONITORING_APPROACHES: true,
    UNCERTAINTY_ANALYSIS: true,
    MANAGEMENT_PROCEDURES: true,
    MONITORING_METHODOLOGY_PLAN: true,
    ADDITIONAL_INFORMATION: true,
    CONFIDENTIALITY_STATEMENT: true,
  },
};