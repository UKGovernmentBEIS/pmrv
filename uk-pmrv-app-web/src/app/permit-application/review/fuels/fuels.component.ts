import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { pluck } from 'rxjs';

import { GovukTableColumn } from 'govuk-components';

import { EmissionPoint, EmissionSource, EmissionSummary, MeasurementDevice, SourceStream } from 'pmrv-api';

@Component({
  selector: 'app-fuels',
  templateUrl: './fuels.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FuelsComponent {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  groupKey$ = this.route.data.pipe(pluck('groupKey'));
  sourceStreamsColumns: GovukTableColumn<SourceStream>[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-!-width-one-third' },
    { field: 'description', header: 'Description', widthClass: 'govuk-!-width-one-third' },
    { field: 'type', header: 'Type', widthClass: 'govuk-!-width-one-third' },
  ];
  pointsAndSourcesColumns: GovukTableColumn<EmissionPoint | EmissionSource>[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-!-width-one-half' },
    { field: 'description', header: 'Description', widthClass: 'govuk-!-width-one-half' },
  ];
  measurementDevicesColumns: GovukTableColumn<MeasurementDevice>[] = [
    { field: 'reference', header: 'Reference', widthClass: 'govuk-!-width-one-half' },
    { field: 'type', header: 'Type', widthClass: 'govuk-!-width-one-half' },
  ];
  emissionSummariesColumns: GovukTableColumn<EmissionSummary>[] = [
    { field: 'sourceStream', header: 'Source stream', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'emissionSources', header: 'Emission sources', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'emissionPoints', header: 'Emission points', widthClass: 'govuk-!-width-one-quarter' },
    { field: 'regulatedActivity', header: 'Regulated activity', widthClass: 'govuk-!-width-one-quarter' },
  ];

  constructor(private readonly router: Router, private readonly route: ActivatedRoute) {}
}
