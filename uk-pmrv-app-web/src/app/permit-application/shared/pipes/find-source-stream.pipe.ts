import { Pipe, PipeTransform } from '@angular/core';

import { map, Observable } from 'rxjs';

import { SourceStream } from 'pmrv-api';

import { PermitApplicationStore } from '../../store/permit-application.store';

@Pipe({ name: 'findSourceStream' })
export class FindSourceStreamPipe implements PipeTransform {
  constructor(private readonly store: PermitApplicationStore) {}

  transform(sourceStream?: string): Observable<SourceStream> {
    return this.store
      .getTask('sourceStreams')
      .pipe(map((sourceStreams) => sourceStreams.find((stream) => stream.id === sourceStream)));
  }
}
