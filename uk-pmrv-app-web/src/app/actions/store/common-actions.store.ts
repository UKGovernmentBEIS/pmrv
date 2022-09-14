import { Injectable } from '@angular/core';
import { Router } from '@angular/router';

import { first, map, Observable, of, tap } from 'rxjs';

import { RequestActionDTO, RequestActionsService } from 'pmrv-api';

import { Store } from '../../core/store';
import { catchNotFoundRequest, ErrorCode } from '../../error/not-found-error';
import { CommonActionsState, initialState } from './common-actions.state';

@Injectable({ providedIn: 'root' })
export class CommonActionsStore extends Store<CommonActionsState> {
  constructor(private readonly router: Router, private readonly requestActionsService: RequestActionsService) {
    super(initialState);
  }

  setState(state: CommonActionsState): void {
    super.setState(state);
  }

  get state$() {
    return this.asObservable();
  }

  get payload$(): Observable<any> {
    return this.state$.pipe(map((state) => state.action.payload));
  }

  get requestAction$(): Observable<RequestActionDTO> {
    return this.state$.pipe(map((state) => state.action));
  }

  get storeInitialized$(): Observable<boolean> {
    return this.state$.pipe(map((state) => !!state.storeInitialized));
  }

  get actionId() {
    return this.getValue().action.id;
  }

  private patchState(state: Partial<CommonActionsState>) {
    this.setState({ ...this.getState(), ...state });
  }

  requestedAction(actionId: number) {
    this.reset();
    this.requestActionsService
      .getRequestActionByIdUsingGET(actionId)
      .pipe(
        catchNotFoundRequest(ErrorCode.NOTFOUND1001, () => {
          this.router.navigate(['error', '404']);
          return of(null);
        }),
        tap((action) => this.patchState({ action: action, storeInitialized: true })),
        first(),
      )
      .subscribe();
  }
}
