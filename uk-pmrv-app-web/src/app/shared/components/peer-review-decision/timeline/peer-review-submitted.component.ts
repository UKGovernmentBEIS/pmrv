import { ChangeDetectionStrategy, ChangeDetectorRef, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { RequestActionsService } from 'pmrv-api';

import { BackLinkService } from '../../../back-link/back-link.service';

@Component({
  selector: 'app-peer-reviewer-submitted',
  templateUrl: './peer-review-submitted.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class PeerReviewSubmittedComponent implements OnInit {
  action: any;

  constructor(
    private readonly requestActionsService: RequestActionsService,
    private readonly backLinkService: BackLinkService,
    private readonly route: ActivatedRoute,
    private readonly cdRef: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
    this.requestActionsService
      .getRequestActionByIdUsingGET(Number(this.route.snapshot.paramMap.get('actionId')))
      .subscribe((res) => {
        this.action = res;
        this.cdRef.markForCheck();
      });
  }
}
