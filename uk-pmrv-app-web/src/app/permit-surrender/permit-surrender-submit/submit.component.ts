import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BehaviorSubject, first, map, switchMap, takeUntil } from 'rxjs';

import { PendingRequestService } from '../../core/guards/pending-request.service';
import { DestroySubject } from '../../core/services/destroy-subject.service';
import { BreadcrumbService } from '../../shared/breadcrumbs/breadcrumb.service';
import { PermitSurrenderStore } from '../store/permit-surrender.store';

@Component({
  selector: 'app-submit',
  templateUrl: './submit.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BreadcrumbService, DestroySubject],
})
export class SubmitComponent implements OnInit {
  allowSubmit$ = this.store.pipe(map((state) => state.sectionsCompleted?.SURRENDER_APPLY));
  isPermitSurrenderSubmitted$ = new BehaviorSubject(false);
  competentAuthority$ = this.store.select('competentAuthority');

  constructor(
    readonly store: PermitSurrenderStore,
    readonly pendingRequest: PendingRequestService,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly route: ActivatedRoute,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe((paramMap) => {
      const link = ['/permit-surrender', paramMap.get('taskId')];
      this.breadcrumbService.show([{ text: 'Permit surrender task list', link }]);
    });
  }

  onSubmit(): void {
    this.store
      .pipe(
        first(),
        switchMap(() => this.store.postSubmitPermitSurrender()),
        this.pendingRequest.trackRequest(),
      )
      .subscribe(() => {
        this.isPermitSurrenderSubmitted$.next(true);
        this.breadcrumbService.clear();
      });
  }
}
