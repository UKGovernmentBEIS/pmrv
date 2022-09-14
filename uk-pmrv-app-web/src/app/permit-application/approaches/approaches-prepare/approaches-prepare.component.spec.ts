import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../../testing';
import { PermitApplicationModule } from '../../permit-application.module';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPostBuild, mockState } from '../../testing/mock-state';
import { ApproachesPrepareComponent } from './approaches-prepare.component';

describe('ApproachesPrepareComponent', () => {
  let page: Page;
  let router: Router;
  let activatedRoute: ActivatedRoute;
  let component: ApproachesPrepareComponent;
  let fixture: ComponentFixture<ApproachesPrepareComponent>;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<ApproachesPrepareComponent> {
    get confirmationButton() {
      return this.query<HTMLButtonElement>('button[type="button"]');
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  function createComponent() {
    fixture = TestBed.createComponent(ApproachesPrepareComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);
    fixture.detectChanges();
  }

  describe('should confirm the define monitoring approaches preparation statement', () => {
    beforeEach(() => {
      const store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create component', () => {
      expect(component).toBeTruthy();
    });

    it('should press the confirm button and navigate to summary page', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.confirmationButton.click();
      fixture.detectChanges();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild(undefined, { monitoringApproachesPrepare: [true] }),
      );
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], {
        relativeTo: activatedRoute,
        state: { notification: true },
      });
    });
  });
});
