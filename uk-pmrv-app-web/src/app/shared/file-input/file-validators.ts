import { GovukValidators, MessageValidationErrors, MessageValidatorFn } from 'govuk-components';

import { FileUploadEvent } from '../file-input/file-upload-event';

export class FileValidators {
  static maxFileSize(max: number, message?: string): MessageValidatorFn {
    return ({ value }: { value: FileUploadEvent | FileUploadEvent[] }) => {
      const valueIsArray = value && value instanceof Array && value.length > 0;
      const values: FileUploadEvent[] = (valueIsArray ? value : [value]) as FileUploadEvent[];

      const messages = values.reduce((acc, event, idx) => {
        const errorMessage =
          event?.file?.size >= max * 1024 * 1024
            ? `${event.file.name} ` + (message ?? `must be smaller than ${max}MB`)
            : null;
        return errorMessage ? { ...acc, [`maxFileSize-${idx}`]: errorMessage } : acc;
      }, {});
      return Object.keys(messages).length > 0 ? messages : null;
    };
  }

  static notEmpty(): MessageValidatorFn {
    return ({ value }: { value: FileUploadEvent | FileUploadEvent[] }) => {
      const valueIsArray = value && value instanceof Array && value.length > 0;
      const values: FileUploadEvent[] = (valueIsArray ? value : [value]) as FileUploadEvent[];

      const messages = values.reduce((acc, event, idx) => {
        const message = event?.file?.size === 0 ? `${event.file.name} should not be empty` : null;
        return message ? { ...acc, [`notEmpty-${idx}`]: message } : acc;
      }, {});
      return Object.keys(messages).length > 0 ? messages : null;
    };
  }

  static validContentTypes(types: string[], message: string = 'has an invalid type'): MessageValidatorFn {
    return ({ value }: { value: FileUploadEvent | FileUploadEvent[] }) => {
      const valueIsArray = value && value instanceof Array && value.length > 0;
      const values: FileUploadEvent[] = (valueIsArray ? value : [value]) as FileUploadEvent[];

      const messages = values.reduce((acc, event, idx) => {
        const fileType = event?.file?.type;

        const errorMessage = fileType && !types.includes(fileType) ? `${event.file.name} ` + message : null;
        return errorMessage ? { ...acc, [`validContentTypes-${idx}`]: errorMessage } : acc;
      }, {});
      return Object.keys(messages).length > 0 ? messages : null;
    };
  }

  static maxImageDimensionsSize(maxWidth: number, maxHeight: number, message?: string): MessageValidatorFn {
    return ({ value }: { value: FileUploadEvent | FileUploadEvent[] }) => {
      const valueIsArray = value && value instanceof Array && value.length > 0;
      const values: FileUploadEvent[] = (valueIsArray ? value : [value]) as FileUploadEvent[];

      const messages = values
        .filter((val) => !!val?.dimensions && (val.dimensions.width || val.dimensions.height))
        .reduce((acc, event, idx) => {
          const dim = event.dimensions;

          const areInvalidDimensions = dim?.width > maxWidth || dim?.height > maxHeight;
          const errorMessage = areInvalidDimensions
            ? `${event.file.name} ` + (message ?? `must be smaller than ${maxWidth}x${maxHeight} px`)
            : null;
          return errorMessage ? { ...acc, [`dimensions-${idx}`]: errorMessage } : acc;
        }, {});
      return Object.keys(messages).length > 0 ? messages : null;
    };
  }

  static concatenateErrors(errors: MessageValidationErrors[] = []): MessageValidationErrors {
    return errors?.every((error) => error === null)
      ? null
      : errors.reduce(
          (result, error, index) => ({
            ...result,
            ...(error &&
              Object.entries(error).reduce((acc, [key, value]) => ({ ...acc, [`${key}-${index}`]: value }), {})),
          }),
          {},
        );
  }

  static multipleCompose(
    validator: ({ value }: { value: FileUploadEvent }) => MessageValidationErrors,
  ): MessageValidatorFn {
    return (control) => {
      const value: FileUploadEvent[] | null = control.value;
      const fileValidities = value?.map((fileEvent) => validator({ value: fileEvent }));

      return this.concatenateErrors(fileValidities);
    };
  }
}

export const commonFileValidators: MessageValidatorFn[] = [FileValidators.maxFileSize(20), FileValidators.notEmpty()];
export const requiredFileValidator = GovukValidators.required('Select a file');
