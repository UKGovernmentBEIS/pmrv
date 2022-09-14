import { NgModule } from '@angular/core';

import { SharedModule } from '@shared/shared.module';

import { CancelledComponent } from './cancelled/cancelled.component';
import { CompletedComponent } from './completed/completed.component';
import { BankTransferComponent } from './make/bank-transfer/bank-transfer.component';
import { ConfirmationComponent } from './make/confirmation/confirmation.component';
import { DetailsComponent } from './make/details/details.component';
import { MarkPaidComponent } from './make/mark-paid/mark-paid.component';
import { NotSuccessComponent } from './make/not-success/not-success.component';
import { OptionsComponent } from './make/options/options.component';
import { PaidComponent } from './paid/paid.component';
import { PaymentRoutingModule } from './payment-routing.module';
import { ReceivedComponent } from './received/received.component';
import { PaymentSummaryComponent } from './shared/components/payment-summary/payment-summary.component';
import { ReturnLinkComponent } from './shared/components/return-link/return-link.component';
import { PaymentMethodDescriptionPipe } from './shared/pipes/payment-method-description.pipe';
import { PaymentStatusPipe } from './shared/pipes/payment-status.pipe';
import { CancelComponent } from './track/cancel/cancel.component';
import { MarkPaidComponent as TrackMarkPaidComponent } from './track/mark-paid/mark-paid.component';
import { TrackComponent } from './track/track.component';

@NgModule({
  declarations: [
    BankTransferComponent,
    CancelComponent,
    CancelledComponent,
    CompletedComponent,
    ConfirmationComponent,
    DetailsComponent,
    MarkPaidComponent,
    NotSuccessComponent,
    OptionsComponent,
    PaidComponent,
    PaymentMethodDescriptionPipe,
    PaymentStatusPipe,
    PaymentSummaryComponent,
    ReceivedComponent,
    ReturnLinkComponent,
    TrackComponent,
    TrackMarkPaidComponent,
  ],
  imports: [PaymentRoutingModule, SharedModule],
})
export class PaymentModule {}
