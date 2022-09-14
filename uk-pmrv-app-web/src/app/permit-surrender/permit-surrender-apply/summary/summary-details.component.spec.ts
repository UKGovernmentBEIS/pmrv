import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { PermitSurrenderModule } from '../../permit-surrender.module';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { mockTaskState } from '../../testing/mock-state';
import { SummaryDetailsComponent } from './summary-details.component';

describe('SummaryDetailsComponent', () => {
  let component: SummaryDetailsComponent;
  let fixture: ComponentFixture<SummaryDetailsComponent>;
  let store: PermitSurrenderStore;
  let page: Page;

  class Page extends BasePage<SummaryDetailsComponent> {
    get answers() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
    get confirmButton() {
      return this.query<HTMLButtonElement>('.govuk-button');
    }
  }

  const route = new ActivatedRouteStub({ taskId: mockTaskState.requestTaskId }, null, {
    statusKey: 'SURRENDER_APPLY',
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(SummaryDetailsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitSurrenderModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);
  });

  afterEach(() => jest.clearAllMocks());

  beforeEach(() => {
    store.setState({
      ...store.getState(),
      requestTaskId: mockTaskState.requestTaskId,
      isEditable: true,
      permitSurrender: {
        stopDate: '2012-12-21',
        justification: 'justify',
        documentsExist: true,
        documents: ['e227ea8a-778b-4208-9545-e108ea66c114'],
      },
      permitSurrenderAttachments: { 'e227ea8a-778b-4208-9545-e108ea66c114': 'hello.txt' },
      sectionsCompleted: { SURRENDER_APPLY: true },
    });
  });

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render surrender values', () => {
    createComponent();
    expect(page.answers).toEqual([
      ['Date the regulated activities ended', '21 Dec 2012'],
      ['Explain why the regulated activities have ended', 'justify'],
      ['Supporting documents', 'hello.txt'],
    ]);
  });
});
