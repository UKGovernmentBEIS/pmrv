import { TaskItemStatus } from '../../shared/task-list/task-list.interface';
import { PermitApplicationState } from '../store/permit-application.state';

export function emissionSummariesStatus(state: PermitApplicationState): TaskItemStatus {
  return state.permit.emissionSummaries.length === 0
    ? !areEmissionSummariesPrerequisitesMet(state)
      ? 'cannot start yet'
      : 'not started'
    : !areEmissionSummariesValid(state) ||
      (!areEmissionSummariesUsingEverything(state) && state.permitSectionsCompleted.emissionSummaries?.[0])
    ? 'needs review'
    : !state.permitSectionsCompleted.emissionSummaries?.[0]
    ? 'in progress'
    : 'complete';
}

export function areEmissionSummariesPrerequisitesMet(state: PermitApplicationState): boolean {
  return (
    state.permitSectionsCompleted.regulatedActivities?.[0] &&
    state.permitSectionsCompleted.sourceStreams?.[0] &&
    state.permitSectionsCompleted.emissionPoints?.[0] &&
    state.permitSectionsCompleted.emissionSources?.[0]
  );
}

export function areEmissionSummariesValid(state: PermitApplicationState): boolean {
  return (
    state.permit.emissionSummaries.length === 0 ||
    state.permit.emissionSummaries.every(
      (summary) =>
        state.permit.sourceStreams.map((stateStream) => stateStream.id).includes(summary.sourceStream) &&
        summary.emissionPoints.every((pointId) =>
          state.permit.emissionPoints.map((statePoint) => statePoint.id).includes(pointId),
        ) &&
        summary.emissionSources.every((sourceId) =>
          state.permit.emissionSources.map((stateSource) => stateSource.id).includes(sourceId),
        ) &&
        (summary.excludedRegulatedActivity ||
          state.permit.regulatedActivities
            .map((stateActivity) => stateActivity.id)
            .includes(summary.regulatedActivity)),
    )
  );
}

export function areEmissionSummariesUsingEverything(state: PermitApplicationState): boolean {
  return (
    state.permit.sourceStreams
      .map((stream) => stream.id)
      .every((streamId) => state.permit.emissionSummaries.map((summary) => summary.sourceStream).includes(streamId)) &&
    state.permit.emissionPoints
      .map((point) => point.id)
      .every((pointId) =>
        state.permit.emissionSummaries
          .map((summary) => summary.emissionPoints)
          .some((points) => points.includes(pointId)),
      ) &&
    state.permit.emissionSources
      .map((source) => source.id)
      .every((sourceId) =>
        state.permit.emissionSummaries
          .map((summary) => summary.emissionSources)
          .some((sources) => sources.includes(sourceId)),
      ) &&
    state.permit.regulatedActivities
      .map((activity) => activity.id)
      .every((activityId) =>
        state.permit.emissionSummaries.map((summary) => summary.regulatedActivity).includes(activityId),
      )
  );
}
