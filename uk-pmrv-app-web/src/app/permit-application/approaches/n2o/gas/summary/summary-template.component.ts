import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

@Component({
  selector: 'app-n2o-approach-gas-summary-template',
  template: `
    <dl
      *ngIf="('N2O' | monitoringApproachTask | async)?.gasFlowCalculation?.exist === false; else procedureForm"
      govuk-summary-list
      [hasBorders]="false"
      [class.summary-list--edge-border]="hasBottomBorder"
    >
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Is gas flow determined by calculation?</dt>
        <dd govukSummaryListRowValue>No</dd>
      </div>
    </dl>

    <ng-template #procedureForm>
      <app-procedure-form-summary
        [details]="('N2O' | monitoringApproachTask | async)?.gasFlowCalculation?.procedureForm"
        cssClass="summary-list--edge-border"
        [hasBottomBorder]="hasBottomBorder"
      ></app-procedure-form-summary>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryTemplateComponent {
  @Input() hasBottomBorder = true;
}
