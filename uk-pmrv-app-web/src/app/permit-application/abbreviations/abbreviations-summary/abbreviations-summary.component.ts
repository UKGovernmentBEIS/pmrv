import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Router } from '@angular/router';

import { filter, map, Observable } from 'rxjs';

import { SummaryItem } from 'govuk-components';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-abbreviations-summary',
  templateUrl: './abbreviations-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AbbreviationsSummaryComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  hasAbbreviations$: Observable<boolean> = this.store.getTask('abbreviations').pipe(map((item) => !!item?.exist));
  abbreviations$: Observable<SummaryItem[][]> = this.store.getTask('abbreviations').pipe(
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

  constructor(readonly store: PermitApplicationStore, private readonly router: Router) {}
}
