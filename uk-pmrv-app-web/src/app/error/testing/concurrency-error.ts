import { ChangeDetectionStrategy, Component, NgModule } from '@angular/core';
import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { firstValueFrom } from 'rxjs';

import { SharedModule } from '../../shared/shared.module';
import { ConcurrencyError } from '../concurrency-error/concurrency-error';
import { ConcurrencyErrorService } from '../concurrency-error/concurrency-error.service';

export const expectConcurrentErrorToBe = async (error: ConcurrencyError) => {
  return expect(firstValueFrom(TestBed.inject(ConcurrencyErrorService).error$)).resolves.toEqual(error);
};

@Component({ selector: 'app-concurrency-error', template: '', changeDetection: ChangeDetectionStrategy.OnPush })
export class ConcurrencyErrorStubComponent {}

@NgModule({
  imports: [
    RouterTestingModule.withRoutes([{ path: 'error/concurrency', component: ConcurrencyErrorStubComponent }]),
    SharedModule,
  ],
  declarations: [ConcurrencyErrorStubComponent],
})
export class ConcurrencyTestingModule {}
