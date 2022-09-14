import { Component } from '@angular/core';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { PermitSurrenderStore } from '../../../store/permit-surrender.store';
import { mockTaskState } from '../../testing/mock-state';
import { SummaryDetailsComponent } from './summary-details.component';

describe('SummaryDetailsComponent', () => {
  @Component({
    template: `
      <app-cessation-summary-details
        [cessation]="cessation$ | async"
        [allowancesSurrenderRequired]="allowancesSurrenderRequired$ | async"
        [isEditable]="isEditable$ | async"
      ></app-cessation-summary-details>
    `,
  })
  class TestComponent {
    cessation$ = store.select('cessation');
    allowancesSurrenderRequired$ = store.select('allowancesSurrenderRequired');
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
      imports: [RouterTestingModule, SharedModule],
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

  it('should render summary with allowances required', () => {
    store.setState({
      ...mockTaskState,
    });
    createComponent();
    const navigateSpy = jest.spyOn(router, 'navigate');

    expect(page.rows).toEqual([
      ['Outcome of the emission report determination', 'Approved'],
      ['When were the installation allowances surrendered?', '13 Dec 2012'],
      ['Surrendered allowances', '100'],
      ['Installations annual reportable emissions', '123.54 tCO2e'],
      ['Should the installations subsistence fee be refunded?', 'Yes'],
      [
        'Text for the official notice',
        'Satisfied that the requirements set out in the schedule have been compiled with',
      ],
      ['Notes', 'notes'],
    ]);

    page.changeLinks[0].click();
    expect(navigateSpy).toHaveBeenCalledWith(['../outcome'], { relativeTo: route, state: { changing: true } });
  });

  it('should render summary with allowances not required', () => {
    store.setState({
      ...mockTaskState,
      allowancesSurrenderRequired: false,
    });
    createComponent();

    expect(page.rows).toEqual([
      ['Outcome of the emission report determination', 'Approved'],
      ['Installations annual reportable emissions', '123.54 tCO2e'],
      ['Should the installations subsistence fee be refunded?', 'Yes'],
      [
        'Text for the official notice',
        'Satisfied that the requirements set out in the schedule have been compiled with',
      ],
      ['Notes', 'notes'],
    ]);
  });
});
