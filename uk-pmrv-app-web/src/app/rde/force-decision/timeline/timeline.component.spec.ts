import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { RdeForceDecisionPayload } from 'pmrv-api';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { RdeStore } from '../../store/rde.store';
import { TimelineComponent } from './timeline.component';

describe('TimelineComponent', () => {
  let component: TimelineComponent;
  let fixture: ComponentFixture<TimelineComponent>;

  let store: RdeStore;
  let page: Page;

  const activatedRouteStub = new ActivatedRouteStub({ taskId: '279' });

  class Page extends BasePage<TimelineComponent> {
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [{ provide: ActivatedRoute, useValue: activatedRouteStub }],
      declarations: [TimelineComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(RdeStore);

    fixture = TestBed.createComponent(TimelineComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display rejected respond timeline', () => {
    store.setState({
      ...store.getState(),
      rdeForceDecisionPayload: {
        decision: 'REJECTED',
        evidence: 'this is an evidence',
      } as RdeForceDecisionPayload,
    });

    fixture.detectChanges();

    expect(page.answers).toEqual([
      ['Decision', 'Rejected'],
      ['Evidence of operator consent', 'this is an evidence'],
    ]);
  });

  it('should display accepted respond timeline', () => {
    store.setState({
      ...store.getState(),
      rdeForceDecisionPayload: {
        decision: 'ACCEPTED',
        evidence: 'this is an evidence',
      } as RdeForceDecisionPayload,
    });
    fixture.detectChanges();

    expect(page.answers).toEqual([
      ['Decision', 'Accepted'],
      ['Evidence of operator consent', 'this is an evidence'],
    ]);
  });
});
