import { ChangeDetectionStrategy, Component, EventEmitter, Input, Output } from '@angular/core';

import { SourceStream } from 'pmrv-api';

@Component({
  selector: 'app-source-stream-delete-template',
  template: `
    <ng-container *ngIf="sourceStream">
      <app-page-heading size="xl">
        Are you sure you want to delete
        <span class="nowrap"> ‘{{ sourceStream.reference }} {{ sourceStream | sourceStreamDescription }}’? </span>
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
export class SourceStreamDeleteTemplateComponent {
  @Input() sourceStream: SourceStream;
  @Output() readonly delete: EventEmitter<void> = new EventEmitter();

  onDelete(): void {
    this.delete.emit();
  }
}
