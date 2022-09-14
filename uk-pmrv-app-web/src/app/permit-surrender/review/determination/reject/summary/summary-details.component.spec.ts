import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { PermitSurrenderReviewDeterminationReject } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { SharedPermitSurrenderModule } from '../../../../shared/shared-permit-surrender.module';
import { PermitSurrenderStore } from '../../../../store/permit-surrender.store';
import { mockTaskState } from '../../../../testing/mock-state';
import { SummaryDetailsComponent } from './summary-details.component';

describe('SummaryDetailsComponent', () => {
  @Component({
    template: `
      <app-reject-determination-summary-details
        [rejectDetermination]="rejectDetermination$ | async"
      ></app-reject-determination-summary-details>
    `,
  })
  class TestComponent {
    rejectDetermination$ = store.select('reviewDetermination');
    isEditable$ = store.select('isEditable');
  }

  let component: SummaryDetailsComponent;
  let fixture: ComponentFixture<TestComponent>;
  let store: PermitSurrenderStore;
  let page: Page;
  let router: Router;

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, null);

  class Page extends BasePage<TestComponent> {
    get rows() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }

    get changeLinks() {
      return this.queryAll<HTMLAnchorElement>('a');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(TestComponent);
    component = fixture.debugElement.query(By.directive(SummaryDetailsComponent)).componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SummaryDetailsComponent, TestComponent],
      imports: [RouterTestingModule, SharedPermitSurrenderModule, SharedModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
  });

  it('should create', () => {
    store.setState({
      ...mockTaskState,
    });
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render reject summary', () => {
    store.setState({
      ...mockTaskState,
      reviewDecision: {
        type: 'REJECTED',
        notes: 'rejected notes',
      },
      reviewDetermination: {
        type: 'REJECTED',
        reason: 'reason',
        officialRefusalLetter: 'official refusal letter',
        shouldFeeBeRefundedToOperator: true,
      } as PermitSurrenderReviewDeterminationReject,
    });
    createComponent();
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.rows).toEqual([
      ['Decision', 'Reject'],
      ['Reason for decision', 'reason'],
      ['Official refusal letter', 'official refusal letter'],
      ['Operator refund', 'Yes'],
    ]);

    page.changeLinks[0].click();
    fixture.detectChanges();
    expect(navigateSpy).toHaveBeenCalledWith(['../../'], { relativeTo: route, state: { changing: true } });

    page.changeLinks[1].click();
    fixture.detectChanges();
    expect(navigateSpy).toHaveBeenCalledWith(['../reason'], { relativeTo: route, state: { changing: true } });

    page.changeLinks[2].click();
    fixture.detectChanges();
    expect(navigateSpy).toHaveBeenCalledWith(['../refusal'], { relativeTo: route, state: { changing: true } });

    page.changeLinks[3].click();
    fixture.detectChanges();
    expect(navigateSpy).toHaveBeenCalledWith(['../refund'], { relativeTo: route, state: { changing: true } });
  });
});
