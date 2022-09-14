import { DOCUMENT, KeyValue } from '@angular/common';
import {
  AfterViewInit,
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Inject,
  Input,
  OnChanges,
  QueryList,
  ViewChild,
  ViewChildren,
} from '@angular/core';
import { AbstractControl, FormArray, FormGroup, NgForm } from '@angular/forms';
import { Title } from '@angular/platform-browser';

import { map, Observable, startWith, tap } from 'rxjs';

import { FormService } from '../form/form.service';
import { NestedMessageValidationErrors } from './nested-message-validation-errors';

@Component({
  selector: 'govuk-error-summary',
  templateUrl: './error-summary.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ErrorSummaryComponent implements OnChanges, AfterViewInit {
  @Input() form: FormGroup | NgForm;

  @ViewChildren('anchor', { read: ElementRef }) inputs: QueryList<ElementRef<HTMLAnchorElement>>;
  @ViewChild('container', { read: ElementRef }) container: ElementRef<HTMLDivElement>;

  errorList$: Observable<NestedMessageValidationErrors>;

  private formControl: FormGroup;

  constructor(
    @Inject(DOCUMENT) private readonly document,
    private readonly formService: FormService,
    private readonly title: Title,
  ) {}

  ngOnChanges(): void {
    this.formControl = this.form instanceof FormGroup ? this.form : this.form.control;

    this.errorList$ = this.form.statusChanges.pipe(
      startWith(this.form.status),
      map((status) => status === 'INVALID' && this.getAbstractControlErrors(this.formControl)),
      tap((errors) => {
        const currentTitle = this.title.getTitle();
        const prefix = 'Error: ';

        if (errors && !currentTitle.startsWith(prefix)) {
          this.title.setTitle(prefix.concat(currentTitle));
        } else if (!errors) {
          this.title.setTitle(currentTitle.replace(prefix, ''));
        }
      }),
    );
  }

  ngAfterViewInit(): void {
    if (this.container?.nativeElement?.scrollIntoView) {
      this.container.nativeElement.scrollIntoView();
    }
    if (this.container?.nativeElement?.focus) {
      this.container.nativeElement.focus();
    }
  }

  errorClick(path: string): void {
    if (!path) {
      return;
    }
    const labelOrLegend = this.document.getElementById(`l.${path}`);
    if (labelOrLegend) {
      labelOrLegend.scrollIntoView();
    }

    // Case radio - first option
    let targetInput: HTMLElement = this.document.getElementById(`${path}-option0`);
    if (!targetInput) {
      // Case checkbox - first checkbox
      targetInput = this.document.getElementById(`${path}-0`);
    }
    if (!targetInput) {
      // Case date - first input with error
      targetInput = this.document.getElementById(`${path}-day`);
      if (targetInput && !targetInput.classList.contains('govuk-input--error')) {
        targetInput = this.document.getElementById(`${path}-month`);
        if (targetInput && !targetInput.classList.contains('govuk-input--error')) {
          targetInput = this.document.getElementById(`${path}-year`);
          if (targetInput && !targetInput.classList.contains('govuk-input--error')) {
            targetInput = this.document.getElementById(`${path}-day`);
          }
        }
      }
    }
    // Case text, textarea, select
    if (!targetInput) {
      targetInput = this.document.getElementById(path);
    }
    if (targetInput) {
      targetInput.focus({ preventScroll: true });
    }
  }

  sortByPosition = (
    a: KeyValue<string, NestedMessageValidationErrors>,
    b: KeyValue<string, NestedMessageValidationErrors>,
  ) => {
    const combinedSelector = [a, b]
      .map(({ value }) => this.findFirstLeafSelector(value) ?? '')
      .filter((selector) => !!selector)
      .map((selector) => `#${this.sanitizeSelector(selector)}`)
      .join(', ');
    const elements: HTMLElement[] = Array.from(this.document.querySelectorAll(combinedSelector));
    const aIndex = elements.findIndex((element) => element.id === a.key);
    const bIndex = elements.findIndex((element) => element.id === b.key);

    return aIndex === -1 ? 1 : bIndex === -1 ? -1 : aIndex - bIndex;
  };

  private getAbstractControlErrors(control: AbstractControl, path: string[] = []): NestedMessageValidationErrors {
    let childControlErrors;

    if (control instanceof FormGroup) {
      childControlErrors = Object.entries(control.controls)
        .map(([key, value]) => ({ [key]: this.getAbstractControlErrors(value, path.concat([key])) }))
        .reduce((errors, controlErrors) => ({ ...errors, ...controlErrors }), {});
    } else if (control instanceof FormArray) {
      childControlErrors = control.controls.map((arrayControlItem, index) =>
        this.getAbstractControlErrors(arrayControlItem, path.concat([String(index)])),
      );
    }

    return {
      path: this.formService.getIdentifier(path),
      self: control.errors,
      controls: childControlErrors,
    };
  }

  private sanitizeSelector(selector: string): string {
    return selector.replace(/\./g, '\\.');
  }

  private findFirstLeafSelector(error: NestedMessageValidationErrors): string {
    return error.controls
      ? Object.values(error.controls)
          .map((control) => control.path || this.findFirstLeafSelector(control))
          .find((path) => path)
      : error.path;
  }
}
