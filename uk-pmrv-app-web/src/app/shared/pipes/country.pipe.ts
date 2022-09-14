import { ChangeDetectorRef, Pipe, PipeTransform } from '@angular/core';

import { catchError, EmptyError, of, take, tap } from 'rxjs';

import { CountryService } from '../../core/services/country.service';

// eslint-disable-next-line @angular-eslint/no-pipe-impure
@Pipe({ name: 'country', pure: false })
export class CountryPipe implements PipeTransform {
  private latestValue = '';

  constructor(private readonly countryService: CountryService, private readonly changeDetectorRef: ChangeDetectorRef) {}

  transform(value: string): string {
    this.countryService
      .getCountry(value)
      .pipe(
        tap((country) => (this.latestValue = country.name)),
        take(1),
        catchError((err) => {
          if (err instanceof EmptyError) {
            this.latestValue = 'Invalid country';
          }

          return of('');
        }),
      )
      .subscribe(() => this.changeDetectorRef.markForCheck());

    return this.latestValue;
  }
}
