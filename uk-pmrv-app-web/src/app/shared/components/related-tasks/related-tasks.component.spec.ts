import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { BasePage } from '../../../../testing';
import { SharedModule } from '../../shared.module';
import { RelatedTasksComponent } from './related-tasks.component';

describe('RelatedTasksComponent', () => {
  let component: RelatedTasksComponent;
  let fixture: ComponentFixture<RelatedTasksComponent>;
  let page: Page;

  class Page extends BasePage<RelatedTasksComponent> {
    get heading() {
      return this.query('h2');
    }
    get items() {
      return this.queryAll('.govuk-heading-s').map((el) => el.textContent);
    }
    get daysRemaining() {
      return this.queryAll('.govuk-body').map((el) => el.textContent.trim());
    }
    get links() {
      return this.queryAll<HTMLLinkElement>('a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RelatedTasksComponent);
    component = fixture.componentInstance;
    component.items = [
      {
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_WAIT_FOR_AMENDS',
        taskId: 1,
        daysRemaining: 13,
      },
      {
        requestType: 'PERMIT_ISSUANCE',
        taskType: 'PERMIT_ISSUANCE_APPLICATION_AMENDS_SUBMIT',
        taskId: 2,
        daysRemaining: 14,
      },
    ];
    page = new Page(fixture);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display items', () => {
    expect(page.heading.textContent).toEqual('Related tasks');

    expect(page.items).toEqual(['Permit determination', 'Permit application']);

    expect(page.daysRemaining).toEqual(['Days Remaining: 13', 'Days Remaining: 14']);

    expect(page.links.map((el) => el.getAttribute('href'))).toEqual([
      '/permit-application/1/review',
      '/permit-application/2',
    ]);
  });
});
