import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Inject,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { FormArray, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { filter, first, map, Observable, startWith, takeUntil } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { DestroySubject } from '../../core/services/destroy-subject.service';
import { WizardStepComponent } from '../../shared/wizard/wizard-step.component';
import { PERMIT_TASK_FORM } from '../shared/permit-task-form.token';
import { PermitApplicationStore } from '../store/permit-application.store';
import { createAnotherRole, monitoringRolesFormProvider } from './monitoring-roles-form.provider';

@Component({
  selector: 'app-monitoring-roles',
  templateUrl: './monitoring-roles.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, monitoringRolesFormProvider],
})
export class MonitoringRolesComponent implements AfterViewInit {
  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;
  @ViewChild(WizardStepComponent) wizardStepComponent: WizardStepComponent;
  @ViewChildren('removeButton') removeButtons: QueryList<ElementRef<HTMLButtonElement>>;

  isFileUploaded$: Observable<boolean> = this.form.get('organisationCharts').valueChanges.pipe(
    startWith(this.form.get('organisationCharts').value),
    map((value) => value?.length > 0),
  );

  private rolesLength = this.roles.length;

  constructor(
    @Inject(PERMIT_TASK_FORM) readonly form: FormGroup,
    readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly pendingRequest: PendingRequestService,
    private readonly destroy$: DestroySubject,
  ) {}

  get heading(): HTMLHeadingElement {
    return this.wizardStep.nativeElement.querySelector('h1');
  }

  get roles(): FormArray {
    return this.form.get('monitoringRoles') as FormArray;
  }

  ngAfterViewInit(): void {
    this.removeButtons.changes.pipe(takeUntil(this.destroy$)).subscribe((buttons) => {
      if (buttons.length > this.rolesLength) {
        buttons.last.nativeElement.focus();
      }
      this.rolesLength = buttons.length;
    });
  }

  onSubmit(): void {
    this.store
      .postTask(
        'monitoringReporting',
        {
          ...this.form.value,
          organisationCharts: this.form.value.organisationCharts?.map((file) => file.uuid),
        },
        true,
      )
      .pipe(this.pendingRequest.trackRequest())
      .subscribe(() => {
        this.store.setState({
          ...this.store.getState(),
          permitAttachments: {
            ...this.store.getState().permitAttachments,
            ...this.form.value.organisationCharts?.reduce(
              (result, item) => ({ ...result, [item.uuid]: item.file.name }),
              {},
            ),
          },
        });
        this.store.navigate(this.route, 'summary', 'management-procedures');
      });
  }

  addOtherRole(): void {
    this.roles.push(createAnotherRole());
    this.wizardStepComponent.isSummaryDisplayedSubject
      .pipe(
        first(),
        filter((value) => value),
      )
      .subscribe(() => this.roles.at(this.roles.length - 1).markAllAsTouched());
  }

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }
}
