import { CommonTasksState } from '@tasks/store/common-tasks.state';

import { AerApplicationSubmitRequestTaskPayload, InstallationOperatorDetails } from 'pmrv-api';

export const mockOnshore = {
  companyReferenceNumber: '88888',
  installationLocation: {
    type: 'ONSHORE',
    address: {
      line1: 'Korinthou 4, Neo Psychiko',
      line2: 'line 2 legal test',
      city: 'Athens',
      country: 'GR',
      postcode: '15452',
    },
    gridReference: 'NN166712',
  },
  installationName: 'onshore permit installation',
  operator: 'onshore permit',
  operatorDetailsAddress: {
    line1: 'Korinthou 3, Neo Psychiko',
    line2: 'line 2 legal test',
    city: 'Athens',
    country: 'GR',
    postcode: '15451',
  },
  operatorType: 'LIMITED_COMPANY',
  siteName: 'site name',
} as InstallationOperatorDetails;

export const mockAerApplyPayload: AerApplicationSubmitRequestTaskPayload = {
  aerSectionsCompleted: {
    abbreviations: [true],
    additionalDocuments: [true],
    confidentialityStatement: [true],
    pollutantRegisterActivities: [true],
    sourceStreams: [false],
    monitoringApproachTypes: [true],
    emissionSources: [false],
    naceCodes: [true],
    emissionPoints: [false],
  },
  aerAttachments: {
    '2c30c8bf-3d5e-474d-98a0-123a87eb60dd': 'cover.jpg',
    '60fe9548-ac65-492a-b057-60033b0fbbea': 'PublicationAgreement.pdf',
  },
  payloadType: 'AER_APPLICATION_SUBMIT_PAYLOAD',
  installationOperatorDetails: mockOnshore,
  aer: {
    naceCodes: {
      codes: ['_1012_PROCESSING_AND_PRESERVING_OF_POULTRY_MEAT', '_1012_PROCESSING_AND_PRESERVING_OF_POULTRY_MEAT'],
    },
    abbreviations: {
      exist: true,
      abbreviationDefinitions: [
        {
          abbreviation: 'Mr',
          definition: 'Mister',
        },
        {
          abbreviation: 'Ms',
          definition: 'Miss',
        },
      ],
    },
    additionalDocuments: {
      exist: true,
      documents: ['2c30c8bf-3d5e-474d-98a0-123a87eb60dd', '60fe9548-ac65-492a-b057-60033b0fbbea'],
    },
    confidentialityStatement: {
      exist: true,
      confidentialSections: [
        {
          section: 'Section 1',
          explanation: 'Explanation 1',
        },
        {
          section: 'Section 2',
          explanation: 'Explanation 2',
        },
      ],
    },
    sourceStreams: [
      {
        id: '324',
        description: 'ANTHRACITE',
        type: 'AMMONIA_FUEL_AS_PROCESS_INPUT',
        reference: 'the reference',
      },
      {
        id: '325',
        description: 'BIODIESELS',
        type: 'CEMENT_CLINKER_CKD',
        reference: 'the other reference',
      },
    ],
    emissionSources: [
      {
        id: '853',
        description: 'emission source 1 description',
        reference: 'emission source 1 reference',
      },
      {
        id: '854',
        description: 'emission source 2 description',
        reference: 'emission source 2 reference',
      },
    ],
    pollutantRegisterActivities: {
      exist: false,
    },
    monitoringApproachTypes: ['CALCULATION', 'MEASUREMENT'],
    emissionPoints: [
      {
        id: '900',
        reference: 'EP1',
        description: 'west side chimney',
      },
      {
        id: '901',
        reference: 'EP2',
        description: 'east side chimney',
      },
    ],
    regulatedActivities: [
      {
        id: '324',
        type: 'AMMONIA_PRODUCTION',
        capacity: 100,
        capacityUnit: 'KVA',
        hasEnergyCrf: true,
        hasIndustrialCrf: true,
        energyCrf: '_1_A_1_A_PUBLIC_ELECTRICITY_AND_HEAT_PRODUCTION',
        industrialCrf: '_2_A_4_OTHER_PROCESS_USES_OF_CARBONATES',
      },
    ],
  },
};

export const mockState = {
  requestTaskItem: {
    allowedRequestTaskActions: ['AER_SAVE_APPLICATION', 'AER_UPLOAD_SECTION_ATTACHMENT'],
    requestInfo: {
      id: 'AEM210-2021',
      type: 'AER',
      competentAuthority: 'WALES',
      accountId: 210,
      requestMetadata: {
        type: 'AER',
        year: '2021',
      },
    },
    requestTask: {
      id: 1,
      type: 'AER_APPLICATION_SUBMIT',
      payload: mockAerApplyPayload,
    },
    userAssignCapable: true,
  },
  relatedTasks: [],
  timeLineActions: [],
  storeInitialized: true,
  isEditable: true,
  user: undefined,
} as CommonTasksState;
