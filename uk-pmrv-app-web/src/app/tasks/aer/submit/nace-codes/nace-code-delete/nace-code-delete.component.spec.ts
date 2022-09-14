import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { NaceCodeDeleteComponent } from '@tasks/aer/submit/nace-codes/nace-code-delete/nace-code-delete.component';
import { mockAerApplyPayload, mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

describe('NaceCodeDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let component: NaceCodeDeleteComponent;
  let fixture: ComponentFixture<NaceCodeDeleteComponent>;

  const tasksService = mockClass(TasksService);
  const activatedRoute = new ActivatedRouteStub(
    {
      naceCode: mockAerApplyPayload.aer.naceCodes.codes[0],
    },
    {},
    { permitTask: 'naceCodes' },
  );

  class Page extends BasePage<NaceCodeDeleteComponent> {
    get header() {
      return this.query<HTMLHeadingElement>('h1');
    }

    get body() {
      return this.query<HTMLElement>('.govuk-body');
    }

    get deleteButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [
        KeycloakService,
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(CommonTasksStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(NaceCodeDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the name of the nace code to be deleted', () => {
    expect(page.header.textContent.trim()).toContain('1012 Processing and preserving of poultry meat');
    expect(page.body.textContent.trim()).toContain('Any reference to this item will be removed from your application.');
  });

  it('should delete the nace code', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    const storedCodes = mockAerApplyPayload.aer.naceCodes.codes;
    const storedStatusesCompleted = mockAerApplyPayload.aerSectionsCompleted;
    const deletedCode = activatedRoute.snapshot.paramMap.get('naceCode');

    const remainingCodes = storedCodes.filter((key) => key !== deletedCode);

    const remainingStatusesCompleted = Object.keys(storedStatusesCompleted)
      .filter((key) => key !== deletedCode)
      .reduce((res, key) => ({ ...res, [key]: storedStatusesCompleted[key] }), {});

    page.deleteButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'AER_SAVE_APPLICATION',
      requestTaskId: 1,
      requestTaskActionPayload: {
        aerSectionsCompleted: {
          ...remainingStatusesCompleted,
          naceCodes: [false],
        },
        payloadType: 'AER_SAVE_APPLICATION_PAYLOAD',
        aer: {
          ...(mockState.requestTaskItem.requestTask.payload as any).aer,
          naceCodes: { codes: remainingCodes },
        },
      },
    });
    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect((store.getState().requestTaskItem.requestTask.payload as any).aer.naceCodes.codes).toEqual(remainingCodes);
    expect((store.getState().requestTaskItem.requestTask.payload as any).aerSectionsCompleted).toEqual({
      ...remainingStatusesCompleted,
      naceCodes: [false],
    });
  });
});
