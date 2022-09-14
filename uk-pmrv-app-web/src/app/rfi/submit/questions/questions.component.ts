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
import { ActivatedRoute, Router } from '@angular/router';

import { map, Observable, startWith, takeUntil } from 'rxjs';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { WizardStepComponent } from '../../../shared/wizard/wizard-step.component';
import { RfiStore } from '../../store/rfi.store';
import { createAnotherQuestion, questionFormProvider, RFI_FORM } from './questions-form.provider';

@Component({
  selector: 'app-questions',
  templateUrl: './questions.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [DestroySubject, questionFormProvider],
})
export class QuestionsComponent implements AfterViewInit {
  @ViewChild(WizardStepComponent, { read: ElementRef, static: true }) wizardStep: ElementRef<HTMLElement>;
  @ViewChildren('removeButton') removeButtons: QueryList<ElementRef<HTMLButtonElement>>;

  isFileUploaded$: Observable<boolean> = this.form.get('files').valueChanges.pipe(
    startWith(this.form.get('files').value),
    map((value) => value?.length > 0),
  );

  private questionsLength = this.questions.length;

  tomorrow = new Date(new Date(new Date().setDate(new Date().getDate() + 1)).setHours(0, 0, 0, 0));

  constructor(
    @Inject(RFI_FORM) readonly form: FormGroup,
    readonly store: RfiStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
  ) {}

  ngAfterViewInit(): void {
    this.removeButtons.changes.pipe(takeUntil(this.destroy$)).subscribe((buttons) => {
      if (buttons.length > this.questionsLength) {
        buttons.last.nativeElement.focus();
      }
      this.questionsLength = buttons.length;
    });
  }

  onContinue(): void {
    if (this.form.dirty) {
      this.store.setState({
        ...this.store.getState(),
        rfiSubmitPayload: {
          ...this.store.getState().rfiSubmitPayload,
          questions: (this.form.get('questions').value as Array<any>).map((a) => a.question),
          files: (this.form.get('files').value as Array<any>).map((a) => a.uuid),
          deadline: this.form.get('deadline').value,
        },
        rfiAttachments: {
          ...this.store.getState().rfiAttachments,
          ...this.form.value.files?.reduce((result, item) => ({ ...result, [item.uuid]: item.file.name }), {}),
        },
      });
    }
    this.router.navigate(['../notify'], { relativeTo: this.route });
  }

  get heading(): HTMLHeadingElement {
    return this.wizardStep.nativeElement.querySelector('h1');
  }

  get questions(): FormArray {
    return this.form.get('questions') as FormArray;
  }

  addOtherQuestion(): void {
    this.questions.push(createAnotherQuestion());
    this.questions.at(this.questions.length - 1).markAllAsTouched();
  }

  getDownloadUrl() {
    return this.store.createBaseFileDownloadUrl();
  }
}
