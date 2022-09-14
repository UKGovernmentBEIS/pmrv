import { Inject, Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRouteSnapshot, CanActivate, Resolve, Router, RouterStateSnapshot, UrlTree } from '@angular/router';

import { map, Observable, tap } from 'rxjs';

import { LegalEntitiesService, LegalEntityInfoDTO } from 'pmrv-api';

import { LEGAL_ENTITY_FORM } from '../factories/legal-entity-form.factory';
import { ApplicationSectionType } from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Injectable()
export class LegalEntitySelectGuard implements CanActivate, Resolve<LegalEntityInfoDTO[]> {
  private legalEntities: LegalEntityInfoDTO[];

  constructor(
    @Inject(LEGAL_ENTITY_FORM) private readonly form: FormGroup,
    private readonly legalEntitiesService: LegalEntitiesService,
    private readonly router: Router,
    private readonly store: InstallationAccountApplicationStore,
  ) {}

  canActivate(route: ActivatedRouteSnapshot, state: RouterStateSnapshot): Observable<boolean | UrlTree> {
    return this.legalEntitiesService.getCurrentUserLegalEntitiesUsingGET().pipe(
      tap((legalEntities) => (this.legalEntities = legalEntities)),
      map((legalEntities) => {
        if (legalEntities.length === 0) {
          this.form.get('selectGroup').get('isNew').setValue(true);
          this.store.updateTask(ApplicationSectionType.legalEntity, { selectGroup: { isNew: true } });

          return this.router.parseUrl(
            state.url.slice(0, state.url.indexOf(route.url[route.url.length - 1].path)).concat('details'),
          );
        } else {
          return true;
        }
      }),
    );
  }

  resolve(): LegalEntityInfoDTO[] {
    return this.legalEntities;
  }
}
