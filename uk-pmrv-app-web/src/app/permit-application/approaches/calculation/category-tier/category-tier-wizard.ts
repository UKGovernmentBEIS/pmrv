import { StatusKey } from '../../../shared/types/permit-task.type';
import { PermitApplicationState } from '../../../store/permit-application.state';
import { getSubtaskData, statusKeyToSubtaskNameMapper } from './category-tier';
import { formTierOptionsMap } from './tier/tier.map';

export function isWizardComplete(state: PermitApplicationState, index: number, statusKey: StatusKey): boolean {
  const subtaskData = getSubtaskData(state, index, statusKey);
  const subtaskName = statusKeyToSubtaskNameMapper[statusKey];

  const intermediateTierSelected =
    subtaskData?.tier && subtaskData.tier !== formTierOptionsMap[statusKey]?.options[0].value;

  const isExistFormCompleted = subtaskData?.exist !== undefined;

  const isTierFieldCompleted = !!subtaskData?.tier;
  const isHighestRequiredTierFieldRequied = intermediateTierSelected;
  const isHighestRequiredTierFieldCompleted =
    !isHighestRequiredTierFieldRequied ||
    (isHighestRequiredTierFieldRequied && subtaskData?.isHighestRequiredTier !== undefined);
  const isTierFormCompleted = isTierFieldCompleted && isHighestRequiredTierFieldCompleted;

  const isOneThirdRuleFormRequired =
    ['emissionFactor', 'carbonContent'].includes(subtaskName) && !intermediateTierSelected;
  const isOneThirdRuleFormCompleted =
    !isOneThirdRuleFormRequired || (isOneThirdRuleFormRequired && subtaskData?.oneThirdRule !== undefined);

  const isTierJustificationFormRequired = subtaskData?.isHighestRequiredTier === false;
  const isTierJustificationFormCompleted =
    !isTierJustificationFormRequired ||
    (isTierJustificationFormRequired && !!subtaskData?.noHighestRequiredTierJustification);

  const isDefaultValueFormCompleted = subtaskData?.defaultValueApplied !== undefined;

  const isReferenceFormRequired = !!subtaskData?.defaultValueApplied;
  const isReferenceFormCompleted =
    !isReferenceFormRequired || (isReferenceFormRequired && !!subtaskData?.standardReferenceSource);

  const isUseAnalysisMethodFormCompleted = subtaskData?.analysisMethodUsed !== undefined;

  const isAnalysisMethodFormRequired = !!subtaskData?.analysisMethodUsed;
  const isAnalysisMethodFormCompleted =
    !isAnalysisMethodFormRequired ||
    (isAnalysisMethodFormRequired &&
      !!subtaskData?.analysisMethods?.length &&
      subtaskData?.analysisMethods
        .filter((method) => method.frequencyMeetsMinRequirements === false)
        .every((method) => !!method.reducedSamplingFrequencyJustification));

  return (
    subtaskData &&
    subtaskData?.exist !== undefined &&
    (!subtaskData.exist ||
      (subtaskData.exist &&
        isExistFormCompleted &&
        isTierFormCompleted &&
        isOneThirdRuleFormCompleted &&
        isTierJustificationFormCompleted &&
        isDefaultValueFormCompleted &&
        isReferenceFormCompleted &&
        isUseAnalysisMethodFormCompleted &&
        isAnalysisMethodFormCompleted))
  );
}
