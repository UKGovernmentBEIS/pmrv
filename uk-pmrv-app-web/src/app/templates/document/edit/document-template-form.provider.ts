import { InjectionToken } from '@angular/core';
import { FormBuilder, FormControl, FormGroup } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';

import { DocumentTemplateDTO, DocumentTemplateFilesService } from 'pmrv-api';

import { FileType } from '../../../shared/file-input/file-type.enum';
import { FileUploadService } from '../../../shared/file-input/file-upload.service';
import { FileUploadEvent } from '../../../shared/file-input/file-upload-event';
import {
  commonFileValidators,
  FileValidators,
  requiredFileValidator,
} from '../../../shared/file-input/file-validators';

export const DOCUMENT_TEMPLATE_FORM = new InjectionToken<FormGroup>('Document Template Form');

export const DocumentTemplateFormProvider = {
  provide: DOCUMENT_TEMPLATE_FORM,
  deps: [FormBuilder, ActivatedRoute, FileUploadService, DocumentTemplateFilesService],
  useFactory: (fb: FormBuilder, route: ActivatedRoute) => {
    const documentTemplate = route.snapshot.data.documentTemplate as DocumentTemplateDTO;

    return fb.group({
      documentFile: new FormControl(
        documentTemplate.fileUuid
          ? ({
              uuid: documentTemplate.fileUuid,
              file: { name: documentTemplate.filename } as File,
            } as Pick<FileUploadEvent, 'file' | 'uuid'>)
          : null,
        {
          validators: commonFileValidators.concat(
            requiredFileValidator,
            FileValidators.validContentTypes([FileType.DOCX]),
          ),
          updateOn: 'change',
        },
      ),
    });
  },
};
