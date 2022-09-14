import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { BreadcrumbService } from '@shared/breadcrumbs/breadcrumb.service';

@Component({
  selector: 'app-privacy-notice',
  templateUrl: './privacy-notice.component.html',
  providers: [BreadcrumbService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class PrivacyNoticeComponent implements OnInit {
  constructor(private readonly breadcrumbService: BreadcrumbService) {}

  ngOnInit(): void {
    this.breadcrumbService.show([{ text: 'Home', link: ['../'] }]);
  }
}
