import { DecimalPipe } from '@angular/common';
import {
  AfterViewInit,
  Component,
  ContentChild,
  ElementRef,
  Input,
  OnInit,
  Optional,
  Renderer2,
  Self,
  ViewChild,
} from '@angular/core';
import { ControlContainer, ControlValueAccessor, NgControl } from '@angular/forms';

import { distinctUntilChanged, takeUntil, tap } from 'rxjs';

import { LabelDirective } from '../directives';
import { GovukValidators } from '../error-message/govuk-validators';
import { FormService } from '../form/form.service';
import { FormInput } from '../form/form-input';
import { GovukTextWidthClass, HTMLInputType } from './text-input.type';

// eslint-disable-next-line @angular-eslint/prefer-on-push-component-change-detection
@Component({
  selector: 'div[govuk-text-input]',
  templateUrl: './text-input.component.html',
  providers: [DecimalPipe],
})
export class TextInputComponent extends FormInput implements ControlValueAccessor, OnInit, AfterViewInit {
  @Input() hint: string;
  @Input() inputType: HTMLInputType = 'text';
  @Input() autoComplete = 'on';
  @Input() inputMode: string;
  @Input() spellCheck: boolean;
  @Input() numberFormat: string;
  @Input() widthClass: GovukTextWidthClass = 'govuk-!-width-full';
  @Input() prefix?: string;
  @Input() suffix?: string;
  @ContentChild(LabelDirective) templateLabel: LabelDirective;
  @ViewChild('input') input: ElementRef<HTMLInputElement>;
  currentLabel = 'Insert text';
  isLabelHidden = true;
  disabled: boolean;
  onChange: (_: any) => any;
  onBlur: (_: any) => any;

  constructor(
    @Self() @Optional() ngControl: NgControl,
    formService: FormService,
    private readonly decimalPipe: DecimalPipe,
    private readonly renderer: Renderer2,
    @Optional() container: ControlContainer,
  ) {
    super(ngControl, formService, container);
  }

  @Input() set label(label: string) {
    this.currentLabel = label;
    this.isLabelHidden = false;
  }

  ngOnInit(): void {
    super.ngOnInit();
    if (this.inputType === 'number') {
      const notNanValidator = GovukValidators.notNaN('Enter a numerical value');
      this.control.addValidators(notNanValidator);
      this.control.updateValueAndValidity();
    }
  }

  ngAfterViewInit(): void {
    this.writeValue(this.control.value);
    this.control.valueChanges
      .pipe(
        distinctUntilChanged((prev, curr) => {
          const previousValue = this.inputType === 'number' ? Number(prev) : prev;
          const currentValue = this.inputType === 'number' ? Number(curr) : curr;
          return previousValue === currentValue;
        }),
        tap((value) => this.handleInputValue(value)),
        takeUntil(this.destroy$),
      )
      .subscribe();
  }

  writeValue(value: any): void {
    if (this.input) {
      this.renderer.setProperty(
        this.input.nativeElement,
        'value',
        this.input.nativeElement === document.activeElement
          ? value
          : this.numberFormat && !Number.isNaN(Number(value))
          ? this.decimalPipe.transform(value, this.numberFormat)
          : value,
      );
    }
  }

  registerOnChange(onChange: any): void {
    this.onChange = onChange;
  }

  registerOnTouched(onBlur: any): void {
    this.onBlur = onBlur;
  }

  setDisabledState(isDisabled: boolean): void {
    this.disabled = isDisabled;
  }

  getInputValue(event: Event): string {
    return (event.target as HTMLInputElement).value;
  }

  onFocus(): void {
    switch (this.inputType) {
      case 'number':
        if (this.numberFormat) {
          this.renderer.setProperty(this.input.nativeElement, 'value', this.control.value);
        }
        break;
    }
  }

  onKeyDown(event: KeyboardEvent): void {
    if (event.key === 'Enter') {
      this.handleBlur(this.getInputValue(event));
    }
  }

  handleBlur(value?: string): void {
    this.onBlur(value);
  }

  private handleInputValue(value: string) {
    switch (this.inputType) {
      case 'number':
        if (value === null) {
          break;
        } else if (value === '') {
          this.control.setValue(null);
        } else if (!isNaN(Number(value))) {
          this.control.setValue(Number(value));

          if (this.input.nativeElement !== document.activeElement) {
            this.renderer.setProperty(
              this.input.nativeElement,
              'value',
              this.numberFormat ? this.decimalPipe.transform(value, this.numberFormat) : value,
            );
          }
        }
        break;
      case 'text':
        this.control.setValue(value ? (value.trim() === '' ? null : value.trim()) : value);
    }
  }
}
