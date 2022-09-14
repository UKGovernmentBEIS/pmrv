import { Inject, Injectable } from '@angular/core';

import {
  distinctUntilChanged,
  filter,
  interval,
  map,
  Observable,
  pluck,
  skipWhile,
  Subscription,
  switchMapTo,
} from 'rxjs';

import { LOCAL_STORAGE } from '../../core/services/local-storage';
import { Store } from '../../core/store';
import { initialState, RfiState } from './rfi.state';

@Injectable({ providedIn: 'root' })
export class RfiStore extends Store<RfiState> {
  private storage$ = interval(2000).pipe(
    switchMapTo(this),
    pluck('requestTaskId'),
    skipWhile((requestTaskId) => !requestTaskId),
    map((requestTaskId) => this.localStorage.getItem(`rfi/${requestTaskId}`)),
    distinctUntilChanged(),
    filter((state) => state && state !== JSON.stringify(this.getState())),
    map((state) => JSON.parse(state)),
  );
  private localStorageSubscription: Subscription;

  constructor(@Inject(LOCAL_STORAGE) private readonly localStorage: Storage) {
    super(initialState);
  }

  listenToStorage(): void {
    this.localStorageSubscription = this.storage$.subscribe((state) => this.setState(state));
  }

  stopListeningToStorage(): void {
    this.localStorageSubscription.unsubscribe();
  }

  setState(state: RfiState): void {
    if (state.requestTaskId) {
      this.localStorage.setItem(`rfi/${state.requestTaskId}`, JSON.stringify(state));
    }
    super.setState(state);
  }

  get isEditable$(): Observable<boolean> {
    return this.pipe(pluck('isEditable'));
  }

  getDownloadUrlFiles(files: string[]): { downloadUrl: string; fileName: string }[] {
    const url = this.createBaseFileDownloadUrl();

    return (
      files?.map((id) => ({
        downloadUrl: url + `${id}`,
        fileName: this.getState().rfiAttachments[id],
      })) ?? []
    );
  }

  createBaseFileDownloadUrl() {
    return !this.getState().actionId
      ? `/rfi/${this.getState().requestTaskId}/file-download/`
      : `/rfi/action/${this.getState().actionId}/file-download/attachment/`;
  }
}
