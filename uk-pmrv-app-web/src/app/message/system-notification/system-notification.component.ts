import { ChangeDetectionStrategy, Component } from '@angular/core';
import { Title } from '@angular/platform-browser';
import { ActivatedRoute, Router } from '@angular/router';

import { map, pluck, tap } from 'rxjs';

@Component({
  selector: 'app-system-notification',
  templateUrl: './system-notification.component.html',
  styleUrls: ['./system-notification.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SystemNotificationComponent {
  private readonly info$ = this.route.data.pipe(pluck('review'));
  private readonly requestTask$ = this.info$.pipe(pluck('requestTask'));
  private readonly payload$ = this.requestTask$.pipe(pluck('payload'));

  readonly allowedActions$ = this.info$.pipe(pluck('allowedRequestTaskActions'));
  readonly taskId$ = this.requestTask$.pipe(pluck('id'));
  readonly date$ = this.requestTask$.pipe(pluck('startDate'));
  readonly title$ = this.payload$.pipe(
    pluck('subject'),
    tap((subject) => this.title.setTitle(subject)),
  );
  readonly data$ = this.payload$.pipe(
    pluck('text'),
    map((content) =>
      content.replace(
        /(?:__|[*#])|\[(.*?)]\(.*?\)/gm,
        (text, p1) => `[${p1}](${this.getRoute(JSON.parse(text.match(/{.+}/gm)[0]))})`,
      ),
    ),
  );

  constructor(private readonly route: ActivatedRoute, private readonly router: Router, private readonly title: Title) {}

  archived(): void {
    this.router.navigate(['/dashboard']);
  }

  private getRoute(params: { action: string } & { [key: string]: number }): string {
    switch (params.action) {
      case 'ACCOUNT_USERS_SETUP':
        return `/accounts/${params.accountId}` + `#users`;
      case 'VERIFICATION_BODY_USERS_SETUP':
        return '/user/verifiers';
      default:
        return '/';
    }
  }
}
