import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';

import { PhaseBannerComponent } from './phase-banner.component';

describe('PhaseBannerComponent', () => {
  let component: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: ` <govuk-phase-banner [phase]="phase">Test content</govuk-phase-banner> `,
  })
  class TestComponent {
    phase = 'alpha';
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PhaseBannerComponent, TestComponent],
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

  it('should contain an alpha tag', () => {
    const element: HTMLElement = fixture.nativeElement;
    expect(element.querySelector('.govuk-tag').textContent.trim()).toEqual('alpha');
  });

  it('should contain a text', () => {
    const element: HTMLElement = fixture.nativeElement;
    expect(element.querySelector<HTMLSpanElement>('.govuk-phase-banner__text').textContent).toEqual('Test content');
  });
});
