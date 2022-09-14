const nextStepWithTrueConditionalContent = {
  hasConditionalContent: true,
  nextStep: {
    isHighestRequiredTier: 'default-value',
    isNotHighestRequiredTier: 'tier-justification',
  },
};

export const formTierOptionsMap = {
  CALCULATION_Calorific: {
    options: [
      {
        label: 'Tier 3',
        value: 'TIER_3',
        hasConditionalContent: false,
        nextStep: 'default-value',
      },
      {
        label: 'Tier 2b',
        value: 'TIER_2B',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'Tier 2a',
        value: 'TIER_2A',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'Tier 1',
        value: 'TIER_1',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'No tier',
        value: 'NO_TIER',
        ...nextStepWithTrueConditionalContent,
      },
    ],
  },
  CALCULATION_Emission_Factor: {
    options: [
      {
        label: 'Tier 3',
        value: 'TIER_3',
        hasConditionalContent: false,
        nextStep: 'one-third',
      },
      {
        label: 'Tier 2b',
        value: 'TIER_2B',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'Tier 2a',
        value: 'TIER_2A',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'Tier 2',
        value: 'TIER_2',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'Tier 1',
        value: 'TIER_1',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'No tier',
        value: 'NO_TIER',
        ...nextStepWithTrueConditionalContent,
      },
    ],
  },
  CALCULATION_Oxidation_Factor: {
    options: [
      {
        label: 'Tier 3',
        value: 'TIER_3',
        hasConditionalContent: false,
        nextStep: 'default-value',
      },
      {
        label: 'Tier 2',
        value: 'TIER_2',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'Tier 1',
        value: 'TIER_1',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'No tier',
        value: 'NO_TIER',
        ...nextStepWithTrueConditionalContent,
      },
    ],
  },
  CALCULATION_Carbon_Content: {
    options: [
      {
        label: 'Tier 3',
        value: 'TIER_3',
        hasConditionalContent: false,
        nextStep: 'one-third',
      },
      {
        label: 'Tier 2b',
        value: 'TIER_2B',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'Tier 2a',
        value: 'TIER_2A',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'Tier 1',
        value: 'TIER_1',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'No tier',
        value: 'NO_TIER',
        ...nextStepWithTrueConditionalContent,
      },
    ],
  },
  CALCULATION_Conversion_Factor: {
    options: [
      {
        label: 'Tier 2',
        value: 'TIER_2',
        hasConditionalContent: false,
        nextStep: 'default-value',
      },
      {
        label: 'Tier 1',
        value: 'TIER_1',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'No tier',
        value: 'NO_TIER',
        ...nextStepWithTrueConditionalContent,
      },
    ],
  },
  CALCULATION_Biomass_Fraction: {
    options: [
      {
        label: 'Tier 3',
        value: 'TIER_3',
        hasConditionalContent: false,
        nextStep: 'default-value',
      },
      {
        label: 'Tier 2',
        value: 'TIER_2',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'Tier 1',
        value: 'TIER_1',
        ...nextStepWithTrueConditionalContent,
      },
      {
        label: 'No tier',
        value: 'NO_TIER',
        ...nextStepWithTrueConditionalContent,
      },
    ],
  },
};

export function getMaxTier(statusKey) {
  return formTierOptionsMap[statusKey].options.find((option) => option.hasConditionalContent === false);
}
