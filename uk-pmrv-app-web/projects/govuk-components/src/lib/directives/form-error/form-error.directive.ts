import {
  ChangeDetectorRef,
  ComponentFactoryResolver,
  ComponentRef,
  Directive,
  DoCheck,
  ElementRef,
  EmbeddedViewRef,
  HostBinding,
  Input,
  OnDestroy,
  OnInit,
  Optional,
  Renderer2,
  Self,
  ViewContainerRef,
} from '@angular/core';
import { ControlContainer, FormGroupDirective, NgControl, NgForm } from '@angular/forms';

import { BehaviorSubject, combineLatest, startWith, Subject, takeUntil, tap } from 'rxjs';

import { ErrorMessageComponent } from '../../error-message/error-message.component';

@Directive({ selector: '[govukFormError]' })
export class FormErrorDirective implements OnDestroy, OnInit, DoCheck {
  @Input() id: string;

  private readonly destroy$ = new Subject<void>();
  private touch$ = new BehaviorSubject(false);
  private errorComponent: ComponentRef<ErrorMessageComponent>;
  private isSubmitted = false;

  constructor(
    @Self() private readonly formControl: NgControl,
    private readonly elementRef: ElementRef,
    private readonly viewContainer: ViewContainerRef,
    private readonly componentFactoryResolver: ComponentFactoryResolver,
    private readonly renderer: Renderer2,
    private readonly changeDetectorRef: ChangeDetectorRef,
    @Optional() private readonly container: ControlContainer,
  ) {}

  @HostBinding('attr.id')
  get identifier(): string {
    return this.id;
  }

  private get nativeElement(): HTMLElement {
    return this.elementRef.nativeElement;
  }

  private get form(): FormGroupDirective | NgForm | null {
    return this.container &&
      (this.container.formDirective instanceof FormGroupDirective || this.container.formDirective instanceof NgForm)
      ? this.container.formDirective
      : null;
  }

  ngOnInit(): void {
    combineLatest([
      this.formControl.statusChanges.pipe(startWith(this.formControl.status)),
      this.formControl.control.updateOn === 'submit'
        ? this.form?.ngSubmit.pipe(tap(() => (this.isSubmitted = status !== 'DISABLED')))
        : this.touch$,
    ])
      .pipe(takeUntil(this.destroy$))
      .subscribe(([status]) => this.handleErrorStatus(status));
  }

  ngDoCheck(): void {
    if (this.touch$.getValue() !== this.formControl.touched) {
      this.touch$.next(this.formControl.touched);
    }
  }

  ngOnDestroy(): void {
    this.destroy$.next();
    this.destroy$.unsubscribe();
  }

  private attachErrorComponent() {
    const componentFactory = this.componentFactoryResolver.resolveComponentFactory(ErrorMessageComponent);
    this.errorComponent = this.viewContainer.createComponent(componentFactory, 0);
    this.errorComponent.instance.identifier = this.id;

    const htmlElement: HTMLElement = (this.errorComponent.hostView as EmbeddedViewRef<ErrorMessageComponent>)
      .rootNodes[0];

    this.renderer.insertBefore(this.nativeElement.parentNode, htmlElement, this.nativeElement);
  }

  private detachErrorComponent() {
    if (this.errorComponent) {
      this.errorComponent.destroy();
      this.errorComponent = null;
    }
  }

  private handleErrorStatus(status: string) {
    const parentFormGroup: HTMLElement = this.nativeElement.closest('.govuk-form-group');

    if (status === 'INVALID' && this.shouldDisplayErrors()) {
      if (parentFormGroup) {
        this.renderer.addClass(parentFormGroup, 'govuk-form-group--error');

        if (!this.errorComponent) {
          this.attachErrorComponent();
        }

        this.errorComponent.instance.errors = this.formControl.errors;
        this.errorComponent.changeDetectorRef.markForCheck();
      }

      this.renderer.addClass(this.nativeElement, this.getErrorClass());
    } else {
      if (parentFormGroup) {
        this.renderer.removeClass(parentFormGroup, 'govuk-form-group--error');
        this.detachErrorComponent();
      }

      this.renderer.removeClass(this.nativeElement, this.getErrorClass());
    }

    this.changeDetectorRef.markForCheck();
  }

  private shouldDisplayErrors(): boolean {
    return this.formControl.invalid && (!this.form || this.isSubmitted);
  }

  private getErrorClass(): string {
    switch (this.nativeElement.tagName) {
      case 'SELECT':
        return 'govuk-select--error';
      case 'TEXTAREA':
        return 'govuk-textarea--error';
      default:
        return 'govuk-input--error';
    }
  }
}
