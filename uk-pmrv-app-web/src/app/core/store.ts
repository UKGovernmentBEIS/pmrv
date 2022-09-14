import { BehaviorSubject, Observable, pluck } from 'rxjs';

export abstract class Store<T> extends BehaviorSubject<T> {
  protected constructor(private readonly initialState?: T) {
    super(initialState);
  }

  getState(): T {
    return this.getValue();
  }

  setState(state: T): void {
    this.next(state);
  }

  // eslint-disable-next-line @typescript-eslint/no-unused-vars
  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    return [];
  }

  select<K extends keyof T>(name: K): Observable<T[K]> {
    return this.pipe(pluck(name));
  }

  reset(): void {
    this.setState(this.initialState);
  }
}
