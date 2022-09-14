import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { of } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { BasePage, mockClass } from '../../../testing';
import { PermitApplicationModule } from '../permit-application.module';
import { PermitApplicationStore } from '../store/permit-application.store';
import { mockPermitApplyPayload } from '../testing/mock-permit-apply-action';
import { mockPostBuild, mockState } from '../testing/mock-state';
import { SourceStreamsComponent } from './source-streams.component';

describe('SourceStreamsComponent', () => {
  let component: SourceStreamsComponent;
  let fixture: ComponentFixture<SourceStreamsComponent>;
  let page: Page;
  let router: Router;
  let route: ActivatedRoute;
  let store: PermitApplicationStore;

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
      return this.queryAll<HTMLDListElement>('dl');
    }

    get sourceStreamsTextContents() {
      return this.sourceStreams.map((sourceStream) =>
        Array.from(sourceStream.querySelectorAll('dd')).map((dd) => dd.textContent.trim()),
      );
    }
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, PermitApplicationModule],
      providers: [{ provide: TasksService, useValue: tasksService }],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(SourceStreamsComponent);
    component = fixture.componentInstance;
    page = new Page(fixture);
    router = TestBed.inject(Router);
    route = TestBed.inject(ActivatedRoute);
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

      const store = TestBed.inject(PermitApplicationStore);
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
      store = TestBed.inject(PermitApplicationStore);
      store.setState(mockState);
      fixture.detectChanges();
    });

    it('should show add another stream button and complete button', () => {
      expect(page.submitButton).toBeTruthy();
      expect(page.addSourceStreamBtn).toBeFalsy();
      expect(page.addAnotherSourceStreamBtn).toBeTruthy();
      expect(page.sourceStreams.length).toEqual(2);
    });

    it('should display the source streams', () => {
      expect(page.sourceStreamsTextContents).toEqual([
        ['13123124 White Spirit & SBP', 'Change | Delete', 'Refineries: Hydrogen production'],
        ['33334 Lignite', 'Change | Delete', 'Refineries: Catalytic cracker regeneration'],
      ]);
    });

    it('should submit the source streams, complete the task and navigate to summary', () => {
      const navigateSpy = jest.spyOn(router, 'navigate');
      tasksService.processRequestTaskActionUsingPOST.mockReturnValueOnce(of({}));

      page.submitButton.click();

      expect(navigateSpy).toHaveBeenCalledTimes(1);
      expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: route, state: { notification: true } });
      expect(store.payload.permitSectionsCompleted.sourceStreams).toEqual([true]);
      expect(store.permit.sourceStreams).toEqual(mockPermitApplyPayload.permit.sourceStreams);
      expect(tasksService.processRequestTaskActionUsingPOST).toHaveBeenCalledWith(
        mockPostBuild({}, { sourceStreams: [true] }),
      );
    });
  });
});
