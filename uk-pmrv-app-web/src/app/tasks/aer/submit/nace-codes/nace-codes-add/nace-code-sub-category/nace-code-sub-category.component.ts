import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { AER_TASK_FORM } from '@tasks/aer/core/aer-task-form.token';
import { MainActivitySubCategoryMap, subCategories, SubCategory } from '@tasks/aer/submit/nace-codes/nace-code-types';
import { naceCodeSubCategoryFormProvider } from '@tasks/aer/submit/nace-codes/nace-codes-add/nace-code-sub-category/nace-code-sub-category-form.provider';

@Component({
  selector: 'app-nace-code-sub-category',
  templateUrl: './nace-code-sub-category.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [naceCodeSubCategoryFormProvider],
})
export class NaceCodeSubCategoryComponent implements OnInit {
  subCategoryOptions: [string, string][];
  readonly mainActivity = this.router.getCurrentNavigation()?.extras.state?.mainActivity;

  constructor(
    @Inject(AER_TASK_FORM) readonly form: FormGroup,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
  ) {}

  ngOnInit(): void {
    const relevantSubCategories = Object.entries(MainActivitySubCategoryMap).find(
      (entry) => entry[0] === this.mainActivity,
    )[1];
    this.subCategoryOptions = Object.entries(subCategories).filter((entry) =>
      relevantSubCategories.includes(entry[0] as SubCategory),
    );
  }

  onSubmit(): void {
    const subCategory = this.form.value.subCategory;
    this.router.navigate(['..', 'installation-activity'], {
      relativeTo: this.route,
      state: { subCategory: subCategory },
    });
  }
}
