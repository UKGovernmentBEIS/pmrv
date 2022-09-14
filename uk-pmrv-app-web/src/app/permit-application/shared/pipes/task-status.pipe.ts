import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { TaskItemStatus } from '@shared/task-list/task-list.interface';

import { monitoringApproachesStatus } from '../../approaches/approach-status';
import {
  planStatus as CalculationPlanStatus,
  status as CalculationStatus,
} from '../../approaches/calculation/calculation-status';
import { categoryTierStatus as CalculationCategoryTierStatus } from '../../approaches/calculation/calculation-status';
import { categoryTierSubtaskStatus as CalculationCategoryTierSubtaskStatus } from '../../approaches/calculation/calculation-status';
import {
  FALLBACKCategoryTierStatus,
  FALLBACKCategoryTierSubtaskStatus,
  FALLBACKStatus,
} from '../../approaches/fallback/fallback-status';
import { INHERENT_CO2Status } from '../../approaches/inherent-co2/inherent-co2-status';
import {
  MEASUREMENTCategoryTierStatus,
  MEASUREMENTCategoryTierSubtaskStatus,
  MEASUREMENTStatus,
} from '../../approaches/measurement/measurement-status';
import { N2OCategoryTierStatus, N2OCategoryTierSubtaskStatus, N2OStatus } from '../../approaches/n2o/n2o-status';
import {
  categoryTierStatus as PFCCategoryTierStatus,
  categoryTierSubtaskStatus as PFCCategoryTierSubtaskStatus,
  PFCTier2EmissionFactorSubtaskStatus,
  status as PFCStatus,
} from '../../approaches/pfc/pfc-status';
import {
  TRANSFERRED_CO2_AccountingStatus,
  TRANSFERRED_CO2_InstallationsStatus,
  TRANSFERRED_CO2Status,
} from '../../approaches/transferred-co2/transferred-co2-status';
import { uncertaintyAnalysisStatus } from '../../approaches/uncertainty-analysis/uncertainty-analysis-status';
import { emissionSummariesStatus } from '../../emission-summaries/emission-summaries-status';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { StatusKey } from '../types/permit-task.type';

@Pipe({ name: 'taskStatus' })
export class TaskStatusPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore) {}

  transform(key: StatusKey, index?: number): Observable<TaskItemStatus> {
    return this.store.pipe(
      map((state) => {
        if (!state.isRequestTask) {
          return 'complete';
        } else {
          switch (key) {
            case 'permitType':
              return state.permitType ? 'complete' : 'not started';
            case 'sourceStreams':
            case 'emissionPoints':
            case 'emissionSources':
            case 'measurementDevicesOrMethods':
              return state.permitSectionsCompleted[key]?.[0]
                ? 'complete'
                : state.permit[key].length > 0
                ? 'in progress'
                : 'not started';
            case 'emissionSummaries':
              return emissionSummariesStatus(state);
            case 'monitoringApproaches':
              return monitoringApproachesStatus(state);
            case 'CALCULATION':
              return CalculationStatus(state);
            case 'CALCULATION_Plan':
              return CalculationPlanStatus(state);
            case 'CALCULATION_Category_Tier':
              return CalculationCategoryTierStatus(state, index);
            case 'CALCULATION_Category':
            case 'CALCULATION_Emission_Factor':
            case 'CALCULATION_Calorific':
            case 'CALCULATION_Biomass_Fraction':
            case 'CALCULATION_Carbon_Content':
            case 'CALCULATION_Oxidation_Factor':
            case 'CALCULATION_Conversion_Factor':
            case 'CALCULATION_Activity_Data':
              return CalculationCategoryTierSubtaskStatus(state, key, index);
            case 'FALLBACK':
              return FALLBACKStatus(state);
            case 'FALLBACK_Category_Tier':
              return FALLBACKCategoryTierStatus(state, index);
            case 'FALLBACK_Category':
              return FALLBACKCategoryTierSubtaskStatus(state, key, index);
            case 'INHERENT_CO2':
              return INHERENT_CO2Status(state);
            case 'MEASUREMENT':
              return MEASUREMENTStatus(state);
            case 'MEASUREMENT_Category_Tier':
              return MEASUREMENTCategoryTierStatus(state, index);
            case 'MEASUREMENT_Category':
            case 'MEASUREMENT_Measured_Emissions':
            case 'MEASUREMENT_Applied_Standard':
              return MEASUREMENTCategoryTierSubtaskStatus(state, key, index);
            case 'N2O':
              return N2OStatus(state);
            case 'N2O_Category_Tier':
              return N2OCategoryTierStatus(state, index);
            case 'N2O_Category':
            case 'N2O_Measured_Emissions':
            case 'N2O_Applied_Standard':
              return N2OCategoryTierSubtaskStatus(state, key, index);
            case 'PFC':
              return PFCStatus(state);
            case 'PFC_Category_Tier':
              return PFCCategoryTierStatus(state, index);
            case 'PFC_Category':
            case 'PFC_Activity_Data':
            case 'PFC_Emission_Factor':
              return PFCCategoryTierSubtaskStatus(state, key, index);
            case 'PFC_Tier2EmissionFactor':
              return PFCTier2EmissionFactorSubtaskStatus(state);
            case 'TRANSFERRED_CO2':
              return TRANSFERRED_CO2Status(state);
            case 'TRANSFERRED_CO2_Installations':
              return TRANSFERRED_CO2_InstallationsStatus(state);
            case 'TRANSFERRED_CO2_Accounting':
              return TRANSFERRED_CO2_AccountingStatus(state);
            case 'uncertaintyAnalysis':
              return uncertaintyAnalysisStatus(state);
            default:
              return state.permitSectionsCompleted[key]?.[0] ? 'complete' : 'not started';
          }
        }
      }),
    );
  }
}
