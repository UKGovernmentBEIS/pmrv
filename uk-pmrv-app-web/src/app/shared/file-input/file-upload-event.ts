import { MessageValidationErrors } from 'govuk-components';

export interface FileUploadEvent {
  downloadUrl?: string | string[];
  errors?: MessageValidationErrors;
  file: File;
  progress: number;
  uuid?: string;
  dimensions?: {
    width: number;
    height: number;
  };
}

export type FileUpload = Pick<FileUploadEvent, 'file' | 'uuid' | 'dimensions'>;
export type UuidFilePair = { file: File; uuid: string };
