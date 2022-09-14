import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { RelatedActionsComponent } from "@shared/components/related-actions/related-actions.component";
import { BasePage } from '@testing';

import { SharedModule } from '../../shared.module';

describe('RelatedActionsComponent', () => {
  let component: RelatedActionsComponent;
  let fixture: ComponentFixture<RelatedActionsComponent>;
  let page: Page;

  class Page extends BasePage<RelatedActionsComponent> {
    get links() {
      return this.queryAll<HTMLLinkElement>('li > a');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(RelatedActionsComponent);
    component = fixture.componentInstance;
    component.isAssignable = true;
    component.taskId = 1;
    page = new Page(fixture);
  });

  it('should create', () => {
    fixture.detectChanges();
    expect(component).toBeTruthy();
  });

  it('should display the links', () => {
    fixture.detectChanges();

    expect(page.links.map((el) => [el.href, el.textContent])).toEqual([
      ['http://localhost/tasks/1/change-assignee', 'Reassign task']
    ]);
  });

  it('should display the links with actions', () => {
    component.allowedActions = ['RFI_SUBMIT', 'RDE_SUBMIT', 'PERMIT_ISSUANCE_RECALL_FROM_AMENDS',
      'PERMIT_NOTIFICATION_FOLLOW_UP_RECALL_FROM_AMENDS', 'AER_SAVE_APPLICATION'];
    fixture.detectChanges();

    expect(page.links.map((el) => [el.href, el.textContent])).toEqual([
      ['http://localhost/tasks/1/change-assignee', 'Reassign task'],
      ['http://localhost/rfi/1/questions', 'Request for information'],
      ['http://localhost/rde/1/extend-determination', 'Request deadline extension'],
      ['http://localhost/recall-from-amends', 'Recall the permit'],
      ['http://localhost/recall-from-amends', 'Recall your response']
    ]);
  });
});