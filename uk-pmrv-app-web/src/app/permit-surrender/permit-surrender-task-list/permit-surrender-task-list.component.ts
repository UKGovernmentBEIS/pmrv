import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { map, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';
import { hasRequestTaskAllowedActions } from "@shared/components/related-actions/request-task-allowed-actions.map";

import { PermitSurrenderStore } from '../store/permit-surrender.store';

@Component({
  selector: 'app-permit-surrender-task-list',
  templateUrl: './permit-surrender-task-list.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class PermitSurrenderTaskListComponent implements OnInit {
  navigationState = { returnUrl: this.router.url };

  hasRelatedActions$ = this.store.pipe(
    map((state) => state.assignable || hasRequestTaskAllowedActions(state.allowedRequestTaskActions))
  );
  taskId$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('taskId'))));
  
  constructor(
    readonly store: PermitSurrenderStore,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly destroy$: DestroySubject,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit() {
    this.store.isEditable$.pipe(takeUntil(this.destroy$)).subscribe((editable) => {
      if (!editable) {
        this.backLinkService.show();
      }
    });
  }

  submit(): void {
    this.router.navigate(['summary'], { relativeTo: this.route });
  }
}
