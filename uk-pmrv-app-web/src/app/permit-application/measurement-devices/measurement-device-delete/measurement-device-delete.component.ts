import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, switchMap } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Component({
  selector: 'app-measurement-device-delete',
  templateUrl: './measurement-device-delete.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  styles: [
    `
      .nowrap {
        white-space: nowrap;
      }
    `,
  ],
})
export class MeasurementDeviceDeleteComponent {
  measurementDevice$ = combineLatest([this.store.getTask('measurementDevicesOrMethods'), this.route.paramMap]).pipe(
    map(([measurementDevices, paramMap]) =>
      measurementDevices.find((measurementDevice) => measurementDevice.id === paramMap.get('deviceId')),
    ),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly store: PermitApplicationStore,
  ) {}

  delete(): void {
    this.store
      .getTask('measurementDevicesOrMethods')
      .pipe(
        first(),
        switchMap((measurementDevicesOrMethods) =>
          this.store.postTask(
            'measurementDevicesOrMethods',
            measurementDevicesOrMethods.filter((device) => device.id !== this.route.snapshot.paramMap.get('deviceId')),
            false,
          ),
        ),
      )
      .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route }));
  }
}
