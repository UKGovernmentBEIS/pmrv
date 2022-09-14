import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, pluck, switchMap, takeUntil } from 'rxjs';

import { DestroySubject } from '@core/services/destroy-subject.service';
import { BackLinkService } from '@shared/back-link/back-link.service';

import { ItemDTO, RequestItemsService } from 'pmrv-api';

import { waitForRfiRdeTypes } from '../../core/rfi';
import { RfiStore } from '../../store/rfi.store';

@Component({
  selector: 'app-not-allowed',
  template: `
    <app-page-heading>You can only have one active request at any given time</app-page-heading>
    <div class="govuk-button-group">
      <button (click)="onClick()" govukSecondaryButton type="button">View the active request</button>
    </div>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DestroySubject],
})
export class NotAllowedComponent implements OnInit {
  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly rfiStore: RfiStore,
    private readonly requestItemsService: RequestItemsService,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe(() => this.backLinkService.show());
  }

  onClick(): void {
    this.rfiStore
      .pipe(
        first(),
        pluck('requestId'),
        switchMap((requestId) => this.requestItemsService.getItemsByRequestUsingGET(requestId)),
        first(),
        map((res) => this.sortTimeline(res.items).find((action) => waitForRfiRdeTypes.includes(action.taskType))),
      )
      .subscribe((res) =>
        (res.taskType as string).includes('WAIT_FOR_RFI_RESPONSE')
          ? this.router.navigate(['/rfi', res.taskId, 'wait'])
          : this.router.navigate(['/rde', res.taskId, 'manual-approval']),
      );
  }

  private sortTimeline(res: ItemDTO[]): ItemDTO[] {
    return res
      .slice()
      .sort((a, b) => new Date(a.creationDate).getTime() - new Date(b.creationDate).getTime())
      .reverse();
  }
}
