import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { PermitIssuanceReviewDecision, TasksService } from 'pmrv-api';

import { mockClass } from '../../../../testing';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { ReviewGroupPipe } from './review-group.pipe';

describe('ReviewGroupPipe', () => {
  let pipe: ReviewGroupPipe;
  let store: PermitApplicationStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [ReviewGroupPipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });

    store = TestBed.inject(PermitApplicationStore);
  });

  beforeEach(() => (pipe = new ReviewGroupPipe(store)));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should return an observable of the review group decision', async () => {
    const installationDetails = { type: 'ACCEPTED', notes: 'notes' } as PermitIssuanceReviewDecision;

    store.setState({ ...store.getState(), reviewGroupDecisions: { INSTALLATION_DETAILS: installationDetails } });

    await expect(firstValueFrom(pipe.transform('INSTALLATION_DETAILS'))).resolves.toEqual(installationDetails);
  });

  it('should return null for not existence', async () => {
    store.setState({ ...store.getState(), reviewGroupDecisions: {} });

    await expect(firstValueFrom(pipe.transform('INSTALLATION_DETAILS'))).resolves.toEqual(null);
  });
});
