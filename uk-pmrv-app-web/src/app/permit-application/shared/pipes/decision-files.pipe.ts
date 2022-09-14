import { Pipe, PipeTransform } from '@angular/core';

import { PermitApplicationState } from '../../store/permit-application.state';

@Pipe({ name: 'decisionFiles' })
export class DecisionFilesPipe implements PipeTransform {
  transform(group: string, state: PermitApplicationState): { downloadUrl: string; fileName: string }[] {
    const url = state.isRequestTask
      ? `/permit-application/${state.requestTaskId}/file-download/`
      : `/permit-application/action/${state.actionId}/file-download/`;
    return state.reviewGroupDecisions[group].files.map((id) => ({
      downloadUrl: url + `${id}`,
      fileName: state.reviewAttachments[id],
    }));
  }
}
