import { global } from '@angular/compiler/src/util';
import { ComponentFixture, TestBed } from '@angular/core/testing';
import { ActivatedRoute } from '@angular/router';
import { RouterTestingModule } from '@angular/router/testing';

import { defer, firstValueFrom, of, take } from 'rxjs';

import { FileAttachmentsService, RequestTaskAttachmentsHandlingService, TasksService } from 'pmrv-api';

import { ActivatedRouteStub, mockClass } from '../../../testing';
import { testSchedulerFactory } from '../../../testing/marble-helpers';
import { SharedModule } from '../shared.module';
import { FileDownloadComponent } from './file-download.component';

describe('FileDownloadComponent', () => {
  let component: FileDownloadComponent;
  let fixture: ComponentFixture<FileDownloadComponent>;
  let requestTaskAttachmentsHandlingService: jest.Mocked<RequestTaskAttachmentsHandlingService>;

  beforeEach(async () => {
    Object.defineProperty(global, 'onfocus', { set: jest.fn() });
    requestTaskAttachmentsHandlingService = mockClass(RequestTaskAttachmentsHandlingService);
    requestTaskAttachmentsHandlingService.generateRequestTaskGetFileAttachmentTokenUsingGET.mockReturnValue(
      of({ token: 'abce', tokenExpirationMinutes: 1 }),
    );
    const activatedRoute = new ActivatedRouteStub({ uuid: 'xyz', taskId: 11 });

    await TestBed.configureTestingModule({
      imports: [RouterTestingModule, SharedModule],
      providers: [
        { provide: ActivatedRoute, useValue: activatedRoute },
        { provide: RequestTaskAttachmentsHandlingService, useValue: requestTaskAttachmentsHandlingService },
        { provide: TasksService, useValue: mockClass(TasksService) },
        { provide: FileAttachmentsService, useValue: { configuration: { basePath: '' } } },
      ],
    }).compileComponents();
  });

  beforeEach(() => {
    fixture = TestBed.createComponent(FileDownloadComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });

  it('should display the download link', async () => {
    await expect(firstValueFrom(component.url$)).resolves.toEqual('/api/v1.0/file-attachments/abce');
  });

  it('should refresh the download link', async () => {
    requestTaskAttachmentsHandlingService.generateRequestTaskGetFileAttachmentTokenUsingGET
      .mockClear()
      .mockImplementation(() => {
        let subscribes = 0;

        return defer(() => {
          subscribes += 1;

          return subscribes === 1
            ? of({ token: 'abcf', tokenExpirationMinutes: 1 })
            : subscribes === 2
            ? of({ token: 'abcd', tokenExpirationMinutes: 2 })
            : of({ token: 'abce', tokenExpirationMinutes: 1 });
        });
      });

    testSchedulerFactory().run(({ expectObservable }) =>
      expectObservable(component.url$.pipe(take(3))).toBe('a 59s 999ms b 119s 999ms (c|)', {
        a: '/api/v1.0/file-attachments/abcf',
        b: '/api/v1.0/file-attachments/abcd',
        c: '/api/v1.0/file-attachments/abce',
      }),
    );
  });
});
