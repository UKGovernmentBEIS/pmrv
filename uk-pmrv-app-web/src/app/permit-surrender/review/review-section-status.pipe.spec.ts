import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { mockClass } from '../../../testing';
import { PermitSurrenderStore } from '../store/permit-surrender.store';
import { mockTaskCompletedPayload } from '../testing/mock-state';
import { ReviewSectionStatusPipe } from './review-section-status.pipe';

describe('ReviewSectionStatusPipe', () => {
  let pipe: ReviewSectionStatusPipe;
  let store: PermitSurrenderStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ReviewSectionStatusPipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });

    store = TestBed.inject(PermitSurrenderStore);
  });

  beforeEach(() => (pipe = new ReviewSectionStatusPipe(store)));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should resolve decision status', async () => {
    store.setState({
      ...store.getState(),
      reviewDecision: undefined,
    });
    await expect(firstValueFrom(pipe.transform('DECISION'))).resolves.toEqual('undecided');

    store.setState({
      ...store.getState(),
      reviewDecision: {
        type: 'ACCEPTED',
        notes: 'notes',
      },
    });
    await expect(firstValueFrom(pipe.transform('DECISION'))).resolves.toEqual('accepted');

    store.setState({
      ...store.getState(),
      reviewDecision: {
        type: 'REJECTED',
        notes: 'notes',
      },
    });
    await expect(firstValueFrom(pipe.transform('DECISION'))).resolves.toEqual('rejected');
  });

  it('should resolve determination status', async () => {
    await expect(firstValueFrom(pipe.transform('DETERMINATION'))).resolves.toEqual('undecided');

    store.setState({
      ...store.getState(),
      reviewDeterminationCompleted: false,
    });

    await expect(firstValueFrom(pipe.transform('DETERMINATION'))).resolves.toEqual('undecided');

    store.setState({
      ...store.getState(),
      reviewDeterminationCompleted: mockTaskCompletedPayload.reviewDeterminationCompleted,
      reviewDetermination: mockTaskCompletedPayload.reviewDetermination,
    });

    await expect(firstValueFrom(pipe.transform('DETERMINATION'))).resolves.toEqual('granted');
  });
});
