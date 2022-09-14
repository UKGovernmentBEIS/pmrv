import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../../../../testing/mock-state';
import { DeleteComponent } from './delete.component';

describe('DeleteComponent', () => {
  let page: Page;
  let router: Router;
  let component: DeleteComponent;
  let store: PermitApplicationStore;
  let fixture: ComponentFixture<DeleteComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    { index: 0 },
    {},
    {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2.receivingTransferringInstallations',
      statusKey: 'TRANSFERRED_CO2_Installations',
    },
  );

  class Page extends BasePage<DeleteComponent> {
    get deleteButton() {
      return this.queryAll<HTMLButtonElement>('button')[0];
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [DeleteComponent],
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    store = TestBed.inject(PermitApplicationStore);
    store.setState(mockState);
    fixture = TestBed.createComponent(DeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should delete the installation', () => {
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.deleteButton.click();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild(
        {
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            ...mockPermitApplyPayload.permit.monitoringApproaches,
            TRANSFERRED_CO2: {
              ...mockPermitApplyPayload.permit.monitoringApproaches.TRANSFERRED_CO2,
              receivingTransferringInstallations: [],
            },
          },
        },
        {
          ...mockPermitApplyPayload.permitSectionsCompleted,
          TRANSFERRED_CO2_Installations: [false],
        },
      ),
    );

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(store.permit.monitoringApproaches.TRANSFERRED_CO2['receivingTransferringInstallations']).toEqual([]);
  });
});
