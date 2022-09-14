import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { mockClass } from '../../../../testing';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockPermitApplyPayload } from '../../testing/mock-permit-apply-action';
import { mockState } from '../../testing/mock-state';
import { FindSourceStreamPipe } from './find-source-stream.pipe';

describe('FindSourceStreamPipe', () => {
  let pipe: FindSourceStreamPipe;
  let store: PermitApplicationStore;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [FindSourceStreamPipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });

    store = TestBed.inject(PermitApplicationStore);
  });

  beforeEach(() => (pipe = new FindSourceStreamPipe(store)));

  it('should create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('should find source stream', async () => {
    store.setState(mockState);
    await expect(firstValueFrom(pipe.transform(mockPermitApplyPayload.permit.sourceStreams[0].id))).resolves.toEqual(
      mockPermitApplyPayload.permit.sourceStreams[0],
    );
  });

  it('should not find source stream', async () => {
    store.setState(mockState);
    await expect(firstValueFrom(pipe.transform('test'))).resolves.toBeFalsy();
    await expect(firstValueFrom(pipe.transform())).resolves.toBeFalsy();
  });
});
