import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { RegulatedActivityDeleteComponent } from '@tasks/aer/submit/regulated-activities/delete/regulated-activity-delete.component';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { mockAerApplyPayload, mockState } from '../../testing/mock-permit-apply-action';

describe('RegulatedActivityDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let aerService: AerService;
  let component: RegulatedActivityDeleteComponent;
  let fixture: ComponentFixture<RegulatedActivityDeleteComponent>;

  const route = new ActivatedRouteStub({ activityId: mockAerApplyPayload.aer.regulatedActivities[0].id });
  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<RegulatedActivityDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get submitButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(RegulatedActivityDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    aerService = TestBed.inject(AerService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the regulated activity name', () => {
    expect(page.header.textContent).toContain('Are you sure you want to delete  ‘Ammonia production’?');
  });

  it('should delete the regulated activity', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
    const expectedRegulatedActivities = mockAerApplyPayload.aer.regulatedActivities.filter(
      (stream) => stream.id !== mockAerApplyPayload.aer.regulatedActivities[0].id,
    );

    page.submitButton.click();

    expect(postTaskSaveSpy).toHaveBeenCalledTimes(1);
    expect(postTaskSaveSpy).toHaveBeenCalledWith(
      { regulatedActivities: expectedRegulatedActivities },
      undefined,
      false,
      'regulatedActivities',
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route });
  });
});
