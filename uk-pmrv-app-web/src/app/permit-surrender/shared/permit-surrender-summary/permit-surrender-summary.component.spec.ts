import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';
import { mockActionState } from '../../testing/mock-state';
import { SharedPermitSurrenderModule } from '../shared-permit-surrender.module';
import { PermitSurrenderSummaryComponent } from './permit-surrender-summary.component';

describe('PermitSurrenderSummaryComponent', () => {
  let component: PermitSurrenderSummaryComponent;
  let fixture: ComponentFixture<PermitSurrenderSummaryComponent>;
  let store: PermitSurrenderStore;
  let page: Page;

  class Page extends BasePage<PermitSurrenderSummaryComponent> {
    get creationDate() {
      return this.query<HTMLParagraphElement>('p.govuk-body');
    }

    get rows() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(PermitSurrenderSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedPermitSurrenderModule],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitSurrenderStore);

    store.setState({
      ...store.getState(),
      ...mockActionState,
    });
  });

  it('should create', () => {
    createComponent();
    expect(component).toBeTruthy();
  });

  it('should render surrender values with files', () => {
    createComponent();
    expect(page.creationDate.textContent).toEqual('22 Jan 2022');
    expect(page.rows).toEqual([
      ['Date the regulated activities ended', '13 Dec 2012'],
      ['Explain why the regulated activities have ended', 'justify'],
      ['Supporting documents', 'test.jpg'],
    ]);
  });

  it('should render surrender values with no files', () => {
    store.setState({
      ...store.getState(),
      permitSurrender: {
        ...store.permitSurrender,
        documentsExist: false,
        documents: [],
      },
      permitSurrenderAttachments: {},
    });
    createComponent();
    expect(page.creationDate.textContent).toEqual('22 Jan 2022');
    expect(page.rows).toEqual([
      ['Date the regulated activities ended', '13 Dec 2012'],
      ['Explain why the regulated activities have ended', 'justify'],
      ['Supporting documents', 'No'],
    ]);
  });
});
