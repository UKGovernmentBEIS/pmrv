import { AfterViewChecked, ChangeDetectionStrategy, Component, ElementRef, ViewChild } from '@angular/core';
import { ActivatedRoute, ParamMap } from '@angular/router';

import { combineLatest, expand, map, Observable, of, switchMap, switchMapTo, timer } from 'rxjs';

import {
  DocumentTemplateFilesService,
  FileAttachmentsService,
  FileDocumentsService,
  FileDocumentTemplatesService,
  FileToken,
  RegulatorUsersService,
  RequestActionAttachmentsHandlingService,
  RequestActionFileDocumentsHandlingService,
  RequestTaskAttachmentsHandlingService,
  UsersService,
} from 'pmrv-api';

export interface FileDownloadInfo {
  request: Observable<FileToken>;
  fileType: 'attachment' | 'documentTemplate' | 'document' | 'userSignature';
}

@Component({
  selector: 'app-file-download',
  template: `
    <h1 class="govuk-heading-l">Your download has started</h1>
    <p class="govuk-body">You should see your downloads in the downloads folder.</p>
    <a govukLink [href]="url$ | async" download #anchor>Click to restart download if it fails</a>
  `,
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class FileDownloadComponent implements AfterViewChecked {
  @ViewChild('anchor') readonly anchor: ElementRef<HTMLAnchorElement>;

  private hasDownloadedOnce = false;
  private fileDownloadAttachmentPath = `${this.fileAttachmentsService.configuration.basePath}/api/v1.0/file-attachments/`;
  private fileDownloadDocumentTemplatePath = `${this.fileDocumentTemplatesService.configuration.basePath}/api/v1.0/file-document-templates/`;
  private fileDownloadDocumentPath = `${this.fileDocumentsService.configuration.basePath}/api/v1.0/file-documents/`;
  private userSignaturePath = `${this.regulatorUsersService.configuration.basePath}/api/v1.0/user-signatures/`;

  url$ = this.route.paramMap.pipe(
    map((params): FileDownloadInfo => {
      return params.has('taskId')
        ? this.requestTaskDowloadInfo(params)
        : params.has('actionId')
        ? this.requestActionDownloadInfo(params)
        : params.has('templateId')
        ? this.documentTemplateDownloadInfo(params)
        : params.has('userId')
        ? this.regulatorSignatureDownloadInfo(params)
        : this.currentUserSignaturedDownloadInfo(params);
    }),
    switchMap(({ request, fileType }) => {
      return combineLatest([
        of(fileType),
        request.pipe(
          expand((response) => timer(response.tokenExpirationMinutes * 1000 * 60).pipe(switchMapTo(request))),
        ),
      ]);
    }),
    map(([fileType, fileToken]) => {
      return fileType === 'attachment'
        ? `${this.fileDownloadAttachmentPath}${encodeURIComponent(String(fileToken.token))}`
        : fileType === 'documentTemplate'
        ? `${this.fileDownloadDocumentTemplatePath}${encodeURIComponent(String(fileToken.token))}`
        : fileType === 'document'
        ? `${this.fileDownloadDocumentPath}${encodeURIComponent(String(fileToken.token))}`
        : `${this.userSignaturePath}${encodeURIComponent(String(fileToken.token))}`;
    }),
  );

  constructor(
    private readonly route: ActivatedRoute,
    private readonly requestTaskAttachmentsHandlingService: RequestTaskAttachmentsHandlingService,
    private readonly requestActionAttachmentsHandlingService: RequestActionAttachmentsHandlingService,
    private readonly requestActionFileDocumentsHandlingService: RequestActionFileDocumentsHandlingService,
    private readonly documentTemplateFilesService: DocumentTemplateFilesService,
    private readonly fileAttachmentsService: FileAttachmentsService,
    private readonly fileDocumentTemplatesService: FileDocumentTemplatesService,
    private readonly fileDocumentsService: FileDocumentsService,
    private readonly regulatorUsersService: RegulatorUsersService,
    private readonly usersService: UsersService,
  ) {}

  ngAfterViewChecked(): void {
    if (
      (this.anchor.nativeElement.href.includes(this.fileDownloadAttachmentPath) ||
        this.anchor.nativeElement.href.includes(this.fileDownloadDocumentTemplatePath) ||
        this.anchor.nativeElement.href.includes(this.fileDownloadDocumentPath) ||
        this.anchor.nativeElement.href.includes(this.userSignaturePath)) &&
      !this.hasDownloadedOnce
    ) {
      this.anchor.nativeElement.click();
      this.hasDownloadedOnce = true;
      onfocus = () => close();
    }
  }

  private currentUserSignaturedDownloadInfo(params: ParamMap): FileDownloadInfo {
    return {
      request: this.usersService.generateGetCurrentUserSignatureTokenUsingGET(params.get('uuid')),
      fileType: 'userSignature',
    };
  }

  private regulatorSignatureDownloadInfo(params: ParamMap): FileDownloadInfo {
    return {
      request: this.regulatorUsersService.generateGetRegulatorSignatureTokenUsingGET(
        params.get('userId'),
        params.get('uuid'),
      ),
      fileType: 'userSignature',
    };
  }

  private documentTemplateDownloadInfo(params: ParamMap): FileDownloadInfo {
    return {
      request: this.documentTemplateFilesService.generateGetDocumentTemplateFileTokenUsingGET(
        Number(params.get('templateId')),
        params.get('uuid'),
      ),
      fileType: 'documentTemplate',
    };
  }

  private requestTaskDowloadInfo(params: ParamMap): FileDownloadInfo {
    return {
      request: this.requestTaskAttachmentsHandlingService.generateRequestTaskGetFileAttachmentTokenUsingGET(
        Number(params.get('taskId')),
        params.get('uuid'),
      ),
      fileType: 'attachment',
    };
  }

  private requestActionDownloadInfo(params: ParamMap): FileDownloadInfo {
    if (params.get('fileType') === 'document') {
      return {
        request: this.requestActionFileDocumentsHandlingService.generateRequestActionGetFileDocumentTokenUsingGET(
          Number(params.get('actionId')),
          params.get('uuid'),
        ),
        fileType: 'document',
      };
    } else {
      return {
        request: this.requestActionAttachmentsHandlingService.generateRequestActionGetFileAttachmentTokenUsingGET(
          Number(params.get('actionId')),
          params.get('uuid'),
        ),
        fileType: 'attachment',
      };
    }
  }
}
