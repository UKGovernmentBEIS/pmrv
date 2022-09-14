import { ComponentFixture } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

export function changeInputValue(fixture: ComponentFixture<any>, selector: string, value?: any): void {
  const element = fixture.debugElement.query(By.css(selector));

  if (element.name === 'select') {
    const nativeElement: HTMLElement = element.nativeElement;
    value = Array.from(nativeElement.querySelectorAll('option')).find(
      (option, index) => option.value === `${index}: ${value}` || option.value === value,
    ).value;
  }

  const hasInputEvent = element.name === 'select' || ['radio', 'checkbox', 'file'].includes(element.attributes.type);

  if (element.name === 'select') {
    element.nativeElement.value = value;
  }

  const event = {
    target:
      element.attributes.type === 'radio'
        ? element.nativeElement
        : element.attributes.type === 'file'
        ? { ...element.nativeElement, files: Array.isArray(value) ? value : [value] }
        : { ...element.nativeElement, value },
  };

  element.triggerEventHandler(hasInputEvent ? 'change' : 'input', event);
  element.triggerEventHandler('blur', event);
}

export function buttonClick(fixture: ComponentFixture<any>): void {
  (fixture.nativeElement as HTMLElement).querySelector<HTMLButtonElement>('button').click();
}

type InputElement = HTMLInputElement | HTMLSelectElement | HTMLTextAreaElement;

export function getElement<T extends HTMLElement>(fixture: ComponentFixture<any>, selector: string): T {
  return (fixture.nativeElement as HTMLElement).querySelector<T>(selector);
}

export function getInputValue(
  fixture: ComponentFixture<any>,
  selector: string | HTMLInputElement | HTMLSelectElement,
): any {
  if (selector instanceof Element) {
    selector = selector.id ? `[id="${selector.id}"]` : `[name="${selector.name}"]`;
  }

  const targetProps = fixture.debugElement.query(By.css(selector)).properties;

  return targetProps.checked ?? (getElement<InputElement>(fixture, selector).value || targetProps.value);
}
