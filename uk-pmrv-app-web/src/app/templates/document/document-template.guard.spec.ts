import { TestBed } from '@angular/core/testing';
import { RouterTestingModule } from '@angular/router/testing';

import { lastValueFrom, of } from 'rxjs';

import { DocumentTemplatesService } from 'pmrv-api';

import { ActivatedRouteSnapshotStub, mockClass, MockType } from '../../../testing';
import { mockedDocumentTemplate } from '../testing/mock-data';
import { DocumentTemplateGuard } from './document-template.guard';

describe('DocumentTemplateGuard', () => {
  let guard: DocumentTemplateGuard;
  let documentTemplatesService: MockType<DocumentTemplatesService>;

  beforeEach(() => {
    documentTemplatesService = mockClass(DocumentTemplatesService);
    documentTemplatesService.getDocumentTemplateByIdUsingGET.mockReturnValueOnce(of(mockedDocumentTemplate));

    TestBed.configureTestingModule({
      imports: [RouterTestingModule],
      providers: [DocumentTemplateGuard, { provide: DocumentTemplatesService, useValue: documentTemplatesService }],
    });
    guard = TestBed.inject(DocumentTemplateGuard);
  });

  it('should be created', () => {
    expect(guard).toBeTruthy();
  });

  it('should return email template', async () => {
    await expect(
      lastValueFrom(guard.canActivate(new ActivatedRouteSnapshotStub({ templateId: mockedDocumentTemplate.id }))),
    ).resolves.toBeTruthy();

    expect(documentTemplatesService.getDocumentTemplateByIdUsingGET).toHaveBeenCalledWith(mockedDocumentTemplate.id);

    await expect(guard.resolve()).toEqual(mockedDocumentTemplate);
  });
});
