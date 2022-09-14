import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, mockClass } from '../../../../../../testing';
import { SharedModule } from '../../../../../shared/shared.module';
import { SharedPermitModule } from '../../../../shared/shared-permit.module';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { mockState, mockStateBuild } from '../../../../testing/mock-state';
import { AnswersComponent } from './answers.component';

describe('AnswersComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let component: AnswersComponent;
  let fixture: ComponentFixture<AnswersComponent>;
  let router: Router;
  const route = new ActivatedRouteStub(
    {},
    {},
    { taskKey: 'monitoringApproaches.PFC.tier2EmissionFactor', statusKey: 'PFC_Tier2EmissionFactor' },
  );

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<AnswersComponent> {
    get summaryDefinitions() {
      return this.queryAll('dd').map((row) => row.textContent.trim());
    }
    get changeLinks() {
      return this.queryAll<HTMLLinkElement>('h2 > a');
    }
  }

  const createComponent = () => {
    fixture = TestBed.createComponent(AnswersComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = fixture.debugElement.injector.get(Router);
    jest.spyOn(router, 'navigate').mockImplementation();
    fixture.detectChanges();
    jest.clearAllMocks();
  };

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule, SharedPermitModule],
      declarations: [AnswersComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for tier2 emission factor with one step', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(
        mockStateBuild({
          monitoringApproaches: {
            PFC: {
              tier2EmissionFactor: {
                exist: false,
              },
            },
          },
        }),
      );
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tier2 emission factor answers', () => {
      expect(page.summaryDefinitions).toEqual(['No']);
    });
  });

  describe('for tier2 emission factor with all steps', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should display the tier2 emission factor answers', () => {
      expect(page.summaryDefinitions).toEqual([
        'Yes',
        'procedureDescription2',
        'procedureDocumentName2',
        'procedureReference2',
        'responsibleDepartmentOrRole2',
        'locationOfRecords2',
        'procedureDescription1',
        'procedureDocumentName1',
        'procedureReference1',
        'responsibleDepartmentOrRole1',
        'locationOfRecords1',
      ]);
    });

    it('should navigate to emission factor with changing', () => {
      page.changeLinks[0].click();
      expect(router.navigate).toHaveBeenCalledWith(['../'], { relativeTo: route, state: { changing: true } });
    });

    it('should navigate to determination installation with changing', () => {
      page.changeLinks[2].click();
      expect(router.navigate).toHaveBeenCalledWith(['../determination-installation'], {
        relativeTo: route,
        state: { changing: true },
      });
    });

    it('should navigate to schedule measurements with changing', () => {
      page.changeLinks[1].click();
      expect(router.navigate).toHaveBeenCalledWith(['../schedule-measurements'], {
        relativeTo: route,
        state: { changing: true },
      });
    });
  });
});
