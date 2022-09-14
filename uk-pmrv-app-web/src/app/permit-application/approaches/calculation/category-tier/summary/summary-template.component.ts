import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { map } from 'rxjs';

import { CalculationSourceStreamCategoryAppliedTier } from 'pmrv-api';

import { StatusKey } from '../../../../shared/types/permit-task.type';
import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { categoryAppliedTier, statusKeyToSubtaskNameMapper } from '../category-tier';
import { referenceMap } from '../reference/reference.map';

@Component({
  selector: 'app-calculation-category-tier-subtasks-summary-template',
  templateUrl: './summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class SummaryTemplateComponent implements OnInit {
  @Input() isPreview: boolean;
  @Input() statusKey: StatusKey = this.route.snapshot.data.statusKey;

  readonly index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  readonly referenceMap = referenceMap;

  readonly taskKey = this.route.snapshot.data.taskKey;

  readonly categoryAppliedTiers$ = this.store.findTask<categoryAppliedTier[]>(this.taskKey);

  subtaskName: keyof CalculationSourceStreamCategoryAppliedTier;

  allowChange: boolean;

  constructor(readonly store: PermitApplicationStore, private readonly route: ActivatedRoute) {}

  ngOnInit() {
    this.subtaskName = statusKeyToSubtaskNameMapper[this.statusKey];

    this.allowChange = this.store.getValue().isEditable && !this.isPreview;
  }
}
