import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { mockClass } from '@testing';

import { TasksService } from 'pmrv-api';

import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockReviewState } from '../../testing/mock-state';
import { ReviewGroupStatusPipe } from './review-group-status.pipe';
import { TaskStatusPipe } from './task-status.pipe';

describe('ReviewGroupStatusPipe', () => {
  let pipe: ReviewGroupStatusPipe;
  let store: PermitApplicationStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ReviewGroupStatusPipe, TaskStatusPipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });

    store = TestBed.inject(PermitApplicationStore);
  });

  beforeEach(() => (pipe = new ReviewGroupStatusPipe(store)));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should calculate the source streams status', async () => {
    store.setState(mockReviewState);

    await expect(firstValueFrom(pipe.transform('CALCULATION'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('CONFIDENTIALITY_STATEMENT'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('DEFINE_MONITORING_APPROACHES'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('FALLBACK'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('FUELS_AND_EQUIPMENT'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('INHERENT_CO2'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('INSTALLATION_DETAILS'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('MANAGEMENT_PROCEDURES'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('MEASUREMENT'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('MONITORING_METHODOLOGY_PLAN'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('N2O'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('PFC'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('ADDITIONAL_INFORMATION'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('TRANSFERRED_CO2'))).resolves.toEqual('undecided');
    await expect(firstValueFrom(pipe.transform('UNCERTAINTY_ANALYSIS'))).resolves.toEqual('undecided');
  });
});
