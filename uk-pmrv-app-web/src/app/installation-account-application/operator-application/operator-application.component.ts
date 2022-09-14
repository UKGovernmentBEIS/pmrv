import { ChangeDetectionStrategy, Component, OnDestroy, OnInit } from '@angular/core';

import { combineLatest, filter, take, tap } from 'rxjs';

import { IdentityBarService } from '../../core/services/identity-bar.service';
import { isOnShoreInstallation } from '../pipes/submit-application';
import {
  ApplicationSectionType,
  InstallationSection,
  LegalEntitySection,
} from '../store/installation-account-application.state';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';

@Component({
  selector: 'app-operator-application',
  template: ` <router-outlet appSkipLinkFocus></router-outlet> `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OperatorApplicationComponent implements OnInit, OnDestroy {
  constructor(
    private readonly store: InstallationAccountApplicationStore,
    private readonly barService: IdentityBarService,
  ) {}

  ngOnInit(): void {
    combineLatest([
      this.store.select('isReviewed'),
      this.store.getTask<InstallationSection>(ApplicationSectionType.installation),
      this.store.getTask<LegalEntitySection>(ApplicationSectionType.legalEntity),
    ])
      .pipe(
        take(1),
        filter(([isReviewed]) => isReviewed),
        tap(([, installationSection, legalEntitySection]) => {
          const name = isOnShoreInstallation(installationSection.value)
            ? installationSection.value.onshoreGroup.name
            : installationSection.value.offshoreGroup.name;
          this.barService.show(`<span><b>${name}, ${legalEntitySection.value.detailsGroup.name}</b></span>`);
        }),
      )
      .subscribe();
  }

  ngOnDestroy(): void {
    this.barService.hide();
  }
}
