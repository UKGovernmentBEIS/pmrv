import {
  ChangeDetectionStrategy,
  Component,
  ElementRef,
  Input,
  OnInit,
  Optional,
  Self,
  ViewChild,
} from '@angular/core';
import { ControlValueAccessor, FormControl, NgControl } from '@angular/forms';

import {
  BehaviorSubject,
  combineLatest,
  filter,
  map,
  Observable,
  scan,
  startWith,
  Subject,
  tap,
  withLatestFrom,
} from 'rxjs';

import { FormService, MessageValidationErrors } from 'govuk-components';

import { FileUploadService } from '../file-input/file-upload.service';
import { FileUpload, FileUploadEvent } from '../file-input/file-upload-event';

/*
  eslint-disable
  @typescript-eslint/no-empty-function
 */
@Component({
  selector: 'app-multiple-file-input[baseDownloadUrl]',
  templateUrl: './multiple-file-input.component.html',
  styleUrls: ['./multiple-file-input.component.scss'],
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class MultipleFileInputComponent implements ControlValueAccessor, OnInit {
  @Input() listTitle: string;
  @Input() label = 'Upload a file';
  @Input() hint: string;
  @Input() accepted = '*/*';
  @Input() uploadStatusText = 'Uploading files, please wait';
  @Input() dropzoneHintText = 'Drag and drop files here or';
  @Input() dropzoneButtonText = 'Choose files';
  @Input() baseDownloadUrl: string;
  @ViewChild('input') fileInput: ElementRef<HTMLInputElement>;

  uploadStatusText$ = new Subject<string>();
  uploadedFiles$: Observable<FileUploadEvent[]>;
  isFocused = false;
  isDraggedOver = false;
  isDisabled: boolean;
  private onChange: (value: FileUpload[]) => any;
  private onBlur: () => any;
  private value$ = new BehaviorSubject<FileUpload[]>([]);

  constructor(
    @Self() @Optional() private readonly ngControl: NgControl,
    private readonly formService: FormService,
    private readonly fileUploadService: FileUploadService,
  ) {
    ngControl.valueAccessor = this;
  }

  get id(): string {
    return this.formService.getControlIdentifier(this.ngControl);
  }

  private get control(): FormControl {
    return this.ngControl.control as FormControl;
  }

  ngOnInit(): void {
    this.uploadedFiles$ = combineLatest([
      this.value$,
      this.control.statusChanges.pipe(
        startWith(this.control.status),
        //as we now have async validators we should update errors on final statuses (VALID, INVALID)
        filter((status) => status !== 'PENDING'),
        map(() => this.control.errors),
        startWith(this.control.errors),
      ),
      this.fileUploadService.uploadProgress$.pipe(
        withLatestFrom(this.value$),
        filter(([uploadEvent, value]) => value?.some(({ file }) => file === uploadEvent.file)),
        tap(([{ progress, ...uploadEvent }, value]) => {
          if (uploadEvent.uuid) {
            value.splice(
              value.findIndex((upload) => upload.file === uploadEvent.file),
              1,
              uploadEvent,
            );
          }
        }),
        map(([uploadEvent]) => uploadEvent),
        startWith(undefined),
      ),
    ]).pipe(
      scan(
        (acc, [existing, errors, uploadEvent]) =>
          (existing ?? []).map((existingFile, index) => {
            return {
              ...existingFile,
              ...acc.find(({ file, uuid }) => (uuid && uuid === existingFile.uuid) || file === existingFile.file),
              ...(uploadEvent?.file === existingFile.file ? uploadEvent : {}),
              ...(existingFile.uuid && { downloadUrl: this.baseDownloadUrl + `${existingFile.uuid}` }),
              errors: this.applyRowErrors(errors, index),
            };
          }),
        [],
      ),
    );
  }

  registerOnChange(onChange: (value: FileUpload[]) => any): void {
    this.onChange = (value) => {
      this.value$.next(value);
      onChange(value);
    };
  }

  registerOnTouched(onBlur: () => any): void {
    this.onBlur = onBlur;
  }

  writeValue(value: FileUploadEvent[]): void {
    this.value$.next(value ?? []);
  }

  setDisabledState(isDisabled: boolean): void {
    this.isDisabled = isDisabled;
  }

  onFileChange(event: Event): void {
    const files = (event?.target as HTMLInputElement)?.files;
    this.uploadFiles(files);
    this.fileInput.nativeElement.value = null;
    this.fileInput.nativeElement.focus();
  }

  onFileFocus(): void {
    this.isFocused = true;
  }

  onFileBlur(): void {
    this.isFocused = false;
    this.onBlur();
  }

  onDragOver(event: DragEvent): void {
    event.preventDefault();

    if (!this.isDisabled) {
      this.isDraggedOver = true;
    }
  }

  onDragLeave(): void {
    this.isDraggedOver = false;
  }

  onDrop(event: DragEvent): void {
    event.preventDefault();
    this.isDraggedOver = false;

    if (!this.isDisabled) {
      this.uploadStatusText$.next(this.uploadStatusText);
      this.uploadFiles(event.dataTransfer.files);
    }
  }

  onFileDeleteClick(deletedIndex: number): void {
    this.onChange(this.value$.getValue().filter((_, index) => index !== deletedIndex));
  }

  private uploadFiles(files: FileList): void {
    this.onChange(this.value$.getValue().concat(Array.from(files).map((file) => ({ file }))));
  }

  private applyRowErrors(errors: MessageValidationErrors, index: number): MessageValidationErrors {
    const rowErrors = Object.entries(errors ?? {}).filter(([key]) => key.endsWith(`-${index}`));

    return rowErrors.length === 0
      ? null
      : rowErrors.reduce((acc, [key, value]) => ({ ...(acc ? acc : {}), [key]: value } as any), null);
  }
}
