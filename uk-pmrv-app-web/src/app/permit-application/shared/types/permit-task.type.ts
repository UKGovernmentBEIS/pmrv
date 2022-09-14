import { Permit, PermitMonitoringApproachSection } from 'pmrv-api';

import { AmendTaskStatuses } from '../../amend/amend';
import { statuses as CalculationStatuses } from '../../approaches/calculation/calculation-status';
import { FallbackStatuses } from '../../approaches/fallback/fallback-status';
import { InherentCo2Status } from '../../approaches/inherent-co2/inherent-co2-status';
import { MeasurementStatuses } from '../../approaches/measurement/measurement-status';
import { N2OStatuses } from '../../approaches/n2o/n2o-status';
import { statuses as PFCStatuses } from '../../approaches/pfc/pfc-status';
import { TransferredCo2Status } from '../../approaches/transferred-co2/transferred-co2-status';

export type MainTaskKey = keyof Permit | 'monitoringApproachesPrepare';
export type PermitTaskType = MainTaskKey | PermitMonitoringApproachSection['type'] | 'permitType';
export type SubTaskKey = PermitMonitoringApproachSection['type'];
export type TaskKey = 'monitoringApproachesPrepare' | Path<Permit>;
export type StatusKey =
  | MainTaskKey
  | SubTaskKey
  | CalculationStatuses
  | TransferredCo2Status
  | PFCStatuses
  | N2OStatuses
  | MeasurementStatuses
  | InherentCo2Status
  | FallbackStatuses
  | AmendTaskStatuses
  | 'determination';

type PathImpl<T, K extends keyof T> = K extends string
  ? T[K] extends Record<string, any>
    ? T[K] extends ArrayLike<any>
      ? K | `${K}.${PathImpl<T[K], Exclude<keyof T[K], keyof any[]>>}`
      : K | `${K}.${PathImpl<T[K], keyof T[K]>}`
    : K
  : never;

export type Path<T> = PathImpl<T, keyof T> | keyof T;
