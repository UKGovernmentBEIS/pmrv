import {
  AfterContentInit,
  ChangeDetectorRef,
  Component,
  ContentChildren,
  ElementRef,
  HostBinding,
  HostListener,
  Inject,
  Input,
  OnInit,
  Optional,
  QueryList,
} from '@angular/core';
import { ControlValueAccessor, FormControl, FormGroupDirective, NgControl, NgForm } from '@angular/forms';

import { BehaviorSubject, Subject } from 'rxjs';
import { filter, skip, startWith, takeUntil, tap, withLatestFrom } from 'rxjs/operators';

import { FormService } from 'govuk-components';

import { DestroySubject } from '../../core/services/destroy-subject.service';
import { DOCUMENT_EVENT } from '../services/document-event.service';
import { MultiSelectItemComponent } from './multi-select-item/multi-select-item.component';

/*
  eslint-disable
  @typescript-eslint/no-unused-vars,
  @typescript-eslint/no-empty-function,
  @angular-eslint/prefer-on-push-component-change-detection
*/
@Component({
  selector: 'div[app-multi-select]',
  templateUrl: './multi-select.component.html',
  styleUrls: ['./multi-select.component.scss'],
  providers: [DestroySubject],
})
export class MultiSelectComponent implements ControlValueAccessor, OnInit, AfterContentInit {
  @HostBinding('class.govuk-!-display-block') readonly govukDisplayBlock = true;
  @HostBinding('class.govuk-form-group') readonly govukFormGroupClass = true;
  @HostBinding('class.govuk-form-group--error')
  get govukFormGroupErrorClass(): boolean {
    return this.shouldDisplayErrors;
  }

  @Input() label: string;
  @Input() hint: string;
  @Input() showErrors: boolean;

  @ContentChildren(MultiSelectItemComponent) options: QueryList<MultiSelectItemComponent>;

  isOpen = new BehaviorSubject<boolean>(false);
  isDisabled: boolean;
  itemMap: { [key: string]: string };
  hasBeenTouched = false;
  currentValue = [];
  private onBlur: () => any;
  private onChange: (value: any) => void;

  get shouldDisplayErrors(): boolean {
    return this.control?.invalid && (!this.form || this.form.submitted || this.showErrors);
  }

  private get form(): FormGroupDirective | NgForm | null {
    return this.root ?? this.rootNgForm;
  }

  @HostListener('keydown', ['$event'])
  onKeyDown(event: KeyboardEvent): void {
    if (event.code === 'Escape' && this.isOpen.getValue()) {
      this.isOpen.next(false);
    }
  }

  constructor(
    @Inject(DOCUMENT_EVENT) readonly documentEvent: Subject<FocusEvent | PointerEvent>,
    private readonly ngControl: NgControl,
    private readonly formService: FormService,
    private readonly destroy$: DestroySubject,
    private readonly elRef: ElementRef,
    private readonly cdRef: ChangeDetectorRef,
    @Optional() private readonly root: FormGroupDirective,
    @Optional() private readonly rootNgForm: NgForm,
  ) {
    ngControl.valueAccessor = this;
  }

  ngOnInit(): void {
    this.documentEvent
      .pipe(
        takeUntil(this.destroy$),
        withLatestFrom(this.isOpen),
        filter(([event]) => event.type === 'focusin' || event.type === 'click'),
      )
      .subscribe(([event, isOpen]) => {
        if (!this.elRef.nativeElement.contains(event.target)) {
          if (isOpen) {
            this.isOpen.next(false);
          }
          if (this.hasBeenTouched && !this.control.touched) {
            this.control.markAsTouched();
            this.cdRef.markForCheck();
          }
        } else {
          this.hasBeenTouched = true;
        }
      });
    this.isOpen.pipe(takeUntil(this.destroy$), skip(1)).subscribe((res) => {
      if (!res) {
        this.onBlur();
      }
    });
  }

  get identifier(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  get control(): FormControl {
    return this.ngControl.control as FormControl;
  }

  click(): void {
    this.isOpen.next(!this.isOpen.getValue());
  }

  ngAfterContentInit(): void {
    this.options.changes
      .pipe(
        takeUntil(this.destroy$),
        startWith(this.options),
        tap((options: QueryList<MultiSelectItemComponent>) => {
          this.itemMap = options.reduce((result, option) => ({ ...result, [option.itemValue]: option.label }), {});
          options.forEach((option, index) => {
            option.groupIdentifier = this.identifier;
            option.index = index;
            option.registerOnChange(() => {
              this.currentValue = options.filter((opt) => opt.isChecked).map((opt) => opt.itemValue);
              this.onChange(this.currentValue);
            });
            option.registerOnTouched(() => this.onBlur());
            option.cdRef.markForCheck();
          });

          this.writeValue(this.control.value);
          this.setDisabledState(this.control.disabled);
        }),
      )
      .subscribe();
  }

  writeValue(value: any): void {
    this.currentValue = value;
    if (this.options) {
      this.options.forEach((option) => option.writeValue(value?.includes(option.itemValue) ?? false));
    }
  }

  registerOnChange(fn: (value: any) => void) {
    this.onChange = fn;
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
    if (this.options) {
      this.options.forEach((option) => option.setDisabledState(isDisabled));
    }
  }
}
