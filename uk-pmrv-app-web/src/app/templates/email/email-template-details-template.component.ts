import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Router } from '@angular/router';

import { NotificationTemplateDTO } from 'pmrv-api';

@Component({
  selector: 'app-email-template-details-template',
  templateUrl: './email-template-details-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class EmailTemplateDetailsTemplateComponent {
  @Input() emailTemplate: NotificationTemplateDTO;

  constructor(private readonly router: Router) {}

  navigateToDocumentTemplate(id: number): void {
    this.router.navigateByUrl(`templates/document/${id}`);
  }
}
