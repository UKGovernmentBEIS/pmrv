import { Pipe, PipeTransform } from '@angular/core';

import { Observable } from 'rxjs';

import { AerService } from '@tasks/aer/core/aer.service';

import { Aer } from 'pmrv-api';

@Pipe({ name: 'task' })
export class TaskPipe implements PipeTransform {
  constructor(private readonly aerService: AerService) {}

  transform<K extends keyof Aer>(key: K): Observable<Aer[K]> {
    return this.aerService.getTask(key);
  }
}
