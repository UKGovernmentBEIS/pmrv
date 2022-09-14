import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';

import { DetailsComponent } from './details.component';

describe('DetailsComponent', () => {
  let component: DetailsComponent;
  let testComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  @Component({
    template: ` <govuk-details [summary]="summary"></govuk-details> `,
  })
  class TestComponent {
    summary: string;
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DetailsComponent, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    testComponent = fixture.componentInstance;
    component = fixture.debugElement.query(By.directive(DetailsComponent)).componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the summary', () => {
    testComponent.summary = 'Something is up';
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;

    expect(element.querySelector('.govuk-details__summary-text').textContent).toEqual(testComponent.summary);
  });
});
