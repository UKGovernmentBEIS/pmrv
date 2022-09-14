import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { filter, map, Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { SummaryItem } from 'govuk-components';

@Component({
  selector: 'app-abbreviations-summary',
  templateUrl: './summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  hasAbbreviations$: Observable<boolean> = this.aerService.getTask('abbreviations').pipe(map((item) => !!item?.exist));
  abbreviations$: Observable<SummaryItem[][]> = this.aerService.getTask('abbreviations').pipe(
    filter((item) => !!item),
    map((item) =>
      item.exist
        ? item.abbreviationDefinitions.map((abbreviationDef) => [
            { key: 'Abbreviation, acronym or terminology', value: abbreviationDef.abbreviation },
            { key: 'Definition', value: abbreviationDef.definition },
          ])
        : [
            [
              {
                key: 'Are you using any abbreviations, acronyms or terminology in your permit application which may require definition?',
                value: 'No',
              },
            ],
          ],
    ),
  );

  constructor(readonly aerService: AerService, private readonly router: Router) {}
}
