import { AbstractControl, FormGroup, ValidatorFn } from '@angular/forms';

// @dynamic
export class DateInputValidators {
  static dateFieldValidator(identifier: string, min: number, max: number): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null =>
      control.value && (control.value < min || control.value > max) ? { [identifier]: true } : null;
  }

  static minMaxDateValidator(min: Date, max: Date): ValidatorFn {
    return (control: AbstractControl): { [key: string]: boolean } | null =>
      control.value && min && control.value < min
        ? { minDate: true }
        : control.value && max && control.value > max
        ? { maxDate: true }
        : null;
  }

  static dateIncompleteValidator: ValidatorFn = (fg: FormGroup) => {
    const day = fg.get('day').value;
    const month = fg.get('month').value;
    const year = fg.get('year').value;
    return (day || month || year) && (!year || !month || !day) ? { incomplete: true } : null;
  };

  static leapYearValidator: ValidatorFn = (fg: FormGroup) => {
    const day = fg.get('day').value;
    const month = fg.get('month').value;
    const year = fg.get('year').value;

    return Number(day) === 29 && Number(month) === 2 && !DateInputValidators.isLeapYear(Number(year))
      ? { leapYear: true }
      : null;
  };

  static incorrectDayValidator: ValidatorFn = (fg: FormGroup) => {
    const day = fg.get('day').value;
    const month = fg.get('month').value;

    return (Number(day) > 29 && Number(month) === 2) ||
      (DateInputValidators.isShortMonth(Number(month)) && Number(day) > 30) ||
      Number(day) > 31
      ? { day: true }
      : null;
  };

  static isLeapYear(year: number): boolean {
    // eslint-disable-next-line no-bitwise
    return !(year & 3 || (!(year % 25) && year & 15));
  }

  static isShortMonth(month: number): boolean {
    return month === 2 || month === 4 || month === 6 || month === 9 || month === 11;
  }

  static buildDate({ year, month, day }): Date | null {
    return !year || !month || !day ? null : new Date(Date.UTC(Number(year), Number(month) - 1, Number(day)));
  }
}
