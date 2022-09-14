import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Inject,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { filter, first, switchMap } from 'rxjs';

import { PendingRequestService } from '../../../../core/guards/pending-request.service';
import { BackLinkService } from '../../../../shared/back-link/back-link.service';
import { WizardStepComponent } from '../../../../shared/wizard/wizard-step.component';
import { PERMIT_TASK_FORM } from '../../../shared/permit-task-form.token';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { createAnotherEmissionsTarget, emissionsFormProvider } from './emissions-form.provider';

@Component({
  selector: 'app-emissions',
  templateUrl: './emissions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [emissionsFormProvider, BackLinkService],
})
export class EmissionsComponent {
  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;
  @ViewChildren('removeButton') removeButtons: QueryList<ElementRef<HTMLButtonElement>>;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    readonly pendingRequest: PendingRequestService,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  get heading(): HTMLHeadingElement {
    return this.wizardStep.nativeElement.querySelector('h1');
  }

  get annualEmissionsTargets(): FormArray {
    return this.form.get('annualEmissionsTargets') as FormArray;
  }

  onContinue(): void {
    const annualEmissionsTargets = {};
    this.form.value.annualEmissionsTargets.forEach(
      (target) => (annualEmissionsTargets[target.year] = Number(target.emissions)),
    );

    this.store
      .pipe(
        first(),
        switchMap((state) =>
          this.store.postDetermination(
            {
              ...state.determination,
              annualEmissionsTargets: annualEmissionsTargets,
            },
            false,
          ),
        ),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => this.router.navigate(['../answers'], { relativeTo: this.route }));
  }

  addOtherAnnualEmissionsTarget(): void {
    this.annualEmissionsTargets.push(createAnotherEmissionsTarget());
    this.wizardStepComponent.isSummaryDisplayedSubject
      .pipe(
        first(),
        filter((value) => value),
      )
      .subscribe(() => this.annualEmissionsTargets.at(this.annualEmissionsTargets.length - 1).markAllAsTouched());
  }
}
