import { ChangeDetectorRef, Directive, OnInit } from '@angular/core';

import { map } from 'rxjs';

import { SelectComponent } from 'govuk-components';

import { Country } from '../../core/models/country';
import { CountryService } from '../../core/services/country.service';

@Directive({
  selector: 'govuk-select[appCountries],[govuk-select][appCountries]',
})
export class CountriesDirective implements OnInit {
  constructor(
    private readonly apiService: CountryService,
    private readonly selectComponent: SelectComponent,
    private readonly changeDetectorRef: ChangeDetectorRef,
  ) {}

  ngOnInit(): void {
    this.apiService
      .getUkCountries()
      .pipe(
        map((countries: Country[]) =>
          countries
            .sort((a: Country, b: Country) => (a.name > b.name ? 1 : -1))
            .map((country) => ({
              text: country.name,
              value: country.code,
            })),
        ),
      )
      .subscribe((res) => {
        this.selectComponent.options = res;
        this.changeDetectorRef.markForCheck();
      });
  }
}
