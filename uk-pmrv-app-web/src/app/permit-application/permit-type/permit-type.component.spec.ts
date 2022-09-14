import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { ActivatedRouteSnapshotStub, ActivatedRouteStub, BasePage, mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPostBuild, mockState } from '../testing/mock-state';
import { PermitTypeComponent } from './permit-type.component';

describe('PermitTypeComponent', () => {
  let page: Page;
  let store: PermitApplicationStore;
  let activatedRoute: ActivatedRouteSnapshotStub;
  let component: PermitTypeComponent;
  let fixture: ComponentFixture<PermitTypeComponent>;

  const tasksService = mockClass(TasksService);
  const route = new ActivatedRouteStub({ taskId: '237', index: '0' }, null, {
    permitTask: 'permitType',
  });

  class Page extends BasePage<PermitTypeComponent> {
    get typeRadios() {
      return this.queryAll<HTMLInputElement>('input[name$="type"]');
    }

    get errorSummary() {
      return this.query<HTMLDivElement>('.govuk-error-summary');
    }

    get submitButton() {
      return this.query<HTMLButtonElement>('button[type="submit"]');
    }
  }
  const createComponent = () => {
    fixture = TestBed.createComponent(PermitTypeComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    activatedRoute = new ActivatedRouteSnapshotStub({ taskId: '237', index: '0' });
    fixture.detectChanges();
    jest.clearAllMocks();
  };
  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [PermitApplicationModule, RouterTestingModule],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: ActivatedRouteSnapshotStub, useValue: activatedRoute },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();
  });

  describe('for new permit type', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState({
        ...mockState,
        permitType: undefined,
      });
    });
    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeTruthy();

      page.typeRadios[0].click();
      page.submitButton.click();
      fixture.detectChanges();

      expect(page.errorSummary).toBeFalsy();

      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledTimes(1);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
        ...mockPostBuild({}),
        requestTaskActionPayload: {
          ...mockPostBuild({}).requestTaskActionPayload,
          permitType: 'GHGE',
        },
      });
    });
  });
  describe('for existing  permit type', () => {
    beforeEach(() => {
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
    });

    beforeEach(createComponent);

    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should submit a valid form, update the store and navigate to summary', () => {
      expect(page.errorSummary).toBeFalsy();

      expect(page.typeRadios.length).toEqual(2);
      expect(page.typeRadios[0].checked).toBeTruthy();
      expect(page.typeRadios[1].checked).toBeFalsy();
    });
  });
});
