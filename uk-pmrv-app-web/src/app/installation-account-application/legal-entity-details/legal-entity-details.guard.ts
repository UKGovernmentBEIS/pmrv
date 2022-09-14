import { Inject, Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, CanActivate, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { LEGAL_ENTITY_FORM } from '../factories/legal-entity-form.factory';

@Injectable()
export class LegalEntityDetailsGuard implements CanActivate {
  constructor(private readonly router: Router, @Inject(LEGAL_ENTITY_FORM) private readonly form: FormGroup) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): boolean | UrlTree {
    return (
      this.form.get('selectGroup').get('isNew').value ||
      this.router.parseUrl(state.url.slice(0, state.url.indexOf(route.url[route.url.length - 1].path)) + 'select')
    );
  }
}
