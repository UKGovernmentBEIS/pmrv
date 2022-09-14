import { ChangeDetectionStrategy, Component, Input } from '@angular/core';

import { filter, map, Observable } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { AerService } from '@tasks/aer/core/aer.service';

import { SummaryItem } from 'govuk-components';

@Component({
  selector: 'app-confidentiality-statement-summary-template',
  template: `
    <dl
      *ngFor="let detail of details$ | async"
      govuk-summary-list
      appGroupedSummaryList
      [details]="detail"
      [hasBottomBorder]="hasBottomBorder"
    ></dl>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject],
})
export class ConfidentialityStatementSummaryTemplateComponent {
  @Input() hasBottomBorder = true;

  details$: Observable<SummaryItem[][]> = this.aerService.getTask('confidentialityStatement').pipe(
    filter((task) => !!task),
    map((task) =>
      task.exist
        ? task.confidentialSections.map((section) => [
            { key: 'Section', value: section.section },
            { key: 'Explanation', value: section.explanation },
          ])
        : [[{ key: 'Is any of the information in your application commercially confidential?', value: 'No' }]],
    ),
  );
  constructor(readonly aerService: AerService) {}
}
