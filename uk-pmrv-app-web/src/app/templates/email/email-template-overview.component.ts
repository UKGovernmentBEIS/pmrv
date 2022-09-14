import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable, pluck } from 'rxjs';

import { NotificationTemplateDTO } from 'pmrv-api';

import { BackLinkService } from '../../shared/back-link/back-link.service';

@Component({
  selector: 'app-email-template-overview',
  templateUrl: './email-template-overview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class EmailTemplateOverviewComponent implements OnInit {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  emailTemplate$: Observable<NotificationTemplateDTO> = this.route.data.pipe(pluck('emailTemplate'));

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
