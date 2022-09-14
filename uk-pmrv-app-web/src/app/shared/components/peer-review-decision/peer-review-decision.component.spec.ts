import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteStub, BasePage } from '../../../../testing';
import { SharedPermitModule } from '../../../permit-application/shared/shared-permit.module';
import { SharedModule } from '../../shared.module';
import { PeerReviewDecisionComponent } from './peer-review-decision.component';

describe('PeerReviewDecisionComponent', () => {
  let page: Page;

  let component: PeerReviewDecisionComponent;
  let fixture: ComponentFixture<PeerReviewDecisionComponent>;

  let route: ActivatedRouteStub;

  class Page extends BasePage<PeerReviewDecisionComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get errorSummaryLinks() {
      return Array.from(this.query<HTMLDivElement>('.govuk-error-summary').querySelectorAll('a')).map((item) =>
        item.textContent.trim(),
      );
    }
  }

  beforeEach(async () => {
    route = new ActivatedRouteStub({ taskId: '237', index: '0' });

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      providers: [{ provide: ActivatedRoute, useValue: route }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(PeerReviewDecisionComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should submit a valid form and navigate to summary', () => {
    expect(page.errorSummary).toBeFalsy();

    page.submitButton.click();
    fixture.detectChanges();

    expect(page.errorSummary).toBeTruthy();
    expect(page.errorSummaryLinks).toEqual(['Enter your decision', 'Enter notes']);
  });
});
