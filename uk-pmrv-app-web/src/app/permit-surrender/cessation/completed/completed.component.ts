import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { combineLatest, map, Observable, pluck } from 'rxjs';

import { RequestActionUserInfo } from 'pmrv-api';

import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { PermitSurrenderStore } from '../../store/permit-surrender.store';

@Component({
  selector: 'app-completed',
  templateUrl: './completed.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class CompletedComponent implements OnInit {
  signatory$: Observable<string>;
  usersInfo$: Observable<{
    [key: string]: RequestActionUserInfo;
  }>;
  operators$: Observable<string[]>;

  constructor(readonly store: PermitSurrenderStore, private readonly backLinkService: BackLinkService) {}

  ngOnInit(): void {
    this.backLinkService.show();

    this.signatory$ = this.store.pipe(pluck('cessationDecisionNotification', 'signatory'));
    this.usersInfo$ = this.store.pipe(pluck('cessationDecisionNotificationUsersInfo')) as Observable<{
      [key: string]: RequestActionUserInfo;
    }>;
    this.operators$ = combineLatest([this.usersInfo$, this.signatory$]).pipe(
      map(([usersInfo, signatory]) => Object.keys(usersInfo).filter((userId) => userId !== signatory)),
    );
  }
}
