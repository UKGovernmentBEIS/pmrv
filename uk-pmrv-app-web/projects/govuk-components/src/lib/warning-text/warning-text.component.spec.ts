import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { WarningTextComponent } from './warning-text.component';

describe('WarningTextComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: ` <govuk-warning-text [assistiveText]="text">Test warning</govuk-warning-text> `,
  })
  class TestComponent {
    text = 'Warn';
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [WarningTextComponent, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should contain a hidden warn text', () => {
    const element: HTMLElement = fixture.nativeElement;
    expect(element.querySelector<HTMLSpanElement>('.govuk-warning-text__assistive').textContent).toEqual('Warn');
  });

  it('should contain a text', () => {
    const element: HTMLElement = fixture.nativeElement;
    expect(element.querySelector<HTMLElement>('.govuk-warning-text__text').textContent).toEqual('WarnTest warning');
  });
});
