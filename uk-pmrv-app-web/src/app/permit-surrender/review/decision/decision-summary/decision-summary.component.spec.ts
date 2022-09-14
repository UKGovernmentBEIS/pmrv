import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { SharedPermitSurrenderModule } from '../../../shared/shared-permit-surrender.module';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';
import { mockTaskState } from '../../../testing/mock-state';
import { DecisionSummaryComponent } from './decision-summary.component';

describe('DecisionSummaryComponent', () => {
  @Component({
    template: `
      <app-permit-surrender-decision-summary
        [reviewDecision$]="reviewDecision$"
        [isEditable]="isEditable$ | async"
      ></app-permit-surrender-decision-summary>
    `,
  })
  class TestComponent {
    reviewDecision$ = store.select('reviewDecision');
    isEditable$ = store.select('isEditable');
  }

  let component: DecisionSummaryComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: PermitSurrenderStore;
  let page: Page;

  class Page extends BasePage<TestComponent> {
    get rows() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get changeLink() {
      return this.query<HTMLAnchorElement>('h2 a');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(DecisionSummaryComponent)).componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DecisionSummaryComponent, TestComponent],
      imports: [RouterTestingModule, SharedPermitSurrenderModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);

    store.setState({
      ...mockTaskState,
      reviewDecision: {
        type: 'ACCEPTED',
        notes: 'notes',
      },
    });
  });

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render surrender decision summary', () => {
    createComponent();
    expect(page.rows).toEqual([
      ['Decision status', 'Accepted'],
      ['Notes', 'notes'],
    ]);
  });

  it('should render change link', () => {
    createComponent();
    expect(page.changeLink).toBeTruthy();
  });

  it('should not render change link if not editable', () => {
    store.setState({
      ...mockTaskState,
      isEditable: false,
    });
    createComponent();
    expect(page.changeLink).toBeNull();
  });
});
