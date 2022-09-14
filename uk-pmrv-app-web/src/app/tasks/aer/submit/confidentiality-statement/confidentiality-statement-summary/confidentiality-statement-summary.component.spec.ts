import { ComponentFixture, TestBed } from '@angular/core/testing';
import { Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { AerModule } from '@tasks/aer/aer.module';
import { mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { mockStateBuild } from '../../testing/mock-state';
import { ConfidentialityStatementSummaryComponent } from './confidentiality-statement-summary.component';

describe('ConfidentialityStatementSummaryComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: ConfidentialityStatementSummaryComponent;
  let fixture: ComponentFixture<ConfidentialityStatementSummaryComponent>;

  class Page extends BasePage<ConfidentialityStatementSummaryComponent> {
    get sections() {
      return this.queryAll<HTMLDListElement>('dl');
    }
    get summaryHeader() {
      return this.query<HTMLElement>('h2 span');
    }
    get notificationBanner() {
      return this.query('.govuk-notification-banner');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(ConfidentialityStatementSummaryComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
  }

  beforeEach(() => {
    const router = TestBed.inject(Router);
    jest.spyOn(router, 'getCurrentNavigation').mockReturnValue({ extras: { state: { notification: true } } } as any);
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
  });

  beforeEach(createComponent);

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should list the other sections', () => {
    expect(page.sections).toHaveLength(2);
    expect(
      page.sections.map((permit) => Array.from(permit.querySelectorAll('dd')).map((dd) => dd.textContent)),
    ).toEqual([
      ['Section 1', 'Explanation 1'],
      ['Section 2', 'Explanation 2'],
    ]);
    expect(page.summaryHeader.textContent).toEqual('Commercially confidential sections');
    expect(page.summaryHeader.classList).not.toContain('govuk-visually-hidden');
    expect(page.notificationBanner).toBeTruthy();
  });

  it('should mention that no other sections exist', () => {
    store.setState(mockStateBuild({ confidentialityStatement: { exist: false } }));
    fixture.detectChanges();

    expect(page.sections).toHaveLength(1);
    expect(
      page.sections.map((permit) => Array.from(permit.querySelectorAll('dd')).map((dd) => dd.textContent)),
    ).toEqual([['No']]);
    expect(page.summaryHeader.classList).toContain('govuk-visually-hidden');
  });
});
