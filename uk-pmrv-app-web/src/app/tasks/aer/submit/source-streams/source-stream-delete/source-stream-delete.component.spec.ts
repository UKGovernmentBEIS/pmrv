import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { ActivatedRouteStub, BasePage, MockType } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { mockAerApplyPayload, mockState } from '../../testing/mock-permit-apply-action';
import { SourceStreamDeleteComponent } from './source-stream-delete.component';

describe('SourceStreamDeleteComponent', () => {
  let page: Page;
  let router: Router;
  let store: CommonTasksStore;
  let aerService: AerService;
  let component: SourceStreamDeleteComponent;
  let fixture: ComponentFixture<SourceStreamDeleteComponent>;

  const route = new ActivatedRouteStub({ streamId: mockAerApplyPayload.aer.sourceStreams[0].id });
  const tasksService: MockType<TasksService> = {
    processRequestTaskActionUsingPOST: jest.fn().mockReturnValue(of(null)),
  };

  class Page extends BasePage<SourceStreamDeleteComponent> {
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
    fixture = TestBed.createComponent(SourceStreamDeleteComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    aerService = TestBed.inject(AerService);
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the source stream name', () => {
    expect(page.header.textContent).toContain(mockAerApplyPayload.aer.sourceStreams[0].reference);
  });

  it('should delete the source stream', () => {
    const navigateSpy = jest.spyOn(router, 'navigate');
    const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
    const expectedSourceStreams = mockAerApplyPayload.aer.sourceStreams.filter(
      (stream) => stream.id !== mockAerApplyPayload.aer.sourceStreams[0].id,
    );

    page.submitButton.click();

    expect(postTaskSaveSpy).toHaveBeenCalledTimes(1);
    expect(postTaskSaveSpy).toHaveBeenCalledWith(
      { sourceStreams: expectedSourceStreams },
      undefined,
      false,
      'sourceStreams',
    );
    expect(navigateSpy).toHaveBeenCalledWith(['../..'], { relativeTo: route });
  });
});
