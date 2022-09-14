import { AsyncValidatorFn, FormBuilder, ValidationErrors } from '@angular/forms';

import { first, map, Observable } from 'rxjs';

import { RegulatedActivityTypePipe } from '@shared/pipes/regulated-activity-type.pipe';
import { SourceStreamDescriptionPipe } from '@shared/pipes/source-streams-description.pipe';

import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationState } from '../store/permit-application.state';
import { PermitApplicationStore } from '../store/permit-application.store';

export const emisionSummariesFormFactory = {
  provide: PERMIT_TASK_FORM,
  deps: [FormBuilder, PermitApplicationStore],
  useFactory: (fb: FormBuilder, store: PermitApplicationStore) =>
    fb.group(
      {},
      {
        asyncValidators: [
          atLeastOneSourceStream(store),
          atLeastOneEmissionPoint(store),
          atLeastOneRegulatedActivity(store),
          atLeastOneEmissionSource(store),
          useAllEmissionPoints(store),
          useAllEmissionSources(store),
          useAllSourceStreams(store),
          useAllRegulatedActivities(store),
        ],
      },
    ),
};

function atLeastOneSourceStream(stateChanges: Observable<PermitApplicationState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) =>
        state.permit.emissionSummaries.every((summary) =>
          state.permit.sourceStreams.some((stateStream) => stateStream.id === summary.sourceStream),
        )
          ? null
          : { sourceStreamNotExist: 'Select a source stream' },
      ),
    );
}

function atLeastOneEmissionSource(stateChanges: Observable<PermitApplicationState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) =>
        state.permit.emissionSummaries.every((summary) =>
          summary.emissionSources.some((source) =>
            state.permit.emissionSources.some((stateSource) => stateSource.id === source),
          ),
        )
          ? null
          : { emissionSourceNotExist: 'Select at least one emission source' },
      ),
    );
}

function atLeastOneEmissionPoint(stateChanges: Observable<PermitApplicationState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) =>
        state.permit.emissionSummaries.every((summary) =>
          summary.emissionPoints.some((point) =>
            state.permit.emissionPoints.some((statePoint) => statePoint.id === point),
          ),
        )
          ? null
          : { emissionPointNotExist: 'Select at least one emission point' },
      ),
    );
}

function atLeastOneRegulatedActivity(stateChanges: Observable<PermitApplicationState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) =>
        state.permit.emissionSummaries.every(
          (summary) =>
            summary.excludedRegulatedActivity ||
            state.permit.regulatedActivities.some((stateActivity) => stateActivity.id === summary.regulatedActivity),
        )
          ? null
          : { regulatedActivityNotExist: 'Select a regulated activity' },
      ),
    );
}

function useAllSourceStreams(stateChanges: Observable<PermitApplicationState>): AsyncValidatorFn {
  const streamDescriptionPipe = new SourceStreamDescriptionPipe();
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) => {
        const missingSourceStreams = state.permit.sourceStreams.filter(
          (stream) => !state.permit.emissionSummaries.some((summary) => summary.sourceStream === stream.id),
        );
        const length = missingSourceStreams.length;

        const errorMessage = missingSourceStreams.reduce((result, stream, index) => {
          if (length === 1 || index === 0) {
            return `‘${stream.reference} ${streamDescriptionPipe.transform(stream)}’`;
          } else if (index === length - 1) {
            return `${result} and ‘${stream.reference} ${streamDescriptionPipe.transform(stream)}’`;
          } else {
            return `${result}, ‘${stream.reference} ${streamDescriptionPipe.transform(stream)}’`;
          }
        }, '');

        return length === 0
          ? null
          : { sourceStreamsNotUsed: `${errorMessage} must be included in your emission summaries` };
      }),
    );
}

function useAllEmissionSources(stateChanges: Observable<PermitApplicationState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) => {
        const missingEmissionSources = state.permit.emissionSources.filter(
          (source) => !state.permit.emissionSummaries.some((summary) => summary.emissionSources.includes(source.id)),
        );
        const length = missingEmissionSources.length;

        const errorMessage = missingEmissionSources.reduce((result, source, index) => {
          if (length === 1 || index === 0) {
            return `‘${source.reference} ${source.description}’`;
          } else if (index === length - 1) {
            return `${result} and ‘${source.reference} ${source.description}’`;
          } else {
            return `${result}, ‘${source.reference} ${source.description}’`;
          }
        }, '');

        return length === 0
          ? null
          : { emissionSourcesNotUsed: `${errorMessage} must be included in your emission summaries` };
      }),
    );
}

function useAllEmissionPoints(stateChanges: Observable<PermitApplicationState>): AsyncValidatorFn {
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) => {
        const missingEmissionPoints = state.permit.emissionPoints.filter(
          (point) => !state.permit.emissionSummaries.some((summary) => summary.emissionPoints.includes(point.id)),
        );
        const length = missingEmissionPoints.length;

        const errorMessage = missingEmissionPoints.reduce((result, point, index) => {
          if (length === 1 || index === 0) {
            return `‘${point.reference} ${point.description}’`;
          } else if (index === length - 1) {
            return `${result} and ‘${point.reference} ${point.description}’`;
          } else {
            return `${result}, ‘${point.reference} ${point.description}’`;
          }
        }, '');

        return length === 0
          ? null
          : { emissionPointsNotUsed: `${errorMessage} must be included in your emission summaries` };
      }),
    );
}

function useAllRegulatedActivities(stateChanges: Observable<PermitApplicationState>): AsyncValidatorFn {
  const regulatedActivityTypePipe = new RegulatedActivityTypePipe();
  return (): Observable<ValidationErrors | null> =>
    stateChanges.pipe(
      first(),
      map((state) => {
        const missingRegulatedActivities = state.permit.regulatedActivities.filter(
          (activity) => !state.permit.emissionSummaries.some((summary) => summary.regulatedActivity === activity.id),
        );
        const length = missingRegulatedActivities.length;

        const errorMessage = missingRegulatedActivities.reduce((result, activity, index) => {
          if (length === 1 || index === 0) {
            return `‘${regulatedActivityTypePipe.transform(activity.type)}’`;
          } else if (index === length - 1) {
            return `${result} and ‘${regulatedActivityTypePipe.transform(activity.type)}’`;
          } else {
            return `${result}, ‘${regulatedActivityTypePipe.transform(activity.type)}’`;
          }
        }, '');

        return length === 0
          ? null
          : { regulatedActivitiesNotUsed: `${errorMessage} must be included in your emission summaries` };
      }),
    );
}
