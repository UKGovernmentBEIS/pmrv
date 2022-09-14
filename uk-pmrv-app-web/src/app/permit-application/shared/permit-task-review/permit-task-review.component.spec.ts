import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { PermitTaskReviewComponent } from './permit-task-review.component';

describe('PermitTaskReviewComponent', () => {
  let page: Page;
  let hostComponent: TestComponent;
  let fixture: ComponentFixture<TestComponent>;

  class Page extends BasePage<TestComponent> {
    get pageHeadings(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h1');
    }
    get headings(): HTMLHeadingElement[] {
      return this.queryAll<HTMLHeadingElement>('h2');
    }
  }

  @Component({
    template: `
      <app-permit-task-review [breadcrumb]="true" heading="Transferred CO2">
        <h2 class="govuk-heading-m">Transferred CO2 details</h2>
      </app-permit-task-review>
    `,
  })
  class TestComponent {}

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [SharedModule, RouterTestingModule],
      declarations: [PermitTaskReviewComponent, TestComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TestComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(hostComponent).toBeTruthy();
  });

  it('should display all internal titles', () => {
    expect(page.pageHeadings).toHaveLength(1);
    expect(page.pageHeadings[0].textContent.trim()).toEqual('Transferred CO2');

    expect(page.headings).toHaveLength(1);
    expect(page.headings[0].textContent.trim()).toEqual('Transferred CO2 details');
  });
});
