import { Inject, Injectable } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { CanActivate } from '@angular/router';

import { combineLatest, mapTo, Observable, take, tap } from 'rxjs';

import { INSTALLATION_FORM } from '../factories/installation-form.factory';
import { LEGAL_ENTITY_FORM, legalEntityInitialValue } from '../factories/legal-entity-form.factory';
import {
  ApplicationSectionType,
  InstallationSection,
  LegalEntitySection,
} from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Injectable()
export class FormGuard implements CanActivate {
  constructor(
    private readonly store: InstallationAccountApplicationStore,
    @Inject(INSTALLATION_FORM) private readonly installationForm: FormGroup,
    @Inject(LEGAL_ENTITY_FORM) private readonly legalEntityForm: FormGroup,
  ) {}

  canActivate(): Observable<boolean> {
    return combineLatest([
      this.store.getTask(ApplicationSectionType.installation),
      this.store.getTask(ApplicationSectionType.legalEntity),
      this.store.select('isReviewed'),
    ]).pipe(
      take(1),
      tap(
        ([installationSection, legalEntitySection, isReviewed]: [InstallationSection, LegalEntitySection, boolean]) => {
          this.installationForm.reset(installationSection.value);
          this.legalEntityForm.reset({ ...legalEntityInitialValue, ...legalEntitySection.value });
          if (isReviewed) {
            this.installationForm.get('installationTypeGroup').disable();
            this.installationForm.get('locationGroup').disable();
          }
        },
      ),
      mapTo(true),
    );
  }
}
