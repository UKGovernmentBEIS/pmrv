import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { mockClass } from '../../../testing';
import { PermitSurrenderStore } from '../store/permit-surrender.store';
import { mockTaskState } from '../testing/mock-state';
import { TaskStatusPipe } from './task-status.pipe';

describe('TaskStatusPipe', () => {
  let pipe: TaskStatusPipe;
  let store: PermitSurrenderStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [TaskStatusPipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });

    store = TestBed.inject(PermitSurrenderStore);
  });

  beforeEach(() => (pipe = new TaskStatusPipe(store)));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should resolve apply status', async () => {
    await expect(firstValueFrom(pipe.transform('SURRENDER_APPLY'))).resolves.toEqual('not started');

    store.setState(mockTaskState);
    await expect(firstValueFrom(pipe.transform('SURRENDER_APPLY'))).resolves.toEqual('complete');

    store.setState({
      ...store.getState(),
      sectionsCompleted: { ...store.getState().sectionsCompleted, SURRENDER_APPLY: false },
    });
    await expect(firstValueFrom(pipe.transform('SURRENDER_APPLY'))).resolves.toEqual('in progress');

    store.setState({
      ...store.getState(),
      permitSurrender: undefined,
    });
    await expect(firstValueFrom(pipe.transform('SURRENDER_APPLY'))).resolves.toEqual('not started');
  });

  it('should resolve submit status', async () => {
    await expect(firstValueFrom(pipe.transform('SURRENDER_SUBMIT'))).resolves.toEqual('cannot start yet');

    store.setState(mockTaskState);
    await expect(firstValueFrom(pipe.transform('SURRENDER_SUBMIT'))).resolves.toEqual('not started');
  });
});
