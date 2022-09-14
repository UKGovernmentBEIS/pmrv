import { Inject, Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { INSTALLATION_FORM } from '../factories/installation-form.factory';

@Injectable()
export class GasEmissionsDetailsGuard implements CanActivate {
  constructor(private readonly router: Router, @Inject(INSTALLATION_FORM) private readonly form: FormGroup) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
    return (
      (this.form.get('installationTypeGroup').get('type').value === 'ONSHORE' && this.form.get('onshoreGroup').valid) ||
      this.router.parseUrl(state.url.slice(0, state.url.indexOf(route.url[route.url.length - 1].path)).concat('type'))
    );
  }
}
