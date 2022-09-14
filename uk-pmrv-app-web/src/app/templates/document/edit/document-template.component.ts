import { ChangeDetectionStrategy, Component, Inject, OnInit } from '@angular/core';
import { FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, first, Observable, of, pluck, switchMap, takeUntil } from 'rxjs';

import { DocumentTemplateDTO, DocumentTemplatesService } from 'pmrv-api';

import { DestroySubject } from '../../../core/services/destroy-subject.service';
import { BackLinkService } from '../../../shared/back-link/back-link.service';
import { DOCUMENT_TEMPLATE_FORM, DocumentTemplateFormProvider } from './document-template-form.provider';

@Component({
  selector: 'app-document-template',
  templateUrl: './document-template.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService, DocumentTemplateFormProvider, DestroySubject],
})
export class DocumentTemplateComponent implements OnInit {
  documentTemplate$: Observable<DocumentTemplateDTO> = this.route.data.pipe(pluck('documentTemplate'));
  displayErrorSummary$ = new BehaviorSubject<boolean>(false);

  constructor(
    @Inject(DOCUMENT_TEMPLATE_FORM) readonly form: FormGroup,
    private readonly route: ActivatedRoute,
    private readonly router: Router,
    private readonly documentTemplatesService: DocumentTemplatesService,
    private readonly backLinkService: BackLinkService,
    private readonly destroy$: DestroySubject,
  ) {}

  ngOnInit() {
    this.backLinkService.show();
    this.form.controls.documentFile.valueChanges
      .pipe(takeUntil(this.destroy$))
      .subscribe(() => this.displayErrorSummary$.next(false));
  }

  onSubmit(): void {
    if (!this.form.valid) {
      this.displayErrorSummary$.next(true);
    } else {
      this.documentTemplate$
        .pipe(
          first(),
          switchMap((documentTemplate) =>
            this.form.dirty
              ? this.documentTemplatesService.updateDocumentTemplateUsingPOST(
                  documentTemplate.id,
                  this.form.controls.documentFile.value.file,
                )
              : of(null),
          ),
        )
        .subscribe(() => this.router.navigate(['..'], { relativeTo: this.route, state: { notification: true } }));
    }
  }

  getDownloadUrl(uuid: string): string | string[] {
    return ['..', 'file-download', uuid];
  }
}
