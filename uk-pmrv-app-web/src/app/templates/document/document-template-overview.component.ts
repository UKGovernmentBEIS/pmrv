import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { Observable, pluck } from 'rxjs';

import { DocumentTemplateDTO } from 'pmrv-api';

import { BackLinkService } from '../../shared/back-link/back-link.service';

@Component({
  selector: 'app-document-template-overview',
  templateUrl: './document-template-overview.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class DocumentTemplateOverviewComponent implements OnInit {
  notification = this.router.getCurrentNavigation()?.extras.state?.notification;
  documentTemplate$: Observable<DocumentTemplateDTO> = this.route.data.pipe(pluck('documentTemplate'));

  constructor(
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly backLinkService: BackLinkService,
  ) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
