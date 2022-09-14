import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../../shared.module';
import { tasks } from '../testing/tasks';
import { TaskSectionComponent } from './task-section.component';

describe('TaskSectionComponent', () => {
  let component: TaskSectionComponent;
  let fixture: ComponentFixture<TaskSectionComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskSectionComponent);
    component = fixture.componentInstance;
    component.title = 'Test title';
    component.tasks = tasks;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should the title', () => {
    const element: HTMLElement = fixture.nativeElement;
    const header = element.querySelector<HTMLHeadingElement>('h2');

    expect(header.textContent).toEqual('Test title');
  });

  it('should render the tasks items', () => {
    const element: HTMLElement = fixture.nativeElement;
    const items = element.querySelectorAll<HTMLUListElement>('li');

    expect(items.length).toEqual(6);
  });
});
