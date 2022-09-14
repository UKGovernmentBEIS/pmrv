import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { TemplateComponent } from './template.component';

describe('TemplateComponent', () => {
  let component: TemplateComponent;
  let fixture: ComponentFixture<TestComponent>;
  let page: Page;

  @Component({
    template: ` <app-template taskKey="monitoringApproaches.N2O.emissionDetermination"></app-template> `,
  })
  class TestComponent {}

  class Page extends BasePage<TestComponent> {
    get textContent() {
      return this.queryAll<HTMLParagraphElement>('.govuk-body p');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [TestComponent, TemplateComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(TemplateComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the content of subtask', () => {
    expect(page.textContent).toBeTruthy();
    expect(page.textContent.map((element) => element.textContent.trim())).toEqual([
      'Provide details about the procedures describing the calculation formulae used to determine the annual nitrous oxide emissions and the method of data aggregation.',
      'Get help with emission determination procedure.',
    ]);
  });
});
