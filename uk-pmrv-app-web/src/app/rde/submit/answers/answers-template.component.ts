import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, Observable, pluck } from 'rxjs';

import { RequestActionUserInfo } from 'pmrv-api';

import { RdeStore } from '../../store/rde.store';

@Component({
  selector: 'app-answers-template',
  templateUrl: './answers-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnswersTemplateComponent implements OnInit {
  signatory$: Observable<string>;
  usersInfo$: Observable<{
    [key: string]: RequestActionUserInfo;
  }>;
  operators$: Observable<string[]>;

  constructor(readonly store: RdeStore, private readonly route: ActivatedRoute, private readonly router: Router) {}

  ngOnInit(): void {
    this.signatory$ = this.store.pipe(pluck('rdePayload', 'signatory'));
    this.usersInfo$ = this.store.pipe(pluck('usersInfo')) as Observable<{ [key: string]: RequestActionUserInfo }>;
    this.operators$ = combineLatest([this.usersInfo$, this.signatory$]).pipe(
      first(),
      map(([usersInfo, signatory]) => Object.keys(usersInfo).filter((userId) => userId !== signatory)),
    );
  }

  changeDetailsClick(): void {
    this.router.navigate(['../extend-determination'], { relativeTo: this.route, state: { changing: true } });
  }

  changeRecipientsClick(): void {
    this.router.navigate(['../notify-users'], { relativeTo: this.route, state: { changing: true } });
  }
}
