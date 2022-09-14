import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { BasePage, mockClass } from '@testing';
import { KeycloakService } from 'keycloak-angular';

import { TasksService } from 'pmrv-api';

import { SourceStreamsComponent } from './source-streams.component';

describe('SourceStreamsComponent', () => {
  let component: SourceStreamsComponent;
  let fixture: ComponentFixture<SourceStreamsComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: CommonTasksStore;
  let aerService: AerService;

  const tasksService = mockClass(TasksService);

  class Page extends BasePage<SourceStreamsComponent> {
    get submitButton() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Confirm and complete',
      );
    }

    get addSourceStreamBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add a source stream',
      );
    }

    get addAnotherSourceStreamBtn() {
      return Array.from(this.queryAll<HTMLButtonElement>('button[type="button"]')).find(
        (button) => button.textContent.trim() === 'Add another source stream',
      );
    }

    get sourceStreams() {
      return this.queryAll<HTMLDListElement>('tr');
    }

    get sourceStreamsTextContents() {
      return this.sourceStreams.map((sourceStream) =>
        Array.from(sourceStream.querySelectorAll('td')).map((td) => td.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, AerModule],
      providers: [KeycloakService, { provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SourceStreamsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
    aerService = TestBed.inject(AerService);
    fixture.detectChanges();
    jest.clearAllMocks();
  });

  describe('for adding new source stream', () => {
    it('should create', () => {
      expect(component).toBeTruthy();
    });

    it('should show add new stream button and hide complete button', () => {
      expect(page.submitButton).toBeFalsy();
      expect(page.addSourceStreamBtn).toBeFalsy();
      expect(page.addAnotherSourceStreamBtn).toBeFalsy();
      expect(page.sourceStreams.length).toEqual(0);

      const store = TestBed.inject(CommonTasksStore);
      store.setState({
        ...store.getState(),
        isEditable: true,
      });
      fixture.detectChanges();

      expect(page.addSourceStreamBtn).toBeTruthy();
    });
  });

  describe('for existing source streams', () => {
    beforeEach(() => {
      store = TestBed.inject(CommonTasksStore);
      store.setState(mockState);
      fixture.detectChanges();
    });

    it('should show add another stream button and complete button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addSourceStreamBtn).toBeFalsy();
      expect(page.addAnotherSourceStreamBtn).toBeTruthy();
      expect(page.sourceStreams.length).toEqual(3);
    });

    it('should display the source streams', () => {
      expect(page.sourceStreamsTextContents).toEqual([
        [],
        ['the reference', 'Anthracite', 'Ammonia: Fuel as process input', 'Change', 'Delete'],
        ['the other reference', 'Biodiesels', 'Cement clinker: CKD', 'Change', 'Delete'],
      ]);
    });

    it('should submit the source streams, complete the task and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      const postTaskSaveSpy = jest.spyOn(aerService, 'postTaskSave');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.submitButton.click();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['..'], { relativeTo: route });
      expect(postTaskSaveSpy).toHaveBeenCalledWith({}, {}, true, 'sourceStreams');
    });
  });
});
