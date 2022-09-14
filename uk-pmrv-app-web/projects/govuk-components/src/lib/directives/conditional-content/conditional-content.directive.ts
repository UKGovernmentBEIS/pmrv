import { ContentChildren, Directive, QueryList } from '@angular/core';
import { AbstractControl, AbstractControlDirective, ControlContainer, NgControl } from '@angular/forms';

@Directive({ selector: '[govukConditionalContent]' })
export class ConditionalContentDirective {
  @ContentChildren(NgControl, { descendants: true }) private readonly childControls: QueryList<NgControl>;
  @ContentChildren(ControlContainer, { descendants: true })
  private readonly childContainers: QueryList<ControlContainer>;
  @ContentChildren(ConditionalContentDirective, { descendants: true })
  private readonly childConditionals: QueryList<ConditionalContentDirective>;

  private get childControlsAndContainers(): AbstractControlDirective[] {
    return [...this.childControls.toArray(), ...this.childContainers.toArray()];
  }

  enableControls(): void {
    // There seems to be an extreme case in content projection that it detects itself
    const nestedControls: AbstractControl[] = [].concat(
      ...this.childConditionals
        .filter((conditional) => conditional !== this)
        .map((conditional) => conditional.childControlsAndContainers.map(({ control }) => control)),
    );

    const nestedStatuses = nestedControls.map((nested) => nested.status);

    this.childControlsAndContainers
      .filter(({ control }) => !nestedControls.includes(control))
      .forEach(({ control }) => control.enable({ emitEvent: this.shouldEmitEvent(control) }));

    nestedControls
      .filter((control, index) => nestedStatuses[index] === 'DISABLED' && !control.value)
      .forEach((control) => control.disable({ emitEvent: this.shouldEmitEvent(control) }));
  }

  disableControls(): void {
    this.childControlsAndContainers.forEach(({ control }) =>
      control.disable({ emitEvent: this.shouldEmitEvent(control) }),
    );
  }

  private shouldEmitEvent(control: AbstractControl): boolean {
    return control.updateOn !== 'submit';
  }
}
