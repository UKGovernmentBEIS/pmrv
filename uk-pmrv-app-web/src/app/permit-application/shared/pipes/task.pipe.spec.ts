import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { SourceStream, TasksService } from 'pmrv-api';

import { mockClass } from '../../../../testing';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { TaskPipe } from './task.pipe';

describe('TaskPipe', () => {
  let pipe: TaskPipe;
  let store: PermitApplicationStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [TaskPipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });

    store = TestBed.inject(PermitApplicationStore);
  });

  beforeEach(() => (pipe = new TaskPipe(store)));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return an observable of the task', async () => {
    const sourceStreams: SourceStream[] = [
      {
        id: 'abc',
        description: 'ANTHRACITE',
        type: 'AMMONIA_FUEL_AS_PROCESS_INPUT',
        reference: 'test',
      },
    ];

    store.setState({ ...store.getState(), permit: { ...store.permit, sourceStreams } });

    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual(sourceStreams);
  });
});
