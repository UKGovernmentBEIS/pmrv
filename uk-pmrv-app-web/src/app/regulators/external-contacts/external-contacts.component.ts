import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';

import { BehaviorSubject, combineLatest, map, Observable, pluck, shareReplay } from 'rxjs';

import { GovukTableColumn, SortEvent } from 'govuk-components';

import { CaExternalContactDTO, CaExternalContactsService } from 'pmrv-api';

@Component({
  selector: 'app-external-contacts',
  templateUrl: './external-contacts.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class ExternalContactsComponent implements OnInit {
  sorting$ = new BehaviorSubject<SortEvent>({ column: 'lastUpdatedDate', direction: 'ascending' });
  contacts$: Observable<CaExternalContactDTO[]>;
  isEditable$: Observable<boolean>;
  editableColumns: GovukTableColumn<CaExternalContactDTO>[] = [
    { field: 'name', header: 'Displayed name', isSortable: true, isHeader: true },
    { field: 'email', header: 'Email address', isSortable: true },
    { field: 'description', header: 'Description' },
    { field: null, header: null },
  ];
  nonEditableColumns = this.editableColumns.slice(0, 3);

  constructor(
    readonly router: Router,
    readonly route: ActivatedRoute,
    private readonly externalContactsService: CaExternalContactsService,
  ) {}

  ngOnInit(): void {
    const contactResponse$ = this.externalContactsService
      .getCaExternalContactsUsingGET()
      .pipe(shareReplay({ bufferSize: 1, refCount: true }));
    this.isEditable$ = contactResponse$.pipe(pluck('isEditable'));
    this.contacts$ = combineLatest([contactResponse$.pipe(pluck('caExternalContacts')), this.sorting$]).pipe(
      map(([contacts, sorting]) => contacts.slice().sort(this.sortContacts(sorting))),
    );
  }

  private sortContacts(sorting: SortEvent): (a: CaExternalContactDTO, b: CaExternalContactDTO) => number {
    return (a, b) => {
      let diff: number;

      switch (sorting.column) {
        case 'name':
        case 'email':
          diff = a[sorting.column].localeCompare(b[sorting.column], 'en-GB', { sensitivity: 'base' });
          break;
        default:
          diff = new Date(a.lastUpdatedDate).valueOf() - new Date(b.lastUpdatedDate).valueOf();
          break;
      }

      return diff * (sorting.direction === 'ascending' ? 1 : -1);
    };
  }
}
