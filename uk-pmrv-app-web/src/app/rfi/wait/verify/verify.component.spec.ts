import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { ActivatedRouteStub, BasePage, MockType } from '../../../../testing';
import { SharedModule } from '../../../shared/shared.module';
import { VerifyComponent } from './verify.component';

describe('VerifyComponent', () => {
  let page: Page;
  let component: VerifyComponent;
  let fixture: ComponentFixture<VerifyComponent>;
  let route: ActivatedRouteStub;
  let router: Router;
  let tasksService: MockType<TasksService>;

  class Page extends BasePage<VerifyComponent> {
    get submitButton() {
      return this.query<HTMLButtonElement>('button');
    }
  }

  beforeEach(async () => {
    tasksService = {
      processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of({})),
    };
    route = new ActivatedRouteStub({ taskId: '237' });

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [VerifyComponent],
      providers: [
        { provide: ActivatedRoute, useValue: route },
        { provide: TasksService, useValue: tasksService },
      ],
    }).compileComponents();

    await TestBed.configureTestingModule({
      declarations: [VerifyComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(VerifyComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the verify cancel and navigate to confirmation', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');

    page.submitButton.click();
    fixture.detectChanges();

    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalled();
    expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith({
      requestTaskActionType: 'RFI_CANCEL',
      requestTaskId: 237,
      requestTaskActionPayload: {
        payloadType: 'EMPTY_PAYLOAD',
      },
    });
    expect(navigateSpy).toHaveBeenCalledWith(['../cancel-confirmation'], { relativeTo: route });
  });
});
