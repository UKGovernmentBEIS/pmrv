import { ChangeDetectionStrategy, Component, Input, OnInit } from '@angular/core';
import { ValidationErrors } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { combineLatest, first, map, tap } from 'rxjs';

import { PermitApplicationStore } from '../../../../store/permit-application.store';
import { getSubtaskData, statusKeyToSubtaskNameMapper } from '../category-tier';

@Component({
  selector: 'app-analysis-method-list-template',
  templateUrl: './analysis-method-list-template.component.html',
  styleUrls: ['./analysis-method-list-template.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class AnalysisMethodListTemplateComponent implements OnInit {
  @Input() isPreview: boolean;
  @Input() formErrors: ValidationErrors | null;

  allowChange: boolean;
  subtaskData;

  readonly index$ = this.route.paramMap.pipe(map((paramMap) => Number(paramMap.get('index'))));
  private readonly statusKey = this.route.snapshot.data.statusKey;
  private readonly subtaskName = statusKeyToSubtaskNameMapper[this.statusKey];

  constructor(
    readonly store: PermitApplicationStore,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.allowChange = this.store.getValue().isEditable && !this.isPreview;

    combineLatest([this.index$, this.store])
      .pipe(
        first(),
        tap(([index, state]) => {
          this.subtaskData = getSubtaskData(state, index, this.statusKey);
        }),
      )
      .subscribe();
  }
  addAnalysisMethod(): void {
    this.router.navigate(
      [`../analysis-method/${this.subtaskData?.analysisMethods ? this.subtaskData?.analysisMethods.length : 0}`],
      {
        relativeTo: this.route,
        state: { changing: true },
      },
    );
  }
}
