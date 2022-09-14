import { ChangeDetectionStrategy, Component, Inject } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, tap } from 'rxjs';

import { RdeStore } from '../../store/rde.store';
import { extendDeterminationFormProvider, RDE_FORM } from './extend-determination-form.provider';

@Component({
  selector: 'app-extend-determination',
  templateUrl: './extend-determination.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [extendDeterminationFormProvider],
})
export class ExtendDeterminationComponent {
  constructor(
    @Inject(RDE_FORM) readonly form: FormGroup,
    readonly store: RdeStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  readonly taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));

  onContinue(): void {
    if (!this.form.dirty) {
      this.router.navigate(['../notify-users'], { relativeTo: this.route });
    } else {
      this.store
        .pipe(
          first(),
          tap((state) =>
            this.store.setState({
              ...state,
              rdePayload: {
                ...this.store.getState().rdePayload,
                extensionDate: this.form.get('extensionDate').value,
                deadline: this.form.get('deadline').value,
              },
            }),
          ),
        )
        .subscribe(() => {
          this.router.navigate(['../notify-users'], { relativeTo: this.route });
        });
    }
  }
}
