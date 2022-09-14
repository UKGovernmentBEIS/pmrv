import { ComponentFixture, TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { SharedModule } from '../../shared.module';
import { tasks } from '../testing/tasks';
import { TaskItemListComponent } from './task-item-list.component';

describe('TaskItemListComponent', () => {
  let component: TaskItemListComponent;
  let fixture: ComponentFixture<TaskItemListComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(TaskItemListComponent);
    component = fixture.componentInstance;
    component.taskItems = tasks;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should render the tasks items', () => {
    const element: HTMLElement = fixture.nativeElement;
    const items = element.querySelectorAll<HTMLUListElement>('li');

    expect(items.length).toEqual(6);
  });
});
