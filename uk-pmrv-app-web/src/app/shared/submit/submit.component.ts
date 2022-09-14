import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output, TemplateRef } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { BackLinkService } from '@shared/back-link/back-link.service';

import { RequestInfoDTO } from 'pmrv-api';

@Component({
  selector: 'app-submit',
  templateUrl: './submit.component.html',
  providers: [BackLinkService],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SubmitComponent implements OnInit {
  @Input() customSubmitContentTemplate: TemplateRef<any>;
  @Input() customSubmittedContentTemplate: TemplateRef<any>;
  @Input() allowSubmit: boolean;
  @Input() isEditable: boolean;
  @Input() isActionSubmitted: boolean;
  @Input() returnUrlConfig: { url: string; text: string };
  @Input() competentAuthority: RequestInfoDTO['competentAuthority'];

  @Output() readonly submitClicked = new EventEmitter<void>();

  constructor(private readonly backLinkService: BackLinkService, readonly route: ActivatedRoute) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit(): void {
    this.submitClicked.emit();
  }
}
