import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { RfiStore } from '../../store/rfi.store';
import { TimelineComponent } from './timeline.component';

describe('TimelineComponent', () => {
  let store: RfiStore;
  let component: TimelineComponent;
  let fixture: ComponentFixture<TimelineComponent>;
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
    store = TestBed.inject(RfiStore);
    store.setState({
      ...store.getState(),
      rfiQuestionPayload: {
        ...store.getState().rfiSubmitPayload,
        questions: ['where', 'what'],
        files: ['regulatorFile'],
      },
      rfiResponsePayload: {
        ...store.getState().rfiResponsePayload,
        answers: ['there', 'this'],
        files: ['operatorFile'],
      },
      rfiAttachments: { regulatorFile: 'regulatorFile', operatorFile: 'operatorFile' },
    });
    fixture = TestBed.createComponent(TimelineComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display respond timeline', () => {
    fixture.detectChanges();

    expect(page.answers).toEqual([
      ['Question 1', 'where'],
      ['Response', 'there'],
      ['Question 2', 'what'],
      ['Response', 'this'],
      ['Regulator files', 'regulatorFile'],
      ['Operator files', 'operatorFile'],
    ]);
  });
});
