import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { AerRequestMetadata } from 'pmrv-api';

@Component({
  selector: 'app-submit',
  templateUrl: './submit-container.component.html',
  styles: [],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitContainerComponent {
  aerTitle$ = this.aerService.requestMetadata$.pipe(
    map((metadata) => (metadata as AerRequestMetadata).year + ' emissions report'),
  );
  isMeasurementOrN2OApproachesSelected$ = this.aerService
    .getTask('monitoringApproachTypes')
    .pipe(
      map(
        (monitoringApproachTypes) =>
          monitoringApproachTypes.includes('MEASUREMENT') || monitoringApproachTypes.includes('N2O'),
      ),
    );
  monitoringApproaches = [
    { link: './calculation', linkText: 'Calculation emissions', status: 'not started' },
    { link: './co2', linkText: 'Measurement of CO2 emissions', status: 'not started' },
  ];
  constructor(
    private readonly aerService: AerService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  onCancelTask() {
    this.router.navigate(['cancel'], { relativeTo: this.route.parent.parent.parent });
  }
}
