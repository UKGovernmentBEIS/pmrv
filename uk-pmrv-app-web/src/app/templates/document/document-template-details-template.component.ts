import { ChangeDetectionStrategy, Component, Input } from '@angular/core';
import { Router } from '@angular/router';

import { DocumentTemplateDTO } from 'pmrv-api';

@Component({
  selector: 'app-document-template-details-template',
  templateUrl: './document-template-details-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class DocumentTemplateDetailsTemplateComponent {
  @Input() documentTemplate: DocumentTemplateDTO;

  constructor(private readonly router: Router) {}

  navigateToEmailTemplate(id: number): void {
    this.router.navigateByUrl(`templates/email/${id}`);
  }
}
