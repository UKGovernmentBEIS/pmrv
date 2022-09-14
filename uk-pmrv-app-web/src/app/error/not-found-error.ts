import { HttpErrorResponse, HttpStatusCode } from '@angular/common/http';

import { catchError, Observable, pipe, throwError, UnaryFunction } from 'rxjs';

export function catchElseRethrow<T, R, E = HttpErrorResponse>(
  predicate: UnaryFunction<E, boolean>,
  handler: UnaryFunction<E, Observable<R>>,
) {
  return pipe(
    catchError<T, Observable<R | never>>((res: E) => (predicate(res) ? handler(res) : throwError(() => res))),
  );
}

export function catchNotFoundRequest(
  code: ErrorCode | ErrorCode[],
  handler: (res: HttpErrorResponse) => Observable<any>,
) {
  return pipe(catchElseRethrow((res) => isNotFoundRequest(res, code), handler));
}

export function isNotFoundRequest(res: unknown, codes?: ErrorCode | ErrorCode[]): res is NotFoundRequest {
  return (
    res instanceof HttpErrorResponse &&
    res.status === HttpStatusCode.NotFound &&
    (codes === undefined ||
      (typeof codes === 'string' && codes === res.error.code) ||
      (Array.isArray(codes) && codes.includes(res.error.code)))
  );
}

export enum ErrorCode {
  NOTFOUND1001 = 'NOTFOUND1001',
}

export interface NotFoundRequest extends HttpErrorResponse {
  status: HttpStatusCode.NotFound;
  error: {
    code: ErrorCode;
    data: unknown;
  };
}
