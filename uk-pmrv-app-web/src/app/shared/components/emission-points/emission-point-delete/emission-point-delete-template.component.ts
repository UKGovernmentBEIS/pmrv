import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

import { EmissionPoint } from 'pmrv-api';

@Component({
  selector: 'app-emission-point-delete-template',
  template: `
    <ng-container *ngIf="emissionPoint">
      <app-page-heading size="xl">
        Are you sure you want to delete
        <span class="nowrap"> ‘{{ emissionPoint.reference }} {{ emissionPoint.description }}’? </span>
      </app-page-heading>

      <p class="govuk-body">Any reference to this item will be removed from your application.</p>

      <div class="govuk-button-group">
        <button (click)="onDelete()" appPendingButton govukWarnButton>Yes, delete</button>
        <a govukLink routerLink="..">Cancel</a>
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
export class EmissionPointDeleteTemplateComponent {
  @Input() emissionPoint: EmissionPoint;
  @Output() readonly delete: EventEmitter<void> = new EventEmitter();

  onDelete(): void {
    this.delete.emit();
  }
}
