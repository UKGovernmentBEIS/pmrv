import { ChangeDetectionStrategy, Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup } from '@angular/forms';
import { ActivatedRoute, Router } from '@angular/router';

import { first, map, pluck, switchMap } from 'rxjs';

import { PendingRequestService } from '@core/guards/pending-request.service';

import { GovukValidators } from 'govuk-components';

import { PaymentMakeRequestTaskPayload } from 'pmrv-api';

import { PaymentStore } from '../../store/payment.store';

@Component({
  selector: 'app-options',
  templateUrl: './options.component.html',
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class OptionsComponent implements OnInit {
  readonly availablePaymentMethods$ = this.store.pipe(
    first(),
    map((state) => state.paymentDetails as PaymentMakeRequestTaskPayload),
    pluck('paymentMethodTypes'),
  );
  form: FormGroup;

  constructor(
    readonly store: PaymentStore,
    readonly pendingRequest: PendingRequestService,
    private readonly formBuilder: FormBuilder,
    private readonly router: Router,
    private readonly route: ActivatedRoute,
  ) {}

  ngOnInit(): void {
    this.store.pipe(first()).subscribe(
      (state) =>
        (this.form = this.formBuilder.group({
          paymentMethod: [
            { value: null, disabled: !state.isEditable },
            { validators: GovukValidators.required('Select a payment method'), updateOn: 'change' },
          ],
        })),
    );
  }

  onContinue(): void {
    const paymentMethod = this.form.get('paymentMethod').value;
    this.store.setState({
      ...this.store.getState(),
      selectedPaymentMethod: paymentMethod,
    });

    switch (paymentMethod) {
      case 'BANK_TRANSFER':
        this.router.navigate(['../bank-transfer'], { relativeTo: this.route });
        break;
      case 'CREDIT_OR_DEBIT_CARD':
        this.store
          .pipe(
            first(),
            switchMap((state) => this.store.postCreateCardPayment(state.requestTaskId)),
            this.pendingRequest.trackRequest(),
          )
          .subscribe((res) => {
            if (res.pendingPaymentExist) {
              this.router.navigate(['../confirmation'], {
                relativeTo: this.route,
                queryParams: { method: 'CREDIT_OR_DEBIT_CARD' },
              });
            } else {
              window.location.assign(res.nextUrl);
            }
          });
        break;
    }
  }
}
