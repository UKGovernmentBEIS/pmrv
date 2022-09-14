import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';

import { BackLinkService } from '../../shared/back-link/back-link.service';

@Component({
  selector: 'app-submitted',
  template: `
    <app-page-heading>Surrender your permit</app-page-heading>
    <app-permit-surrender-summary></app-permit-surrender-summary>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class SubmittedComponent implements OnInit {
  constructor(private readonly backLinkService: BackLinkService) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }
}
