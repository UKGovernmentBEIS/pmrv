import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, convertToParamMap } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { DeleteComponent } from '@tasks/aer/submit/prtr/activity/delete/delete.component';
import { mockPostBuild, mockStateBuild } from '@tasks/aer/submit/testing/mock-state';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('DeleteComponent', () => {
  let page: Page;
  let store: CommonTasksStore;
  let component: DeleteComponent;
  let fixture: ComponentFixture<DeleteComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<DeleteComponent> {
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
        { provide: TasksService, useValue: tasksService },
        {
          provide: ActivatedRoute,
          useValue: {
            paramMap: of(convertToParamMap({ index: 1 })),
            snapshot: {
              paramMap: convertToParamMap({ index: 1 }),
            },
          },
        },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(
      mockStateBuild({
        pollutantRegisterActivities: {
          activities: ['_1_A_2_B_NON_FERROUS_METALS', '_2_D_3_OTHER', '_2_H_OTHER'],
          exist: true,
        },
      }),
    );

    fixture = TestBed.createComponent(DeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the activity item name', () => {
    expect(page.header.textContent.trim()).toEqual('Are you sure you want to delete  ‘2.D.3 Other’?');
  });

  it('should show activities and submit form', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

    page.submitButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild(
        {
          pollutantRegisterActivities: {
            activities: ['_1_A_2_B_NON_FERROUS_METALS', '_2_H_OTHER'],
            exist: true,
          },
        },
        { pollutantRegisterActivities: [false] },
      ),
    );
  });
});
