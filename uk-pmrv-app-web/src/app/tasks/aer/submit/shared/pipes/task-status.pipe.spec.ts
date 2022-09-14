import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { AerModule } from '@tasks/aer/aer.module';
import { AerService } from '@tasks/aer/core/aer.service';
import { mockState } from '@tasks/aer/submit/testing/mock-permit-apply-action';
import { CommonTasksStore } from '@tasks/store/common-tasks.store';
import { KeycloakService } from 'keycloak-angular';

import { TaskStatusPipe } from './task-status.pipe';

describe('TaskStatusPipe', () => {
  let pipe: TaskStatusPipe;
  let store: CommonTasksStore;
  let aerService: AerService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [AerModule, RouterTestingModule],
      providers: [KeycloakService],
    });
    store = TestBed.inject(CommonTasksStore);
    aerService = TestBed.inject(AerService);
    pipe = new TaskStatusPipe(aerService);
  });

  it('should be created', () => {
    expect(pipe).toBeTruthy();
  });

  it('should resolve statuses', async () => {
    await expect(firstValueFrom(pipe.transform('abbreviations'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('additionalDocuments'))).resolves.toEqual('not started');
    await expect(firstValueFrom(pipe.transform('confidentialityStatement'))).resolves.toEqual('not started');

    store.setState(mockState);

    await expect(firstValueFrom(pipe.transform('abbreviations'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('additionalDocuments'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('confidentialityStatement'))).resolves.toEqual('complete');
    await expect(firstValueFrom(pipe.transform('pollutantRegisterActivities'))).resolves.toEqual('complete');

    await expect(firstValueFrom(pipe.transform('sourceStreams'))).resolves.toEqual('in progress');
  });
});
