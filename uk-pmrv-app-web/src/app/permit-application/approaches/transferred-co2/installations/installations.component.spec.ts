import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../testing';
import { SharedModule } from '../../../../shared/shared.module';
import { SharedPermitModule } from '../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../../testing/mock-permit-apply-action';
import { mockPostBuild, mockStateBuild } from '../../../testing/mock-state';
import { InstallationsComponent } from './installations.component';

describe('InstallationsComponent', () => {
  let page: Page;
  let component: InstallationsComponent;
  let fixture: ComponentFixture<InstallationsComponent>;

  let router: Router;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub(
    {},
    {},
    {
      taskKey: 'monitoringApproaches.TRANSFERRED_CO2.receivingTransferringInstallations',
      statusKey: 'TRANSFERRED_CO2_Installations',
    },
  );

  class Page extends BasePage<InstallationsComponent> {
    get addInstallationButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add an installation',
      );
    }

    get addAnotherInstallation() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another installation',
      );
    }

    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get installations() {
      return this.queryAll<HTMLDListElement>('dl');
    }

    get installationsTextContents() {
      return this.installations.map((installation) =>
        Array.from(installation.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [InstallationsComponent],
      providers: [
        { provide: TasksService, useValue: tasksService },
        { provide: ActivatedRoute, useValue: route },
      ],
    }).compileComponents();
  });

  const createComponent = () => {
    fixture = TestBed.createComponent(InstallationsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  it('should show add an installation button on empty installations', () => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(
      mockStateBuild(
        {
          ...mockPermitApplyPayload.permit,
          monitoringApproaches: {
            TRANSFERRED_CO2: {},
          },
        },
        { TRANSFERRED_CO2_Installations: [false] },
      ),
    );

    createComponent();

    expect(component).toBeTruthy();
    expect(page.addInstallationButton).toBeTruthy();
    expect(page.addAnotherInstallation).toBeFalsy();
    expect(page.submitButton).toBeFalsy();
  });

  it('should display existing installations', () => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(mockStateBuild({}, { TRANSFERRED_CO2_Installations: [false] }));

    createComponent();

    expect(component).toBeTruthy();
    expect(page.addInstallationButton).toBeFalsy();
    expect(page.addAnotherInstallation).toBeTruthy();
    expect(page.submitButton).toBeTruthy();

    expect(page.installationsTextContents).toEqual([
      ['Receiving installation', 'Change | Delete', 'code1', 'operator1', 'name1', 'source1'],
    ]);
  });

  it('should submit the installations, complete the task and navigate to summary', () => {
    const store = TestBed.inject(PermitApplicationStore);
    store.setState(mockStateBuild({}, { TRANSFERRED_CO2_Installations: [false] }));

    createComponent();

    const navigateSpy = jest.spyOn(router, 'navigate');
    tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

    page.submitButton.click();

    expect(navigateSpy).toHaveBeenCalledTimes(1);
    expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
      mockPostBuild(
        {},
        {
          ...mockPermitApplyPayload.permitSectionsCompleted,
          TRANSFERRED_CO2_Installations: [true],
        },
      ),
    );
  });
});
