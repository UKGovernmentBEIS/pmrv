import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

import { EmissionSource } from 'pmrv-api';

@Component({
  selector: 'app-emission-source-delete-template',
  template: `
    <ng-container *ngIf="emissionSource">
      <app-page-heading size="xl">
        Are you sure you want to delete
        <span class="nowrap"> ‘{{ emissionSource.reference }} {{ emissionSource.description }}’? </span>
      </app-page-heading>

      <p class="govuk-body">Any reference to this item will be removed from your application.</p>

      <div class="govuk-button-group">
        <button type="button" appPendingButton (click)="onDelete()" govukWarnButton>Yes, delete</button>
        <a routerLink=".." govukLink>Cancel</a>
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
export class EmissionSourceDeleteTemplateComponent {
  @Input() emissionSource: EmissionSource;
  @Output() readonly delete: EventEmitter<void> = new EventEmitter();

  onDelete(): void {
    this.delete.emit();
  }
}
