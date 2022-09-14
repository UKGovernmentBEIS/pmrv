import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';

@Component({
  selector: 'app-documents-summary-template',
  template: `
    <dl *ngIf="!hasAttachments; else attachments" govuk-summary-list [hasBorders]="hasBottomBorder">
      <div govukSummaryListRow>
        <dt govukSummaryListRowKey>Additional documents or information</dt>
        <dd govukSummaryListRowValue>No</dd>
      </div>
    </dl>
    <ng-template #attachments>
      <dl govuk-summary-list [class.dl--no-bottom-border]="!hasBottomBorder" [class]="cssClass">
        <div govukSummaryListRow>
          <dd govukSummaryListRowValue>
            <app-summary-download-files [files]="files"> </app-summary-download-files>
          </dd>
        </div>
      </dl>
    </ng-template>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AdditionalDocumentsSummaryTemplateComponent implements OnInit {
  @Input() hasBottomBorder = true;
  @Input() cssClass: string;
  @Input() files: { downloadUrl: string; fileName: string }[];

  hasAttachments: boolean;

  ngOnInit(): void {
    this.hasAttachments = (this.files || []).length > 0;
  }
}
