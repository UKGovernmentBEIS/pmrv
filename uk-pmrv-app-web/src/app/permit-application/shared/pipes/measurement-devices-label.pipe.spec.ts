import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { TasksService } from 'pmrv-api';

import { mockClass } from '../../../../testing';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { mockState } from '../../testing/mock-state';
import { MeasurementDevicesLabelPipe } from './measurement-devices-label.pipe';

describe('MeasurementDevicesLabelPipe', () => {
  let store: PermitApplicationStore;
  let pipe: MeasurementDevicesLabelPipe;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      declarations: [MeasurementDevicesLabelPipe],
      providers: [{ provide: TasksService, useValue: mockClass(TasksService) }],
    });
    store = TestBed.inject(PermitApplicationStore);
  });

  beforeEach(() => (pipe = new MeasurementDevicesLabelPipe(store)));

  it('create an instance', () => {
    expect(pipe).toBeTruthy();
  });

  it('transform measurement device label', async () => {
    store.setState(mockState);
    await expect(firstValueFrom(pipe.transform('16236817394240.1574963093314700'))).resolves.toEqual(
      'ref1, Ultrasonic meter , Specified uncertainty ±2',
    );
  });
});
