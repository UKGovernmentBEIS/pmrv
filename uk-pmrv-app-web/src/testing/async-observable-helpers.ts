/**
 * Create async observable that emits-once and completes
 * after a JS engine turn
 */
import { defer, Observable } from 'rxjs';

export function asyncData<T>(data: T): Observable<T> {
  return defer(() => Promise.resolve(data));
}
