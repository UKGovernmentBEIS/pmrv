import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

import { PermitMonitoringApproachSection } from 'pmrv-api';

@Component({
  selector: 'app-approaches-delete-template',
  template: `
    <ng-container *ngIf="monitoringApproach">
      <app-page-heading size="xl">
        <span>Are you sure you want to delete </span>
        <span class="nowrap">'{{ monitoringApproach | monitoringApproachDescription }}'?</span>
      </app-page-heading>

      <p class="govuk-body">
        All information related to the {{ monitoringApproach | monitoringApproachDescription }} approach will be
        deleted.
      </p>

      <div class="govuk-button-group">
        <button (click)="onDelete()" appPendingButton govukWarnButton type="button">Yes, delete</button>
        <a govukLink routerLink="../..">Cancel</a>
      </div>
    </ng-container>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .nowrap {
        white-space: nowrap;
      }
    `,
  ],
})
export class ApproachesDeleteTemplateComponent {
  @Input() monitoringApproach: PermitMonitoringApproachSection['type'];
  @Output() readonly delete: EventEmitter<void> = new EventEmitter();

  onDelete(): void {
    this.delete.emit();
  }
}
