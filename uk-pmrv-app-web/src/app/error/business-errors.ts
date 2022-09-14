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

export function catchBadRequest(code: ErrorCode | ErrorCode[], handler: (res: HttpErrorResponse) => Observable<any>) {
  return pipe(catchElseRethrow((res) => isBadRequest(res, code), handler));
}

export function catchTaskReassignedBadRequest(handler: (res: HttpErrorResponse) => Observable<any>) {
  return catchBadRequest(ErrorCode.REQUEST_TASK_ACTION1001, handler);
}

export function isBadRequest(res: unknown, codes?: ErrorCode | ErrorCode[]): res is BadRequest {
  return (
    res instanceof HttpErrorResponse &&
    res.status === HttpStatusCode.BadRequest &&
    (codes === undefined ||
      (typeof codes === 'string' && codes === res.error.code) ||
      (Array.isArray(codes) && codes.includes(res.error.code)))
  );
}

export enum ErrorCode {
  ACCOUNT1004 = 'ACCOUNT1004',
  ACCOUNT1005 = 'ACCOUNT1005',
  ACCOUNT1006 = 'ACCOUNT1006',
  ACCOUNT1007 = 'ACCOUNT1007',
  ACCOUNT_CONTACT1001 = 'ACCOUNT_CONTACT1001',
  ACCOUNT_CONTACT1002 = 'ACCOUNT_CONTACT1002',
  ACCOUNT_CONTACT1003 = 'ACCOUNT_CONTACT1003',
  AUTHORITY1000 = 'AUTHORITY1000',
  AUTHORITY1001 = 'AUTHORITY1001',
  AUTHORITY1003 = 'AUTHORITY1003',
  AUTHORITY1004 = 'AUTHORITY1004',
  AUTHORITY1005 = 'AUTHORITY1005',
  AUTHORITY1006 = 'AUTHORITY1006',
  AUTHORITY1007 = 'AUTHORITY1007',
  EMAIL1001 = 'EMAIL1001',
  EXTCONTACT1000 = 'EXTCONTACT1000',
  EXTCONTACT1001 = 'EXTCONTACT1001',
  EXTCONTACT1002 = 'EXTCONTACT1002',
  EXTCONTACT1003 = 'EXTCONTACT1003',
  OTP1001 = 'OTP1001',
  REQUEST_TASK_ACTION1001 = 'REQUEST_TASK_ACTION1001',
  TOKEN1001 = 'TOKEN1001',
  USER1001 = 'USER1001',
  USER1004 = 'USER1004',
  USER1005 = 'USER1005',
  VERBODY1001 = 'VERBODY1001',
  VERBODY1002 = 'VERBODY1002',
  NOTIF1002 = 'NOTIF1002',
}

export interface BadRequest extends HttpErrorResponse {
  status: HttpStatusCode.BadRequest;
  error: {
    code: ErrorCode;
    data: unknown;
  };
}
