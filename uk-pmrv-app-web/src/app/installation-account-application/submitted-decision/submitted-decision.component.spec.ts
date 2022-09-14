import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';

import { of } from 'rxjs';

import { SharedModule } from '@shared/shared.module';

import { InstallationAccountOpeningDecisionRequestActionPayload, RequestActionsService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../testing';
import { SubmittedDecisionComponent } from './submitted-decision.component';

describe('SubmittedDecisionComponent', () => {
  let hostComponent: SubmittedDecisionComponent;
  let fixture: ComponentFixture<SubmittedDecisionComponent>;
  let page: Page;

  class Page extends BasePage<SubmittedDecisionComponent> {
    get heading() {
      return this.query<HTMLHeadingElement>('h1');
    }
    get summaryListValues() {
      return this.queryAll<HTMLDivElement>('.govuk-summary-list__row')
        .map((row) => [row.querySelector('dt'), row.querySelector('dd')])
        .map((pair) => pair.map((element) => element.textContent.trim()));
    }
  }

  const requestActionsService = mockClass(RequestActionsService);
  requestActionsService.getRequestActionByIdUsingGET.mockReturnValue(
    of({
      payload: {
        decision: 'ACCEPTED',
        reason: 'Looks good',
      } as InstallationAccountOpeningDecisionRequestActionPayload,
    }),
  );

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [SubmittedDecisionComponent],
      imports: [SharedModule],
      providers: [
        { provide: RequestActionsService, useValue: requestActionsService },
        { provide: ActivatedRoute, useValue: new ActivatedRouteStub({ actionId: 1 }) },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SubmittedDecisionComponent);
    hostComponent = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(hostComponent).toBeTruthy();
  });

  it('should show the summary page', () => {
    expect(page.heading.textContent.trim()).toEqual('The regulator accepted the installation account application');
    expect(page.summaryListValues).toEqual([['Decision', 'Looks good']]);
  });
});
