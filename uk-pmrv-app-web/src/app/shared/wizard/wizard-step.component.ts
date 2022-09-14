import { ChangeDetectionStrategy, Component, EventEmitter, Input, OnInit, Output } from '@angular/core';
import { FormGroup } from '@angular/forms';

import { BehaviorSubject, filter, startWith, take } from 'rxjs';

import { BackLinkService } from '../back-link/back-link.service';

@Component({
  selector: 'app-wizard-step',
  templateUrl: './wizard-step.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
  providers: [BackLinkService],
})
export class WizardStepComponent implements OnInit {
  @Input() formGroup: FormGroup;
  @Input() heading: string;
  @Input() caption: string;
  @Input() submitText = 'Continue';
  @Input() hideSubmit: boolean;
  @Output() readonly formSubmit = new EventEmitter<FormGroup>();

  isSummaryDisplayedSubject = new BehaviorSubject(false);

  constructor(private readonly backLinkService: BackLinkService) {}

  ngOnInit(): void {
    this.backLinkService.show();
  }

  onSubmit(): void {
    this.formGroup.statusChanges
      .pipe(
        startWith(this.formGroup.status),
        filter((status) => status !== 'PENDING'),
        take(1),
      )
      .subscribe((status) => {
        switch (status) {
          case 'VALID':
            this.formSubmit.emit(this.formGroup);
            break;
          case 'INVALID':
            this.formGroup.markAllAsTouched();
            this.isSummaryDisplayedSubject.next(true);
            break;
        }
      });
  }
}
