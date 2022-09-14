import { ComponentFixture, fakeAsync, TestBed, tick } from '@angular/core/testing';
import { ActivatedRoute, Router } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../../shared/shared.module';
import { InstallationAccountApplicationStore } from '../store/installation-account-application.store';
import { TaskListComponent } from './task-list.component';

describe('TaskListComponent', () => {
  let component: TaskListComponent;
  let fixture: ComponentFixture<TaskListComponent>;
  let router: Router;
  let activatedRoute: ActivatedRoute;

  function getSubmitButton(): HTMLButtonElement {
    const element: HTMLElement = fixture.nativeElement;

    return element.querySelector('button');
  }

  function completeTasks(): void {
    const store = TestBed.inject(InstallationAccountApplicationStore);

    store.setState({ tasks: store.getState().tasks.map((task) => ({ ...task, status: 'complete' })) });
  }

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      declarations: [TaskListComponent],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskListComponent);
    component = fixture.componentInstance;
    router = TestBed.inject(Router);
    activatedRoute = TestBed.inject(ActivatedRoute);

    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should disable the submit while sections are not completed', fakeAsync(() => {
    expect(getSubmitButton().disabled).toBeTruthy();

    completeTasks();
    tick();
    fixture.detectChanges();

    expect(getSubmitButton().disabled).toBeFalsy();
  }));

  it('should move to summary on submit', fakeAsync(() => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    completeTasks();
    tick();
    fixture.detectChanges();

    getSubmitButton().click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['summary'], { relativeTo: activatedRoute });
  }));

  it('should move to cancellation on cancel', fakeAsync(() => {
    const navigateSpy = jest.spyOn(router, 'navigate').mockResolvedValue(true);

    completeTasks();
    tick();
    fixture.detectChanges();

    const element: HTMLElement = fixture.nativeElement;
    const links = element.querySelectorAll('a');

    links.item(links.length - 1).click();
    fixture.detectChanges();

    expect(navigateSpy).toHaveBeenCalledWith(['cancel'], { relativeTo: activatedRoute });
  }));
});
