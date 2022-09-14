import { ChangeDetectionStrategy, Component } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, switchMap } from 'rxjs';

import { PermitApplicationStore } from '../../../store/permit-application.store';

@Component({
  selector: 'app-installations',
  templateUrl: './installations.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class InstallationsComponent {
  constructor(
    readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  onSubmit(): void {
    this.route.data
      .pipe(
        first(),
        switchMap((data) => this.store.postStatus(data.statusKey, true)),
      )
      .subscribe(() => this.router.navigate(['summary'], { relativeTo: this.route, state: { notification: true } }));
  }
}
