import { ChangeDetectionStrategy, Component, Input, OnInit, TemplateRef } from '@angular/core';
import { Router } from '@angular/router';

import { BackLinkService } from '@shared/back-link/back-link.service';
import { hasRequestTaskAllowedActions } from "@shared/components/related-actions/request-task-allowed-actions.map";

import { ItemDTO, RequestActionInfoDTO, RequestTaskItemDTO } from 'pmrv-api';

@Component({
  selector: 'app-task-layout',
  templateUrl: './task-layout.component.html',
  styles: [
    `
      .task-actions >>> * {
        float: right;
      }
    `,
  ],
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class TaskLayoutComponent implements OnInit {
  @Input() header: string;
  @Input() daysRemaining: number;
  @Input() requestTaskItem: RequestTaskItemDTO;
  @Input() customContentTemplate: TemplateRef<any>;
  @Input() relatedTasks: ItemDTO[];
  @Input() timelineActions: RequestActionInfoDTO[];
  @Input() showSectionBreak: boolean;

  navigationState = { returnUrl: this.router.url };
  hasRelatedActions: boolean;

  constructor(private readonly backService: BackLinkService, private readonly router: Router) {}

  ngOnInit(): void {
    this.hasRelatedActions = this.requestTaskItem.requestTask.assignable
      || hasRequestTaskAllowedActions(this.requestTaskItem.allowedRequestTaskActions)
    this.backService.show();
  }
}