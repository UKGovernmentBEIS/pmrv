import { Injectable } from '@angular/core';
import { ActivatedRouteSnapshot, CanActivate, Router, UrlTree } from '@angular/router';

import { first, map, Observable } from 'rxjs';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Injectable({
  providedIn: 'root',
})
export class MeasurementDeviceDetailsGuard implements CanActivate {
  constructor(private readonly store: PermitApplicationStore, private readonly router: Router) {}

  canActivate(route: ActivatedRouteSnapshot): Observable<boolean | UrlTree> {
    return this.store.getTask('measurementDevicesOrMethods').pipe(
      first(),
      map(
        (measurementDevicesOrMethods) =>
          measurementDevicesOrMethods.some(
            (measurementDevice) => measurementDevice.id === route.paramMap.get('deviceId'),
          ) || this.router.parseUrl(`/permit-application/${route.paramMap.get('taskId')}/measurement-devices/summary`),
      ),
    );
  }
}
