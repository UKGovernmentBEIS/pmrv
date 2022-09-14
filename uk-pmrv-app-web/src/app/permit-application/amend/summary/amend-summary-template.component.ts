import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';

import { combineLatest, map, takeUntil } from 'rxjs';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { BreadcrumbService } from '../../../shared/breadcrumbs/breadcrumb.service';
import { reviewGroupHeading } from '../../review/review';
import { PermitApplicationStore } from '../../store/permit-application.store';
import { amendTaskHeading, findReviewGroupsBySection } from '../amend';

@Component({
  selector: 'app-amend-summary-template',
  templateUrl: './amend-summary-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BreadcrumbService, DestroySubject],
})
export class AmendSummaryTemplateComponent implements OnInit {
  section$ = this.route.paramMap.pipe(map((paramMap) => paramMap.get('section')));
  reviewGroupDecisions$ = combineLatest([this.store, this.section$]).pipe(
    map(([state, section]) =>
      Object.keys(state.reviewGroupDecisions)
        .filter((reviewGroupKey) => findReviewGroupsBySection(section).includes(reviewGroupKey))
        .map((reviewGroupKey) => ({ groupKey: reviewGroupKey, data: state.reviewGroupDecisions[reviewGroupKey] })),
    ),
  );
  groupHeading = reviewGroupHeading;
  heading = amendTaskHeading;

  constructor(
    readonly store: PermitApplicationStore,
    private readonly route: ActivatedRoute,
    private readonly breadcrumbService: BreadcrumbService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit(): void {
    this.route.paramMap.pipe(takeUntil(this.destroy$)).subscribe((paramMap) => {
      const link = ['/permit-application', paramMap.get('taskId')];
      this.breadcrumbService.show([{ text: 'Apply for a permit', link }]);
    });
  }
}
