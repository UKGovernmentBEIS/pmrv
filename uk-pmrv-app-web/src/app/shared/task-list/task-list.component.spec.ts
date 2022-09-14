import { ComponentFixture, TestBed } from '@angular/core/testing';
import { By } from '@angular/platform-browser';
import { RouterTestingModule } from '@angular/router/testing';

import { ButtonDirective } from 'govuk-components';

import { SharedModule } from '../shared.module';
import { TaskListComponent } from './task-list.component';
import { sections } from './testing/sections';

describe('TaskListComponent', () => {
  let component: TaskListComponent;
  let fixture: ComponentFixture<TaskListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskListComponent);
    component = fixture.componentInstance;
    component.sections = sections;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should emit event on submit', () => {
    const submitSpy = jest.spyOn(component.submitApplication, 'emit');
    fixture.debugElement.query(By.directive(ButtonDirective)).nativeElement.click();

    expect(submitSpy).toHaveBeenCalled();
  });

  it('should emit cancel event on cancel', () => {
    const cancelSpy = jest.spyOn(component.cancelApplication, 'emit');
    const element: HTMLElement = fixture.nativeElement;
    const links = element.querySelectorAll('a');

    links.item(links.length - 1).click();

    expect(cancelSpy).toHaveBeenCalled();
  });

  it('should render the sections and tasks', () => {
    const element: HTMLElement = fixture.nativeElement;
    const taskItems = element.querySelectorAll<HTMLLIElement>('.app-task-list__item');

    expect(taskItems).toBeTruthy();
    expect(taskItems.length).toEqual(8);
  });
});
